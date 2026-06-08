/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.isi.completion

import com.intellij.codeInsight.AutoPopupController
import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.JBColor
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import org.pcsoft.intellij.plugin.inno_setup.services.IssLanguageService
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.*
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.IsiSyntaxHighlighting
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.*
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.language.issFile
import org.pcsoft.intellij.plugin.inno_setup.language.languageId
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.settings.IssSettingsService
import org.pcsoft.intellij.plugin.inno_setup.types.IsiFlagTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsiNativeTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsiReferenceTypeSpec

class IsiCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsiTypes.IDENTIFIER)
                .afterLeaf(PlatformPatterns.psiElement(IsiTypes.LBRACKET)),
            SectionNameProvider
        )
        // Attribute key completion for all IDENTIFIER positions in ISS files.
        // The provider itself decides whether the cursor is in a key position
        // (IsiParamKey, IsiDirectiveKey, or an orphaned token on an empty line).
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsiTypes.IDENTIFIER)
                .inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            AttributeKeyProvider
        )
        // Key completion for internationalized sections ([Messages], [CustomMessages]):
        // offers a language-prefix list (flag + name) plus the known message identifiers,
        // and handles the embedded "lang." prefix.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsiTypes.IDENTIFIER)
                .inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            MessagesKeyProvider
        )
        // Declared custom-message suggestions inside the {cm:…} constant.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            CustomMessageAfterCmProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            IsiConstantCompletionProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            IsppVariableAfterHashProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            BooleanValueProvider
        )
        // Cross-section reference completion: Tasks: <name>, Components: <name>, etc.
        // ReferenceBasedCompletionContributor does not fire for ISS because the reference
        // lives on IsiParamValue (parent), not the leaf IDENTIFIER. This provider reads
        // IsiReference.getVariants() explicitly for any reference-typed param value.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsiTypes.IDENTIFIER)
                .inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            SectionReferenceValueProvider
        )
        // Built-in language suggestions for [Languages] Name and MessagesFile parameters.
        // Registered for any element (not just IDENTIFIER) so it also fires inside quoted strings.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            LanguageSectionValueProvider
        )
        // Windows language identifier suggestions for the [LangOptions] LanguageID directive.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            LanguageIdValueProvider
        )
    }
}

private object SectionNameProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val file = parameters.originalFile as? IssFile ?: return
        val specSections = service<IssSpecService>().spec.sections

        val existingNames = file.sections()
            .map { it.nameText().lowercase() }
            .toSet()

        val minVersion = IssSettingsService.getInstance().state.minInnoVersion
        specSections.forEach { specSection ->
            val duplicate = specSection.name.lowercase() in existingNames
            val tooNew = minVersion != null && specSection.since != null &&
                    IssSettingsService.compareIsVersions(specSection.since, minVersion) > 0
            val removed = minVersion != null && specSection.until != null &&
                    IssSettingsService.compareIsVersions(specSection.until, minVersion) <= 0
            val tailText = buildString {
                if (specSection.deprecated) append(" (deprecated)")
                if (removed) append(" [removed IS ${specSection.until}]")
                else if (tooNew) append(" [IS ${specSection.since}+]")
            }
            val element = LookupElementBuilder
                .create(specSection.name)
                .withTypeText(specSection.type)
                .withTailText(tailText, true)
                .withItemTextForeground(when {
                    duplicate -> JBColor.RED
                    removed -> JBColor.GRAY
                    tooNew -> JBColor.ORANGE
                    else -> JBColor.foreground()
                })
                .withInsertHandler { ctx, _ ->
                    val tail = ctx.tailOffset
                    val chars = ctx.document.charsSequence
                    // If ] is already there (e.g. auto-paired by the IDE), skip past it.
                    if (tail < chars.length && chars[tail] == ']') {
                        ctx.document.insertString(tail + 1, "\n")
                        ctx.editor.caretModel.moveToOffset(tail + 2)
                    } else {
                        ctx.document.insertString(tail, "]\n")
                        ctx.editor.caretModel.moveToOffset(tail + 2)
                    }
                }
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    element,
                    when {
                        duplicate -> -10.0
                        removed -> -20.0
                        tooNew -> -5.0
                        else -> 0.0
                    }
                )
            )
        }
    }
}

