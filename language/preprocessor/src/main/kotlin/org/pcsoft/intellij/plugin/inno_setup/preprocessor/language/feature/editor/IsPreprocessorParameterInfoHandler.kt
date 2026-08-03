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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.editor

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.lang.parameterInfo.CreateParameterInfoContext
import com.intellij.lang.parameterInfo.ParameterInfoHandler
import com.intellij.lang.parameterInfo.ParameterInfoUIContext
import com.intellij.lang.parameterInfo.UpdateParameterInfoContext
import com.intellij.openapi.components.service
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.asIsppHostFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorBuiltinParameterKind
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorCallSite
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprType
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.findEnclosingCall
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.isppDirectives
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.preprocessorTypeResolver
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.services.IsPreprocessorService

/**
 * The signature shown in the parameter info popup of a preprocessor call.
 *
 * @property parameters Rendered parameters in declaration order, e.g. `Index: int = 1`.
 * @property returnType Rendered result type appended after the parameter list, or `null` when it cannot be
 *   determined (an unconstrained function-like macro).
 */
data class IsPreprocessorParameterInfoItem(
    val parameters: List<String>,
    val returnType: String?,
) {
    /** The result type rendered as a trailing ` → str`, or empty when it could not be determined. */
    val returnSuffix: String get() = returnType?.let { " → $it" }.orEmpty()
}

/**
 * Shows the parameter list of an ISPP call (**Parameter Info**, `Ctrl+P`) while the caret is inside its
 * argument list, highlighting the argument being typed.
 *
 * Both callee kinds are supported:
 *  * a **built-in function** — rendered from the structured signature of the bundled specification
 *    (`IsPreprocessorService.builtinSignatures`), so declared parameter types, `Ident`/`Array` symbol
 *    parameters, by-reference markers and default values are visible;
 *  * a **function-like macro** of the host file — ISPP declares no types there, so the *probable* parameter
 *    and result types inferred from the macro body are shown whenever they can be determined, and the bare
 *    parameter name is shown otherwise.
 *
 * ISPP expressions are not part of the PSI tree, so the enclosing call is located in the directive text via
 * [findEnclosingCall]; the owning [IsPreprocessorDirective] acts as the parameter owner.
 */
