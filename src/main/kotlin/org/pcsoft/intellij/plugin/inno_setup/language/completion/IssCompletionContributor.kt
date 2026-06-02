package org.pcsoft.intellij.plugin.inno_setup.language.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import com.intellij.patterns.PlatformPatterns
import com.intellij.ui.JBColor
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.*
import org.pcsoft.intellij.plugin.inno_setup.language.psi.IssDirectiveKey
import org.pcsoft.intellij.plugin.inno_setup.language.psi.IssParamKey
import org.pcsoft.intellij.plugin.inno_setup.language.psi.IssPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.psi.IssTypes
import org.pcsoft.intellij.plugin.inno_setup.services.IssIsppService
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.IssFlagType
import org.pcsoft.intellij.plugin.inno_setup.types.IssNativeType
import org.pcsoft.intellij.plugin.inno_setup.types.IssReferenceType

class IssCompletionContributor : CompletionContributor() {
    init {
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IssTypes.IDENTIFIER)
                .afterLeaf(PlatformPatterns.psiElement(IssTypes.LBRACKET)),
            SectionNameProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IssTypes.IDENTIFIER)
                .withParent(IssParamKey::class.java),
            AttributeKeyProvider
        )
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IssTypes.IDENTIFIER)
                .withParent(IssDirectiveKey::class.java),
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
        val position    = parameters.position
        val psiSection  = position.containingSection() ?: return
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
                is IssNativeType    -> t.dataType
                is IssReferenceType -> "→ ${t.section}"
                is IssFlagType      -> "flags"
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