private object AttributeKeyProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        if (parameters.position.isInCodeSection()) return
        val position = parameters.position

        // Only suggest attribute keys in key positions:
        //  • parent is IsiParamKey / IsiDirectiveKey → user is editing an existing key
        //  • not inside any entry at all → user is on an empty / partial line
        // Anything else (inside a value) is skipped.
        val inKeyPosition = position.parent is IsiParamKey
                || position.parent is IsiDirectiveKey
                || (position.containingParameterEntry() == null
                && position.containingDirectiveEntry() == null)
        if (!inKeyPosition) return

        // When typing on an empty line, or after a dangling ';' on a parameter
        // line, the dummy IDENTIFIER lands outside the section (the entry* loop
        // exits before consuming it). Fall back to the element at the same offset
        // in the original file, and finally to an offset-based section lookup,
        // which works even when the trailing tokens are outside any section.
        val originalFile = parameters.originalFile as? IssFile
        val psiSection = position.containingSection()
            ?: parameters.originalPosition?.containingSection()
            ?: originalFile?.sectionAtOffset(parameters.offset)
            ?: return
        val sectionName = psiSection.nameText()

        val specSections = service<IssSpecService>().spec.sections
        val specSection = specSections.firstOrNull {
            it.name.equals(sectionName, ignoreCase = true)
        } ?: return

        // Sections that support a language prefix (e.g. [Messages], [CustomMessages]) are
        // handled by MessagesKeyProvider, which also offers the language-prefix list and copes
        // with the embedded "lang." prefix. Skip them here to avoid duplicate suggestions.
        if (specSection.internationalization) return

        // Directive keys are unique per section; parameter keys are unique per
        // line (entry). So for parameter sections, only the keys already present
        // on the current line count as duplicates — not the whole section.
        val usedKeys = if (specSection.type == "directive") {
            psiSection.directiveEntryList.map { it.directiveKey.text.trim().lowercase() }.toSet()
        } else {
            val document = parameters.editor.document
            val entry = position.containingParameterEntry()
                ?: parameters.originalPosition?.containingParameterEntry()
                ?: psiSection.parameterEntryOnLineOf(parameters.offset, document)
            entry?.paramPairList?.map { it.keyText().lowercase() }?.toSet().orEmpty()
        }

        val minVersion = IssSettingsService.getInstance().state.minInnoVersion
        specSection.attributes.forEach { attr ->
            val duplicate = attr.name.lowercase() in usedKeys
            val tooNew = minVersion != null && attr.since != null &&
                    IssSettingsService.compareIsVersions(attr.since, minVersion) > 0
            val removed = minVersion != null && attr.until != null &&
                    IssSettingsService.compareIsVersions(attr.until, minVersion) <= 0
            val typeHint = when (val t = attr.type) {
                is IsiNativeTypeSpec -> t.dataType
                is IsiReferenceTypeSpec -> "→ ${t.section}"
                is IsiFlagTypeSpec -> "flags"
            }
            val tail = buildString {
                if (attr.required) append(" required")
                if (attr.deprecated) append(" deprecated")
                if (attr.array) append("[]")
                if (removed) append(" [removed IS ${attr.until}]")
                else if (tooNew) append(" [IS ${attr.since}+]")
            }
            val separator = if (specSection.type == "directive") "=" else ": "
            val foreground = when {
                duplicate -> JBColor.RED
                removed -> JBColor.GRAY
                tooNew -> JBColor.ORANGE
                else -> JBColor.foreground()
            }

            val element = LookupElementBuilder
                .create(attr.name)
                .withTypeText(typeHint)
                .withTailText(tail, true)
                .withItemTextForeground(foreground)
                .withBoldness(attr.required)
                .withInsertHandler { ctx, _ ->
                    ctx.document.insertString(ctx.tailOffset, separator)
                    ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                }
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    element,
                    when {
                        duplicate -> -10.0
                        removed -> -20.0
                        tooNew -> -5.0
                        else -> 0.0
                    }
                )
            )
        }
    }
}

/**
 * Key completion for internationalized sections ([Messages], [CustomMessages], driven by the
 * spec's `internationalization` flag). Offers, at a key position:
 *  • a language-prefix list (flag icon + language name) that inserts `lang.` and re-opens the
 *    popup, and
 *  • the section's known message identifiers (empty for [CustomMessages]) that insert `name=`.
 * When a `lang.` prefix is already typed, only the message identifiers are offered, matched
 * against the text after the dot.
 */
