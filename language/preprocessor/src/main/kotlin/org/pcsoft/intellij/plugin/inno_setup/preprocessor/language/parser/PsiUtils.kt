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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.*
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.IsPreprocessorFunctionReturnType
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.IsPreprocessorVariableType

// ── ISPP host bridge (.iss/.isl script + .ist template) ──────────────────────────────

/** Whether `this` is a file ISPP directives can be injected into: a script (.iss/.isl) or template (.ist). */
fun PsiFile?.isIsppHostFile(): Boolean = this is IsPreprocessorHost

/** `this` if it is an ISPP host file (script or template), otherwise `null`. */
fun PsiFile?.asIsppHostFile(): PsiFile? = this?.takeIf { it.isIsppHostFile() }

/**
 * The direct child lines of this host file that carry an injected ISPP `#…` fragment: the section
 * preprocessor lines of a script (.iss/.isl) or the preprocessor lines of a template (.ist). A given file
 * only ever has one kind, so the children stay in document order.
 */
private val PsiFile.preprocessorHostLines: List<PsiElement>
    get() = children.filter { it is IsPreprocessorHostLine }

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
        .filter { (it as? IsPreprocessorDirectiveEx)?.isArrayElementDefine() != true }
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
        if (!dex.isDefine() || dex.isFunctionMacro() || dex.isArrayElementDefine()) return@mapNotNull null
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

/** Evaluates [expr] (as seen at host offset [order]) to a constant integer over this file's scalar #defines. */
private fun PsiFile.staticInt(expr: String, order: Int): Long? {
    if (expr.isBlank()) return null
    expr.trim().toLongOrNull()?.let { return it }
    val value = IsPreprocessorExprValueResolver(simpleDefineInfos(), functionMacroInfos()).evaluate(expr, order)
    return (value as? IsPreprocessorExprValue.IntValue)?.value
}

/** All `#dim`/`#redim` array declarations of this file (size resolved to a constant where possible). */
private fun PsiFile.arrayInfos(): List<IsPreprocessorExprArrayInfo> =
    isppDirectivesWithHostOffset.mapNotNull { (dir, offset) ->
        val ex = dir as? IsPreprocessorDirectiveEx ?: return@mapNotNull null
        if (!ex.isArrayDeclaration()) return@mapNotNull null
        val name = ex.getArrayName() ?: return@mapNotNull null
        IsPreprocessorExprArrayInfo(name, ex.getArraySizeText()?.let { staticInt(it, offset) }, offset)
    }

/**
 * All array element assignments of this file: explicit `#define Name\[Index] Value` plus the positional elements
 * of a `#dim Name[..] {v0, v1, …}` inline initialiser (index = position). Only assignments with a statically
 * resolvable integer index are kept.
 */
private fun PsiFile.arrayElementInfos(): List<IsPreprocessorExprArrayElementInfo> {
    val result = mutableListOf<IsPreprocessorExprArrayElementInfo>()
    isppDirectivesWithHostOffset.forEach { (dir, offset) ->
        val ex = dir as? IsPreprocessorDirectiveEx ?: return@forEach
        if (ex.isArrayElementDefine()) {
            val name = ex.getDefineArrayName()
            val index = ex.getDefineArrayIndexText()?.let { staticInt(it, offset) }
            val expr = ex.getDefineExpressionText()
            if (name != null && index != null && expr != null) {
                result += IsPreprocessorExprArrayElementInfo(name, index, expr, offset)
            }
        }
        if (ex.isDim()) {
            val name = ex.getArrayName()
            val init = ex.getArrayInitializerText()
            if (name != null && init != null) {
                splitTopLevelCommas(init).forEachIndexed { i, element ->
                    if (element.isNotBlank()) {
                        result += IsPreprocessorExprArrayElementInfo(name, i.toLong(), element.trim(), offset)
                    }
                }
            }
        }
    }
    return result
}

/** Splits [text] on top-level commas, ignoring commas nested in `()`/`[]`/`{}` or inside `"…"` strings. */
private fun splitTopLevelCommas(text: String): List<String> {
    val parts = mutableListOf<String>()
    val sb = StringBuilder()
    var depth = 0
    var inString = false
    for (c in text) {
        when {
            inString -> {
                sb.append(c); if (c == '"') inString = false
            }

            c == '"' -> {
                sb.append(c); inString = true
            }

            c == '(' || c == '[' || c == '{' -> {
                depth++; sb.append(c)
            }

            c == ')' || c == ']' || c == '}' -> {
                depth--; sb.append(c)
            }

            c == ',' && depth == 0 -> {
                parts += sb.toString(); sb.clear()
            }

            else -> sb.append(c)
        }
    }
    parts += sb.toString()
    return parts
}

