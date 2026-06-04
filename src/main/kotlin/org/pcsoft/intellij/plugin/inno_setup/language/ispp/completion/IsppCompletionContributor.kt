package org.pcsoft.intellij.plugin.inno_setup.language.ispp.completion

import com.intellij.codeInsight.completion.*
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.openapi.components.service
import com.intellij.patterns.PlatformPatterns
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirective
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppTypes
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.impl.IsppDirectiveMixinImpl
import org.pcsoft.intellij.plugin.inno_setup.services.IssIsppService

class IsppCompletionContributor : CompletionContributor() {
    init {
        // Directive keyword after #
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement(IsppTypes.IDENTIFIER)
                .afterLeaf(PlatformPatterns.psiElement(IsppTypes.HASH))
                .withParent(IsppDirective::class.java),
            IsppDirectiveKeywordProvider
        )
        // Type qualifier after #define (int/str/float/...)
        extend(
            CompletionType.BASIC,
            PlatformPatterns.psiElement()
                .withLanguage(org.pcsoft.intellij.plugin.inno_setup.language.ispp.IsppLanguage),
            IsppDefineTypeProvider
        )
    }
}

private object IsppDirectiveKeywordProvider : CompletionProvider<CompletionParameters>() {
    override fun addCompletions(
        params: CompletionParameters,
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

private object IsppDefineTypeProvider : CompletionProvider<CompletionParameters>() {
    private val LINE_PATTERN = Regex("^#\\s*define\\s+([A-Za-z0-9_.\\-]*)$")

    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val offset    = params.offset
        val doc       = params.editor.document
        val lineStart = doc.getLineStartOffset(doc.getLineNumber(offset))
        val linePrefix = doc.charsSequence.subSequence(lineStart, offset).toString()
        val typed = LINE_PATTERN.find(linePrefix)?.groupValues?.get(1) ?: return

        val adjusted = result.withPrefixMatcher(typed)
        IsppDirectiveMixinImpl.TYPE_KEYWORDS.forEach { keyword ->
            adjusted.addElement(
                LookupElementBuilder.create(keyword)
                    .withTypeText("type")
                    .withIcon(IssIcons.Constant)
                    .withInsertHandler { ctx, _ ->
                        ctx.document.insertString(ctx.tailOffset, " ")
                        ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                    }
            )
        }
    }
}