private object MessagesKeyProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        if (position.isInCodeSection()) return

        val inKeyPosition = position.parent is IsiDirectiveKey
                || (position.containingParameterEntry() == null
                && position.containingDirectiveEntry() == null)
        if (!inKeyPosition) return

        val originalFile = parameters.originalFile as? IssFile
        val psiSection = position.containingSection()
            ?: parameters.originalPosition?.containingSection()
            ?: originalFile?.sectionAtOffset(parameters.offset)
            ?: return
        val specSection = service<IssSpecService>().spec.sections.firstOrNull {
            it.name.equals(psiSection.nameText(), ignoreCase = true)
        } ?: return
        if (!specSection.internationalization) return

        val file = originalFile ?: position.issFile() ?: return

        // Typed text of the key so far (strip the dummy completion identifier).
        val raw = position.text
        val dummyIdx = raw.indexOf(CompletionUtil.DUMMY_IDENTIFIER_TRIMMED)
        val typed = if (dummyIdx >= 0) raw.substring(0, dummyIdx) else raw
        val dotIdx = typed.indexOf('.')

        if (dotIdx >= 0) {
            // A language prefix is already present — only complete message identifiers,
            // matched against the part after the dot.
            val afterDot = result.withPrefixMatcher(typed.substring(dotIdx + 1))
            addMessageIdentifiers(specSection, afterDot)
            return
        }

        // No dot yet: offer language prefixes (flag + name) and message identifiers.
        languagePrefixSources(file).forEach { (name, displayName, icon) ->
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(name)
                        .withTypeText(displayName)
                        .withIcon(icon)
                        .withInsertHandler { ctx, _ ->
                            ctx.document.insertString(ctx.tailOffset, ".")
                            ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                            AutoPopupController.getInstance(ctx.project).scheduleAutoPopup(ctx.editor)
                        },
                    20.0
                )
            )
        }
        addMessageIdentifiers(specSection, result)
    }

    private fun addMessageIdentifiers(
        specSection: org.pcsoft.intellij.plugin.inno_setup.types.IsiSectionSpec,
        result: CompletionResultSet
    ) {
        specSection.attributes.forEach { attr ->
            val tail = buildString {
                if (attr.deprecated) append(" deprecated")
            }
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(attr.name)
                        .withTypeText("message")
                        .withTailText(tail, true)
                        .withItemTextForeground(
                            if (attr.deprecated) JBColor.GRAY else JBColor.foreground()
                        )
                        .withInsertHandler { ctx, _ ->
                            ctx.document.insertString(ctx.tailOffset, "=")
                            ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                        },
                    0.0
                )
            )
        }
    }

    /**
     * Languages offered as a key prefix: the `Name` values declared in the file's [Languages]
     * section (with the matching flag + display name), falling back to all built-in languages
     * when the file declares none.
     */
    /**
     * Prefix sources are the `Name` values declared in the file's [Languages] section; flag and
     * English name are derived from each entry's MessagesFile → LanguageID (the single source of
     * truth). No [Languages] section ⇒ no prefix suggestions.
     */
    private fun languagePrefixSources(file: IssFile): List<Triple<String, String, javax.swing.Icon>> =
        file.findSections("Languages")
            .flatMap { it.nameDeclarations() }
            .mapNotNull { pair ->
                val name = pair.valueUnquoted().ifEmpty { null } ?: return@mapNotNull null
                val messagesFile = pair.containingParameterEntry()?.paramPairList
                    ?.firstOrNull { it.keyText().equals("MessagesFile", ignoreCase = true) }
                    ?.valueUnquoted()
                val lang = messagesFile
                    ?.let { file.languageId(it) }
                    ?.let { service<IssLanguageService>().fromId(it) }
                Triple(name, lang?.displayName ?: name, lang?.icon ?: AllIcons.General.Web)
            }
            .distinctBy { it.first.lowercase() }
}

/**
 * Completion of declared custom-message names inside the {cm:…} constant. Looks back from the
 * caret for the nearest `{cm:` and offers the message names declared in the file's
 * [CustomMessages] section(s).
 */
