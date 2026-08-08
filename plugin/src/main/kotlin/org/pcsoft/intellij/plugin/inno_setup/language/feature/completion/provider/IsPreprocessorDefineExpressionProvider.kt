/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider

import com.intellij.codeInsight.completion.CompletionParameters
import com.intellij.codeInsight.completion.CompletionProvider
import com.intellij.codeInsight.completion.CompletionResultSet
import com.intellij.codeInsight.lookup.LookupElementBuilder
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.components.service
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.util.ProcessingContext
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.externalPreprocessorSymbols
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.isppDirectivesWithHostOffset
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile

/**
 * Inside a `#define` expression, suggest the names of all `#define`s declared on an earlier line, plus the
 * predefined ISPP variables (e.g. `__LINE__`, `PREPROCVER`) which are always referenceable.
 * Declaration order is enforced via host offsets, so neither the current define nor later ones appear.
 *
 * Built-in functions are contributed separately by [IsPreprocessorBuiltinFunctionProvider].
 */
object IsPreprocessorDefineExpressionProvider : CompletionProvider<CompletionParameters>() {

    /**
     * Adds lookup elements for the current completion request.
     */
    override fun addCompletions(
        params: CompletionParameters,
        context: ProcessingContext,
        result: CompletionResultSet
    ) {
        val adjusted = IsPreprocessorExpressionContext.adjustedResult(params, result) ?: return

        val position = params.position
        val injMgr = InjectedLanguageManager.getInstance(position.project)
        val hostFile = injMgr.getTopLevelFile(position.containingFile) as? IsScriptFile ?: return
        val host = injMgr.getInjectionHost(position) ?: return
        val lineOffset = host.textRange.startOffset

        val precedingDefines = hostFile.isppDirectivesWithHostOffset
            .filter { (d, off) ->
                off < lineOffset && (d as? IsPreprocessorDirectiveEx)?.isDefine() == true && !d.isArrayElementDefine()
            }
            .mapNotNull { it.first as? IsPreprocessorDirectiveEx }
            .filter { !it.getDefineName().isNullOrEmpty() }
            .distinctBy { it.getDefineName() }

        precedingDefines.forEach { dex ->
            val name = dex.getDefineName()!!
            // A function-like macro (#define f(x) …) is presented like a built-in function: function icon
            // plus its parameter list, so it is visually distinct from a plain value define.
            val element = if (dex.isFunctionMacro()) {
                LookupElementBuilder.create(name)
                    .withTailText(
                        "(${dex.getMacroParameterDeclarations().joinToString(", ") { it.presentation }})",
                        true
                    )
                    .withTypeText("macro")
                    .withIcon(IsIcons.Function)
            } else {
                LookupElementBuilder.create(name)
                    .withTypeText("define")
                    .withIcon(IsIcons.Variable)
            }
            adjusted.addElement(element)
        }

        // Array names declared by an earlier #dim — offered with a trailing `[]` so the index can be typed.
        hostFile.isppDirectivesWithHostOffset
            .filter { (d, off) -> off < lineOffset && (d as? IsPreprocessorDirectiveEx)?.isDim() == true }
            .mapNotNull { it.first as? IsPreprocessorDirectiveEx }
            .filter { !it.getArrayName().isNullOrEmpty() }
            .distinctBy { it.getArrayName() }
            .forEach { dex ->
                adjusted.addElement(
                    LookupElementBuilder.create(dex.getArrayName()!!)
                        .withTailText("[]", true)
                        .withTypeText("array")
                        .withIcon(IsIcons.Variable)
                )
            }

        // #sub subroutine names declared on an earlier line — a subroutine may *only* be called as a #for body,
        // so its name is offered exclusively in that slot (never in a #define/#if/… expression).
        if (IsPreprocessorExpressionContext.isForBodyContext(params)) {
            hostFile.isppDirectivesWithHostOffset
                .filter { (d, off) -> off < lineOffset && (d as? IsPreprocessorDirectiveEx)?.isSub() == true }
                .mapNotNull { it.first as? IsPreprocessorDirectiveEx }
                .filter { !it.getSubroutineName().isNullOrEmpty() }
                .distinctBy { it.getSubroutineName() }
                .forEach { dex ->
                    adjusted.addElement(
                        LookupElementBuilder.create(dex.getSubroutineName()!!)
                            .withTypeText("sub")
                            .withIcon(IsIcons.Function)
                    )
                }
        }

        // The parameters of the enclosing function-like macro are in scope within its own body.
        (PsiTreeUtil.getParentOfType(position, IsPreprocessorDirective::class.java) as? IsPreprocessorDirectiveEx)
            ?.takeIf { it.isDefine() && it.isFunctionMacro() }
            ?.getMacroParameterDeclarations()
            ?.forEach { parameter ->
                adjusted.addElement(
                    LookupElementBuilder.create(parameter.name)
                        .withTypeText(parameter.declaredType?.name?.lowercase() ?: "parameter")
                        .withTailText(parameter.defaultValue?.let { " = $it" } ?: "", true)
                        .withIcon(IsIcons.Variable)
                )
            }

        // The loop variable of the enclosing #for is in scope within that loop's own slots.
        (PsiTreeUtil.getParentOfType(position, IsPreprocessorDirective::class.java) as? IsPreprocessorDirectiveEx)
            ?.takeIf { it.isFor() }
            ?.getForVariableName()
            ?.takeIf { it.isNotEmpty() }
            ?.let { varName ->
                adjusted.addElement(
                    LookupElementBuilder.create(varName)
                        .withTypeText("loop var")
                        .withIcon(IsIcons.Variable)
                )
            }

        // ISCC `/D<name>` symbols of the selected build configuration are in scope like a #define.
        hostFile.externalPreprocessorSymbols().forEach { (name, value) ->
            adjusted.addElement(
                LookupElementBuilder.create(name)
                    .withTypeText(value?.let { "= $it · build config" } ?: "build config")
                    .withIcon(IsIcons.Variable)
            )
        }

        // Predefined ISPP variables are always available in an expression.
        service<IsPreprocessorService>().spec.predefinedVariables.forEach { variable ->
            adjusted.addElement(
                LookupElementBuilder.create(variable.name)
                    .withTypeText("predefined ${variable.type.typeName}")
                    .withIcon(IsIcons.Constant)
            )
        }
    }
}
