package org.pcsoft.intellij.plugin.inno_setup.language.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import com.intellij.patterns.PlatformPatterns
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.ui.JBColor
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.*
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssDirectiveKey
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamKey
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamPairEx
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssParamValue
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssTypes
import org.pcsoft.intellij.plugin.inno_setup.language.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.services.IssIsppService
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.IssFlagTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IssNativeTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IssReferenceTypeSpec

class IssCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IssTypes.IDENTIFIER)
                .afterLeaf(PlatformPatterns.psiElement(IssTypes.LBRACKET)),
            SectionNameProvider
        )
        // Attribute key completion for all IDENTIFIER positions in ISS files.
        // The provider itself decides whether the cursor is in a key position
        // (IssParamKey, IssDirectiveKey, or an orphaned token on an empty line).
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IssTypes.IDENTIFIER)
                .inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            AttributeKeyProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            IssConstantCompletionProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IssTypes.IDENTIFIER)
                .afterLeaf(PlatformPatterns.psiElement(IssTypes.HASH))
                .withParent(IssPreprocessorDirective::class.java),
            IssIsppDirectiveProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement().inFile(PlatformPatterns.psiFile(IssFile::class.java)),
            IsppVariableAfterHashProvider
        )
        // Cross-section reference completion: Tasks: <name>, Components: <name>, etc.
        // ReferenceBasedCompletionContributor does not fire for ISS because the reference
        // lives on IssParamValue (parent), not the leaf IDENTIFIER. This provider reads
        // IssReference.getVariants() explicitly for any reference-typed param value.
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IssTypes.IDENTIFIER)
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
        val file         = parameters.originalFile as? IssFile ?: return
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
                    ctx.document.insertString(ctx.tailOffset, "]\n")
                    ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
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
        //  • parent is IssParamKey / IssDirectiveKey → user is editing an existing key
        //  • not inside any entry at all → user is on an empty / partial line
        // Anything else (inside a value) is skipped.
        val inKeyPosition = position.parent is IssParamKey
            || position.parent is IssDirectiveKey
            || (position.containingParameterEntry() == null
                && position.containingDirectiveEntry() == null)
        if (!inKeyPosition) return

        // When typing on an empty line the dummy IDENTIFIER lands outside the
        // section (the entry* loop exits before consuming it). Fall back to the
        // element at the same offset in the original file, which IS inside the
        // section (it's a CRLF consumed by the entry* loop).
        val psiSection = position.containingSection()
            ?: parameters.originalPosition?.containingSection()
            ?: return
        val sectionName = psiSection.nameText()

        val specSections = service<IssSpecService>().spec.sections
        val specSection  = specSections.firstOrNull {
            it.name.equals(sectionName, ignoreCase = true)
        } ?: return

        val usedKeys = (
            psiSection.allParamPairs().map { it.keyText().lowercase() } +
            psiSection.directiveEntryList.map { it.directiveKey.text.trim().lowercase() }
        ).toSet()

        specSection.attributes.forEach { attr ->
            val duplicate = attr.name.lowercase() in usedKeys
            val typeHint = when (val t = attr.type) {
                is IssNativeTypeSpec    -> t.dataType
                is IssReferenceTypeSpec -> "→ ${t.section}"
                is IssFlagTypeSpec      -> "flags"
            }
            val tail = buildString {
                if (attr.required)   append(" required")
                if (attr.deprecated) append(" deprecated")
                if (attr.array)      append("[]")
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
        val chars  = parameters.editor.document.charsSequence
        val lookBack = minOf(offset, 100)
        val prefix   = chars.subSequence(offset - lookBack, offset).toString()
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
                        val doc  = ctx.document.charsSequence
                        if (tail >= doc.length || doc[tail] != '}')
                            ctx.document.insertString(tail, "}")
                        ctx.editor.caretModel.moveToOffset(tail + 1)
                    }
            )
        }
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
        val paramValue = PsiTreeUtil.getParentOfType(position, IssParamValue::class.java) ?: return
        val pair = paramValue.containingParamPair() as? IssParamPairEx ?: return
        val targetSection = KEY_TO_SECTION[pair.keyText().lowercase()] ?: return
        val file = paramValue.issFile() ?: return
        file.findSections(targetSection)
            .flatMap { it.nameDeclarations() }
            .mapNotNull { it.valueUnquoted().ifEmpty { null } }
            .forEach { name -> result.addElement(LookupElementBuilder.create(name)) }
    }
}

private object IssIsppDirectiveProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        service<IssIsppService>().spec.directives
            .distinctBy { it.name }
            .forEach { dir ->
                result.addElement(
                    LookupElementBuilder.create(dir.name)
                        .withTypeText("ISPP")
                        .withTailText("  ${dir.syntax}", true)
                        .withIcon(IssIcons.Constant)
                        .withInsertHandler { ctx, _ ->
                            ctx.document.insertString(ctx.tailOffset, " ")
                            ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                        }
                )
            }
    }
}

