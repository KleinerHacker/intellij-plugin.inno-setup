package org.pcsoft.intellij.plugin.inno_setup.language.isi.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.codeInsight.lookup.LookupElementPresentation
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.EditorColorsManager
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.JBColor
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.*
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.IsiSyntaxHighlighting
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.*
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.language.issFile
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.IssFlagTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IssNativeTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IssReferenceTypeSpec

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

        specSections.forEach { specSection ->
            val duplicate = specSection.name.lowercase() in existingNames
            val element = LookupElementBuilder
                .create(specSection.name)
                .withTypeText(specSection.type)
                .withTailText(if (specSection.deprecated) " (deprecated)" else "", true)
                .withItemTextForeground(if (duplicate) JBColor.RED else JBColor.foreground())
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
                PrioritizedLookupElement.withPriority(element, if (duplicate) -10.0 else 0.0)
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

        specSection.attributes.forEach { attr ->
            val duplicate = attr.name.lowercase() in usedKeys
            val typeHint = when (val t = attr.type) {
                is IssNativeTypeSpec -> t.dataType
                is IssReferenceTypeSpec -> "→ ${t.section}"
                is IssFlagTypeSpec -> "flags"
            }
            val tail = buildString {
                if (attr.required) append(" required")
                if (attr.deprecated) append(" deprecated")
                if (attr.array) append("[]")
            }
            val separator = if (specSection.type == "directive") "=" else ": "

            val element = LookupElementBuilder
                .create(attr.name)
                .withTypeText(typeHint)
                .withTailText(tail, true)
                .withItemTextForeground(if (duplicate) JBColor.RED else JBColor.foreground())
                .withBoldness(attr.required)
                .withInsertHandler { ctx, _ ->
                    ctx.document.insertString(ctx.tailOffset, separator)
                    ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                }
            result.addElement(
                PrioritizedLookupElement.withPriority(element, if (duplicate) -10.0 else 0.0)
            )
        }
    }
}

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

        val type = attr.type as? IssNativeTypeSpec ?: return
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