private object CustomMessageAfterCmProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val offset = parameters.offset
        val chars = parameters.editor.document.charsSequence
        val lookBack = minOf(offset, 100)
        val prefix = chars.subSequence(offset - lookBack, offset).toString()
        val braceIdx = prefix.lastIndexOf('{')
        if (braceIdx < 0) return
        val afterBrace = prefix.substring(braceIdx + 1)
        if (!afterBrace.regionMatches(0, "cm:", 0, 3, ignoreCase = true)) return
        val afterColon = afterBrace.substring(3)
        // Past the name (into the printf-style arguments) — nothing to complete.
        if (afterColon.contains(',')) return

        val file = parameters.originalFile as? IssFile ?: return
        val adjusted = result.withPrefixMatcher(afterColon)

        customMessageNames(file).forEach { name ->
            adjusted.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(name)
                        .withTypeText("custom message")
                        .withIcon(IssIcons.Constant)
                        .withInsertHandler { ctx, _ ->
                            val tail = ctx.tailOffset
                            val doc = ctx.document.charsSequence
                            if (tail >= doc.length || doc[tail] != '}')
                                ctx.document.insertString(tail, "}")
                            ctx.editor.caretModel.moveToOffset(tail + 1)
                        },
                    10.0
                )
            )
        }
    }
}

/** Distinct custom-message names declared in the file's [CustomMessages] section(s), with any
 *  `lang.` prefix stripped. */
private fun customMessageNames(file: IssFile): List<String> =
    file.findSections("CustomMessages")
        .flatMap { it.directiveEntryList }
        .map { it.keyText().substringAfterLast('.') }
        .filter { it.isNotEmpty() }
        .distinct()

private object IsppVariableAfterHashProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val offset = parameters.offset
        val chars = parameters.editor.document.charsSequence
        val lookBack = minOf(offset, 100)
        val prefix = chars.subSequence(offset - lookBack, offset).toString()
        val braceIdx = prefix.lastIndexOf('{')
        if (braceIdx < 0) return
        val afterBrace = prefix.substring(braceIdx + 1)
        if (!afterBrace.startsWith('#')) return
        val typedName = afterBrace.substring(1)

        val file = parameters.originalFile as? IssFile ?: return
        val adjusted = result.withPrefixMatcher(typedName)

        file.definedConstants().forEach { (name, value) ->
            adjusted.addElement(
                LookupElementBuilder.create(name)
                    .withTypeText(value?.let { "= $it" } ?: "define")
                    .withIcon(IssIcons.Variable)
                    .withInsertHandler { ctx, _ ->
                        val tail = ctx.tailOffset
                        val doc = ctx.document.charsSequence
                        if (tail >= doc.length || doc[tail] != '}')
                            ctx.document.insertString(tail, "}")
                        ctx.editor.caretModel.moveToOffset(tail + 1)
                    }
            )
        }
    }
}

private object BooleanValueProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        if (position.isInCodeSection()) return
        val paramValue = PsiTreeUtil.getParentOfType(position, IsiParamValue::class.java) ?: return

        val spec = service<IssSpecService>().spec
        val attr = run {
            val pair = paramValue.containingParamPair()
            if (pair != null) {
                val ss = pair.containingSection()?.specSection(spec) ?: return
                ss.attributes.firstOrNull { it.name.equals(pair.keyText(), ignoreCase = true) }
            } else {
                val dir = paramValue.containingDirectiveEntry() ?: return
                val ss = dir.containingSection()?.specSection(spec) ?: return
                ss.attributes.firstOrNull { it.name.equals(dir.keyText(), ignoreCase = true) }
            }
        } ?: return

        val type = attr.type as? IsiNativeTypeSpec ?: return
        if (type.dataType.lowercase() != "boolean") return

        listOf("yes", "no").forEach { value ->
            result.addElement(
                PrioritizedLookupElement.withPriority(keywordLookupElement(value), 20.0)
            )
        }
    }
}

