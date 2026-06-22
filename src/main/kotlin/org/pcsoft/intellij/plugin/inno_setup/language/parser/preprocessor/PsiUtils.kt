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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.template.IsTemplateFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.expression.*
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.language.parser.template.psi.IsTemplatePreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.types.IsPreprocessorFunctionReturnType
import org.pcsoft.intellij.plugin.inno_setup.types.IsPreprocessorVariableType

// ── ISPP host bridge (.iss/.isl script + .ist template) ──────────────────────────────

/** Whether `this` is a file ISPP directives can be injected into: a script (.iss/.isl) or template (.ist). */
fun PsiFile?.isIsppHostFile(): Boolean = this is IsScriptFile || this is IsTemplateFile

/** `this` if it is an ISPP host file (script or template), otherwise `null`. */
fun PsiFile?.asIsppHostFile(): PsiFile? = this?.takeIf { it.isIsppHostFile() }

/**
 * The direct child lines of this host file that carry an injected ISPP `#…` fragment: the section
 * preprocessor lines of a script (.iss/.isl) or the preprocessor lines of a template (.ist). A given file
 * only ever has one kind, so the children stay in document order.
 */
private val PsiFile.preprocessorHostLines: List<PsiElement>
    get() = children.filter { it is IsSectionPreprocessorLine || it is IsTemplatePreprocessorLine }

/**
 * Returns or performs the public behavior represented by this member.
 */
val PsiFile.isppDirectives: List<IsPreprocessorDirective>
    get() {
        val mgr = InjectedLanguageManager.getInstance(project)
        return preprocessorHostLines
            .flatMap { line ->
                val result = mutableListOf<IsPreprocessorDirective>()
                mgr.enumerate(line) { injectedPsi, _ ->
                    if (injectedPsi is IsPreprocessorFile) {
                        result.addAll(
                            PsiTreeUtil.getChildrenOfTypeAsList(
                                injectedPsi,
                                IsPreprocessorDirective::class.java
                            )
                        )
                    }
                }
                result
            }
    }

/**
 * All ISPP directives paired with the host-file offset of the line they live on.
 * Because each `#define` line is injected as its own fragment, the host offset (the start of the
 * containing preprocessor line, a direct child of this file) is the authority for declaration order.
 */
val PsiFile.isppDirectivesWithHostOffset: List<Pair<IsPreprocessorDirective, Int>>
    get() {
        val mgr = InjectedLanguageManager.getInstance(project)
        return preprocessorHostLines
            .flatMap { line ->
                val result = mutableListOf<Pair<IsPreprocessorDirective, Int>>()
                mgr.enumerate(line) { injectedPsi, _ ->
                    if (injectedPsi is IsPreprocessorFile) {
                        PsiTreeUtil.getChildrenOfTypeAsList(injectedPsi, IsPreprocessorDirective::class.java)
                            .forEach { result.add(it to line.textRange.startOffset) }
                    }
                }
                result
            }
    }

/**
 * Returns or performs the public behavior represented by this member.
 */
val PsiFile.definedConstants: List<Pair<String, String?>>
    get() = isppDirectives
        .filter { (it as? IsPreprocessorDirectiveEx)?.isDefine() == true }
        .mapNotNull { directive ->
            val ex = directive as? IsPreprocessorDirectiveEx ?: return@mapNotNull null
            val name = ex.getDefineName()?.ifEmpty { null } ?: return@mapNotNull null
            name to ex.getDefineValue()
        }

// ── ISPP type / value analysis ──────────────────────────────────────────────

private fun IsPreprocessorFunctionReturnType.toExprType(): IsPreprocessorExprType = when (this) {
    IsPreprocessorFunctionReturnType.INT -> IsPreprocessorExprType.INT
    IsPreprocessorFunctionReturnType.STR -> IsPreprocessorExprType.STR
    IsPreprocessorFunctionReturnType.VOID -> IsPreprocessorExprType.VOID
    IsPreprocessorFunctionReturnType.ANY -> IsPreprocessorExprType.ANY
}

private fun IsPreprocessorVariableType.toExprType(): IsPreprocessorExprType = when (this) {
    IsPreprocessorVariableType.INT -> IsPreprocessorExprType.INT
    IsPreprocessorVariableType.STR -> IsPreprocessorExprType.STR
    IsPreprocessorVariableType.VOID -> IsPreprocessorExprType.VOID
}

private fun PsiFile.simpleDefineInfos(): List<IsPreprocessorExprDefineInfo> =
    isppDirectivesWithHostOffset.mapNotNull { (dir, offset) ->
        val dex = dir as? IsPreprocessorDirectiveEx ?: return@mapNotNull null
        if (!dex.isDefine() || dex.isFunctionMacro()) return@mapNotNull null
        val name = dex.getDefineName() ?: return@mapNotNull null
        val text = dex.getDefineExpressionText() ?: return@mapNotNull null
        IsPreprocessorExprDefineInfo(name, text, offset)
    }

private fun PsiFile.functionMacroInfos(): List<IsPreprocessorExprFunctionMacroInfo> =
    isppDirectivesWithHostOffset.mapNotNull { (dir, offset) ->
        val dex = dir as? IsPreprocessorDirectiveEx ?: return@mapNotNull null
        if (!dex.isDefine() || !dex.isFunctionMacro()) return@mapNotNull null
        val name = dex.getDefineName() ?: return@mapNotNull null
        val body = dex.getMacroBody() ?: return@mapNotNull null
        IsPreprocessorExprFunctionMacroInfo(name, dex.getMacroParameters(), body, offset)
    }