class IsPreprocessorParameterInfoHandler :
    ParameterInfoHandler<IsPreprocessorDirective, IsPreprocessorParameterInfoItem> {

    override fun findElementForParameterInfo(context: CreateParameterInfoContext): IsPreprocessorDirective? {
        val (directive, call) = findCall(context.file, context.offset) ?: return null
        val item = buildItem(directive, call.name) ?: return null
        context.itemsToShow = arrayOf(item)
        return directive
    }

    override fun showParameterInfo(element: IsPreprocessorDirective, context: CreateParameterInfoContext) {
        val call = callAt(element, context.offset) ?: return
        context.showHint(element, element.textRange.startOffset + call.argumentListStart + 1, this)
    }

    override fun findElementForUpdatingParameterInfo(
        context: UpdateParameterInfoContext,
    ): IsPreprocessorDirective? = findCall(context.file, context.offset)?.first

    override fun updateParameterInfo(
        parameterOwner: IsPreprocessorDirective,
        context: UpdateParameterInfoContext,
    ) {
        val call = callAt(parameterOwner, context.offset)
        if (call == null) {
            context.removeHint()
            return
        }
        context.setCurrentParameter(call.argumentIndex)
    }

    override fun updateUI(p: IsPreprocessorParameterInfoItem?, context: ParameterInfoUIContext) {
        if (p == null) {
            context.isUIComponentEnabled = false
            return
        }
        val presentation = renderParameterInfo(
            p,
            context.currentParameterIndex,
            PluginBundle.message("parameter_info.no_parameters"),
        )
        context.setupUIComponentPresentation(
            presentation.text, presentation.highlightStart, presentation.highlightEnd,
            false, false, false, context.defaultParameterColor,
        )
    }

    /** The directive at [offset] of [file] together with the call enclosing that offset. */
    private fun findCall(file: PsiFile, offset: Int): Pair<IsPreprocessorDirective, IsPreprocessorCallSite>? {
        val element = file.findElementAt(offset) ?: return null
        val directive = PsiTreeUtil.getParentOfType(element, IsPreprocessorDirective::class.java) ?: return null
        val call = callAt(directive, offset) ?: return null
        return directive to call
    }

    /** The call of [directive] enclosing the (file-relative) [offset]. */
    private fun callAt(directive: IsPreprocessorDirective, offset: Int): IsPreprocessorCallSite? {
        val offsetInDirective = offset - directive.textRange.startOffset
        if (offsetInDirective < 0 || offsetInDirective > directive.textLength) return null
        return findEnclosingCall(directive.text, offsetInDirective)
    }

    /** The presentation of [name]: a built-in signature, else a function-like macro of the host file. */
    private fun buildItem(
        directive: IsPreprocessorDirective,
        name: String,
    ): IsPreprocessorParameterInfoItem? {
        service<IsPreprocessorService>().builtinSignature(name)?.let { signature ->
            return IsPreprocessorParameterInfoItem(
                parameters = signature.parameters.map { parameter ->
                    val type = when (parameter.kind) {
                        IsPreprocessorBuiltinParameterKind.INT -> "int"
                        IsPreprocessorBuiltinParameterKind.STR -> "str"
                        IsPreprocessorBuiltinParameterKind.ANY -> "any"
                        // `Ident`/`Array` are the parameter itself, not a name/type pair.
                        IsPreprocessorBuiltinParameterKind.IDENT,
                        IsPreprocessorBuiltinParameterKind.ARRAY,
                            -> null
                    }
                    buildString {
                        append(parameter.name)
                        type?.let { append(": ").append(it) }
                        if (parameter.byRef) append("*")
                        parameter.defaultValue?.let { append(" = ").append(it) }
                    }
                },
                returnType = signature.returnType.presentableName,
            )
        }
        return macroItem(directive, name)
    }

    /** The presentation of the function-like macro [name] of the host file, with its probable types. */
    private fun macroItem(
        directive: IsPreprocessorDirective,
        name: String,
    ): IsPreprocessorParameterInfoItem? {
        val hostFile = InjectedLanguageManager.getInstance(directive.project)
            .getTopLevelFile(directive.containingFile).asIsppHostFile() ?: return null
        val macro = hostFile.isppDirectives
            .mapNotNull { it as? IsPreprocessorDirectiveEx }
            .lastOrNull { it.isFunctionMacro() && it.getDefineName().equals(name, ignoreCase = true) }
            ?: return null

        val resolver = hostFile.preprocessorTypeResolver()
        val parameterTypes = resolver.probableMacroParameterTypes(name)
        val parameters = macro.getMacroParameters().mapIndexed { index, parameter ->
            val type = parameterTypes.getOrNull(index)?.presentableName
            if (type == null) parameter else "$parameter: $type"
        }
        return IsPreprocessorParameterInfoItem(
            parameters = parameters,
            returnType = resolver.probableMacroReturnType(name).presentableName,
        )
    }
}

/**
 * The rendered popup content of a signature.
 *
 * @property text Full label, e.g. `S: str, Index: int = 1 → int`.
 * @property highlightStart Start offset of the current parameter inside [text], or `-1` when none is
 *   highlighted.
 * @property highlightEnd End offset of the current parameter inside [text], or `-1`.
 */
data class IsPreprocessorParameterInfoPresentation(
    val text: String,
    val highlightStart: Int,
    val highlightEnd: Int,
)

/**
 * Renders [item] for the parameter info popup, highlighting the parameter at [currentParameterIndex].
 *
 * Kept free of platform state so it can be unit tested; [noParametersText] is the localized placeholder for
 * a signature without parameters.
 */
fun renderParameterInfo(
    item: IsPreprocessorParameterInfoItem,
    currentParameterIndex: Int,
    noParametersText: String,
): IsPreprocessorParameterInfoPresentation {
    if (item.parameters.isEmpty()) {
        return IsPreprocessorParameterInfoPresentation(noParametersText + item.returnSuffix, -1, -1)
    }

    val builder = StringBuilder()
    var highlightStart = -1
    var highlightEnd = -1
    item.parameters.forEachIndexed { index, parameter ->
        if (index > 0) builder.append(", ")
        if (index == currentParameterIndex) {
            highlightStart = builder.length
            highlightEnd = highlightStart + parameter.length
        }
        builder.append(parameter)
    }
    builder.append(item.returnSuffix)
    return IsPreprocessorParameterInfoPresentation(builder.toString(), highlightStart, highlightEnd)
}

/** The type name to display, or `null` when the type carries no information worth showing. */
private val IsPreprocessorExprType.presentableName: String?
    get() = when (this) {
        IsPreprocessorExprType.INT -> "int"
        IsPreprocessorExprType.STR -> "str"
        IsPreprocessorExprType.VOID -> "void"
        IsPreprocessorExprType.ANY -> null
    }