private fun keywordLookupElement(value: String): LookupElement = object : LookupElement() {
    override fun getLookupString() = value
    override fun renderElement(presentation: LookupElementPresentation) {
        presentation.itemText = value
        presentation.isItemTextBold = true
        presentation.typeText = "boolean"
        val attrs = EditorColorsManager.getInstance().globalScheme
            .getAttributes(IsiSyntaxHighlighting.KEYWORD)
        presentation.itemTextForeground = attrs?.foregroundColor ?: JBColor.foreground()
    }
}

private object SectionReferenceValueProvider : CompletionProvider<CompletionParameters>() {
    private val KEY_TO_SECTION = mapOf(
        "tasks" to "Tasks",
        "components" to "Components",
        "types" to "Types",
        "languages" to "Languages",
    )

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        if (position.isInCodeSection()) return
        val paramValue = PsiTreeUtil.getParentOfType(position, IsiParamValue::class.java) ?: return
        val pair = paramValue.containingParamPair() as? IsiParamPairEx ?: return
        val targetSection = KEY_TO_SECTION[pair.keyText().lowercase()] ?: return
        val file = paramValue.issFile() ?: return
        file.findSections(targetSection)
            .flatMap { it.nameDeclarations() }
            .mapNotNull { it.valueUnquoted().ifEmpty { null } }
            .forEach { name -> result.addElement(LookupElementBuilder.create(name)) }
    }
}

private object LanguageSectionValueProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        if (position.isInCodeSection()) return
        val paramValue = PsiTreeUtil.getParentOfType(position, IsiParamValue::class.java) ?: return
        val pair = paramValue.containingParamPair() ?: return
        val section = pair.containingSection() ?: return
        if (!section.nameText().equals("Languages", ignoreCase = true)) return

        // STRING_PART tokens inside IsiQuotedString don't get automatic prefix computation,
        // so strip the dummy identifier manually to set the correct prefix.
        val adjustedResult = if (PsiTreeUtil.getParentOfType(position, IsiQuotedString::class.java) != null) {
            val tokenText = position.text
            val dummyIdx = tokenText.indexOf(CompletionUtil.DUMMY_IDENTIFIER_TRIMMED)
            val typed = if (dummyIdx >= 0) tokenText.substring(0, dummyIdx) else tokenText
            result.withPrefixMatcher(typed)
        } else result

        val builtin = service<IssLanguageService>().builtinLanguages
        val key = pair.keyText()
        when {
            key.equals("Name", ignoreCase = true) ->
                builtin.forEach { lang ->
                    val issName = lang.issName ?: return@forEach
                    adjustedResult.addElement(
                        PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(issName)
                                .withTypeText(lang.displayName)
                                .withIcon(lang.icon),
                            10.0
                        )
                    )
                }
            key.equals("MessagesFile", ignoreCase = true) ->
                builtin.forEach { lang ->
                    val messagesFile = lang.messagesFile ?: return@forEach
                    adjustedResult.addElement(
                        PrioritizedLookupElement.withPriority(
                            LookupElementBuilder.create(messagesFile)
                                .withTypeText(lang.displayName)
                                .withIcon(lang.icon),
                            10.0
                        )
                    )
                }
        }
    }
}

private object LanguageIdValueProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val position = parameters.position
        if (position.isInCodeSection()) return
        // [LangOptions] is a directive section (Key=Value), so the value hangs off an
        // IsiDirectiveEntry rather than an IsiParamPair.
        val paramValue = PsiTreeUtil.getParentOfType(position, IsiParamValue::class.java) ?: return
        val directive = paramValue.containingDirectiveEntry() ?: return
        val section = directive.containingSection() ?: return
        if (!section.nameText().equals("LangOptions", ignoreCase = true)) return
        if (!directive.keyText().equals("LanguageID", ignoreCase = true)) return

        service<IssLanguageService>().entries.forEach { lang ->
            val tail = if (lang.builtin) "  Inno built-in" else ""
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create(lang.id)        // inserts the "$0409" hex id
                        .withLookupString(lang.displayName)     // also matchable by typing the language name
                        .withPresentableText(lang.displayName)  // shown label, e.g. "English (United States)"
                        .withTailText(tail, true)               // mark Inno-bundled languages
                        .withTypeText(lang.id, true)            // greyed id on the right
                        .withIcon(lang.icon),
                    // Built-in languages are sorted to the top of the popup.
                    if (lang.builtin) 20.0 else 10.0
                )
            )
        }
    }
}