/** A type resolver over all `#define`s and function-like macros of this file plus the bundled ISPP spec. */
fun PsiFile.preprocessorTypeResolver(): IsPreprocessorExprTypeResolver {
    val spec = service<IsPreprocessorService>().spec
    val builtinReturnType: (String) -> IsPreprocessorExprType = { name ->
        spec.builtinFunctions.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?.returnType?.toExprType() ?: IsPreprocessorExprType.ANY
    }
    val variableType: (String) -> IsPreprocessorExprType? = { name ->
        spec.predefinedVariables.firstOrNull { it.name.equals(name, ignoreCase = true) }?.type?.toExprType()
    }
    return IsPreprocessorExprTypeResolver(simpleDefineInfos(), functionMacroInfos(), variableType, builtinReturnType)
}

/** A value resolver over all simple `#define`s and function-like macros of this file. */
fun PsiFile.preprocessorValueResolver(): IsPreprocessorExprValueResolver =
    IsPreprocessorExprValueResolver(simpleDefineInfos(), functionMacroInfos())

/** The nearest `#define` named [name] declared before host offset [beforeOffset], or `null`. */
fun PsiFile.precedingDefine(name: String, beforeOffset: Int): IsPreprocessorDirectiveEx? =
    isppDirectivesWithHostOffset
        .filter { (d, off) ->
            off < beforeOffset && (d as? IsPreprocessorDirectiveEx)?.isDefine() == true &&
                (d as? IsPreprocessorDirectiveEx)?.getDefineName().equals(name, ignoreCase = true)
        }
        .maxByOrNull { it.second }
        ?.first as? IsPreprocessorDirectiveEx

/** This directive's host file (script or template) together with its declaration order (host offset), or `null`. */
private fun IsPreprocessorDirectiveEx.hostContext(): Pair<PsiFile, Int>? {
    val host = InjectedLanguageManager.getInstance(project).getTopLevelFile(containingFile).asIsppHostFile()
        ?: return null
    val order = host.isppDirectivesWithHostOffset.firstOrNull { it.first === this }?.second ?: return null
    return host to order
}

/**
 * The inferred type of this `#define`: for a simple macro the type of its value expression, for a function-
 * like macro its probable return type. [IsPreprocessorExprType.VOID] for a value-less `#define`,
 * [IsPreprocessorExprType.ANY] when it cannot be determined.
 */
fun IsPreprocessorDirectiveEx.inferType(): IsPreprocessorExprType {
    if (!isDefine()) return IsPreprocessorExprType.ANY
    val name = getDefineName() ?: return IsPreprocessorExprType.ANY
    val (host, order) = hostContext() ?: return IsPreprocessorExprType.ANY
    if (isFunctionMacro()) return host.preprocessorTypeResolver().probableMacroReturnType(name)
    val expr = getDefineExpressionText() ?: return IsPreprocessorExprType.VOID
    return host.preprocessorTypeResolver().inferenceAt(order).infer(IsPreprocessorExprParser.parse(expr).ast)
}

/** The statically computed value of a simple `#define`, or `null` (function-like macro / not computable). */
fun IsPreprocessorDirectiveEx.computeValue(): IsPreprocessorExprValue? {
    if (!isDefine() || isFunctionMacro()) return null
    val expr = getDefineExpressionText() ?: return null
    val (host, order) = hostContext() ?: return null
    return host.preprocessorValueResolver().evaluate(expr, order)
}

/**
 * The probable return type of this `#define`: for a function-like macro the type of its body with the
 * parameters bound to their probable types; for a simple `#define` the same as [inferType].
 * [IsPreprocessorExprType.ANY] when it cannot be determined.
 */
fun IsPreprocessorDirectiveEx.inferReturnType(): IsPreprocessorExprType {
    if (!isFunctionMacro()) return inferType()
    val name = getDefineName() ?: return IsPreprocessorExprType.ANY
    val host = hostContext()?.first ?: return IsPreprocessorExprType.ANY
    return host.preprocessorTypeResolver().probableMacroReturnType(name)
}

/** Probable parameter types of a function-like macro, paired with their names; empty for a simple `#define`. */
fun IsPreprocessorDirectiveEx.inferParameterTypes(): List<Pair<String, IsPreprocessorExprType>> {
    if (!isFunctionMacro()) return emptyList()
    val params = getMacroParameters()
    val name = getDefineName() ?: return params.map { it to IsPreprocessorExprType.ANY }
    val host = hostContext()?.first ?: return params.map { it to IsPreprocessorExprType.ANY }
    val types = host.preprocessorTypeResolver().probableMacroParameterTypes(name)
    return params.mapIndexed { i, p -> p to (types.getOrNull(i) ?: IsPreprocessorExprType.ANY) }
}

/** Probable type of the parameter named [parameterName] of a function-like macro, or [IsPreprocessorExprType.ANY]. */
fun IsPreprocessorDirectiveEx.inferParameterType(parameterName: String): IsPreprocessorExprType =
    inferParameterTypes().firstOrNull { it.first == parameterName }?.second ?: IsPreprocessorExprType.ANY