/**
 * A type resolver over all `#define`s, function-like macros and arrays of this file plus the bundled ISPP spec.
 * [extraVariables] are additional in-scope variable types (e.g. a `#for` loop variable) that take precedence
 * over the predefined ISPP variables; matched case-insensitively.
 */
fun PsiFile.preprocessorTypeResolver(
    extraVariables: Map<String, IsPreprocessorExprType> = emptyMap(),
): IsPreprocessorExprTypeResolver {
    val spec = service<IsPreprocessorService>().spec
    val builtinReturnType: (String) -> IsPreprocessorExprType = { name ->
        spec.builtinFunctions.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?.returnType?.toExprType() ?: IsPreprocessorExprType.ANY
    }
    val variableType: (String) -> IsPreprocessorExprType? = { name ->
        extraVariables.entries.firstOrNull { it.key.equals(name, ignoreCase = true) }?.value
            ?: spec.predefinedVariables.firstOrNull { it.name.equals(name, ignoreCase = true) }?.type?.toExprType()
    }
    return IsPreprocessorExprTypeResolver(
        simpleDefineInfos(), functionMacroInfos(), variableType, builtinReturnType, arrayInfos(),
    )
}

/** A value resolver over all simple `#define`s, function-like macros and array elements of this file. */
fun PsiFile.preprocessorValueResolver(): IsPreprocessorExprValueResolver =
    IsPreprocessorExprValueResolver(simpleDefineInfos(), functionMacroInfos(), arrayElementInfos())

/** The statically computed value of the array element `name[index]` as visible before host offset [beforeOffset]. */
fun PsiFile.computeArrayElementValue(
    name: String,
    index: Long,
    beforeOffset: Int = Int.MAX_VALUE
): IsPreprocessorExprValue? =
    preprocessorValueResolver().arrayElementValue(name, index, beforeOffset)

/** The statically known element count of the array [name] as visible before host offset [beforeOffset], or `null`. */
fun PsiFile.arraySize(name: String, beforeOffset: Int = Int.MAX_VALUE): Long? =
    arrayInfos()
        .filter { it.order < beforeOffset && it.name.equals(name, ignoreCase = true) }
        .maxByOrNull { it.order }
        ?.size

/** The nearest `#dim`/`#redim` array named [name] declared before host offset [beforeOffset], or `null`. */
fun PsiFile.precedingArray(name: String, beforeOffset: Int): IsPreprocessorDirectiveEx? =
    isppDirectivesWithHostOffset
        .filter { (d, off) ->
            val ex = d as? IsPreprocessorDirectiveEx
            off < beforeOffset && ex?.isArrayDeclaration() == true && ex.getArrayName().equals(name, ignoreCase = true)
        }
        .maxByOrNull { it.second }
        ?.first as? IsPreprocessorDirectiveEx

/** The nearest `#define` named [name] declared before host offset [beforeOffset], or `null`. */
fun PsiFile.precedingDefine(name: String, beforeOffset: Int): IsPreprocessorDirectiveEx? =
    isppDirectivesWithHostOffset
        .filter { (d, off) ->
            off < beforeOffset && (d as? IsPreprocessorDirectiveEx)?.isDefine() == true &&
                    (d as? IsPreprocessorDirectiveEx)?.getDefineName().equals(name, ignoreCase = true)
        }
        .maxByOrNull { it.second }
        ?.first as? IsPreprocessorDirectiveEx

/** The nearest `#sub` named [name] declared before host offset [beforeOffset], or `null`. */
fun PsiFile.precedingSub(name: String, beforeOffset: Int): IsPreprocessorDirectiveEx? =
    isppDirectivesWithHostOffset
        .filter { (d, off) ->
            off < beforeOffset && (d as? IsPreprocessorDirectiveEx)?.isSub() == true &&
                    (d as? IsPreprocessorDirectiveEx)?.getSubroutineName().equals(name, ignoreCase = true)
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
