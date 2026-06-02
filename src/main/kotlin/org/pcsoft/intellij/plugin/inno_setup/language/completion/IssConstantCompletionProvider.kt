package org.pcsoft.intellij.plugin.inno_setup.language.completion

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.completion.PrioritizedLookupElement
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.services.IssConstantService
import org.pcsoft.intellij.plugin.inno_setup.services.IssIsppService

object IssConstantCompletionProvider : CompletionProvider<CompletionParameters>() {

    override fun addCompletions(
        parameters: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val offset = parameters.offset
        val chars  = parameters.editor.document.charsSequence
        if (offset == 0 || chars[offset - 1] != '{') return

        val file     = parameters.originalFile as? IssFile ?: return
        val builtins = service<IssConstantService>().spec.constants
        val userDefs = file.definedConstants()

        builtins.forEach { const ->
            val tail = if (const.parameterized) " (${const.syntax ?: "parameterized"})" else ""
            val element = LookupElementBuilder
                .create(if (const.parameterized) "${const.name}:" else const.name)
                .withTypeText(const.category.name.lowercase().replace('_', ' '))
                .withTailText(tail, true)
                .withIcon(AllIcons.Nodes.Static)
                .withInsertHandler { ctx, _ ->
                    if (!const.parameterized)
                        ctx.document.insertString(ctx.tailOffset, "}")
                    ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                }
            result.addElement(PrioritizedLookupElement.withPriority(element, 10.0))
        }

        userDefs.forEach { (name, value) ->
            val element = LookupElementBuilder
                .create("#$name")
                .withTypeText("define")
                .withTailText(value?.let { " = $it" } ?: "", true)
                .withIcon(AllIcons.Nodes.Variable)
                .withInsertHandler { ctx, _ ->
                    ctx.document.insertString(ctx.tailOffset, "}")
                    ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                }
            result.addElement(PrioritizedLookupElement.withPriority(element, 5.0))
        }

        service<IssIsppService>().spec.predefinedVariables.forEach { v ->
            result.addElement(
                PrioritizedLookupElement.withPriority(
                    LookupElementBuilder.create("#${v.name}")
                        .withTypeText("${v.type} · ISPP")
                        .withIcon(AllIcons.Nodes.Variable)
                        .withInsertHandler { ctx, _ ->
                            ctx.document.insertString(ctx.tailOffset, "}")
                            ctx.editor.caretModel.moveToOffset(ctx.tailOffset)
                        },
                    3.0
                )
            )
        }
    }
}
