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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.preprocessor

import com.intellij.codeInsight.hints.declarative.EndOfLinePosition
import com.intellij.codeInsight.hints.declarative.HintFormat
import com.intellij.codeInsight.hints.declarative.InlayHintsCollector
import com.intellij.codeInsight.hints.declarative.InlayHintsProvider
import com.intellij.codeInsight.hints.declarative.InlayTreeSink
import com.intellij.codeInsight.hints.declarative.OwnBypassCollector
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiFile
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprEmpty
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprIntLiteral
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprParser
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprStrLiteral
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprValueResolver
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.isIsppHostFile
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.isppDirectivesWithHostOffset
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.preprocessorValueResolver
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirectiveEx

/**
 * Renders the statically computed value of a simple `#define` as an end-of-line inlay hint, so a macro that is
 * assembled from other macros and operators shows its result without having to compile the script.
 *
 * Only *computed* values are shown. A `#define` whose value is already written out literally in the source
 * (`#define MyVer "1.5"`) contributes no hint, because the hint would merely repeat the text next to it. The
 * same holds for everything that has no single static value: function-like macros, array element defines
 * (`#define Arr[0] …`), value-less defines and expressions that cannot be evaluated at analysis time — an
 * unresolved reference, a `{…}` constant, or a call to an ISPP **built-in**, which
 * [IsPreprocessorExprValueResolver] never executes (it evaluates user-defined macros only).
 *
 * The hint can be switched off under *Settings | Editor | Inlay Hints*; the declarative inlay API provides
 * that toggle on its own, so this provider carries no setting of its own.
 */
class IsDefineValueInlayHintsProvider : InlayHintsProvider {

    /**
     * Creates the collector for [file], or `null` when the file cannot carry ISPP directives at all.
     */
    override fun createCollector(file: PsiFile, editor: Editor): InlayHintsCollector? =
        if (file.isIsppHostFile()) Collector() else null

    /**
     * Collects the hints of the whole file in one pass. A per-element collector would have to rebuild the
     * value resolver for every `#define` line, which is why [OwnBypassCollector] is used here.
     */
    private class Collector : OwnBypassCollector {

        override fun collectHintsForFile(file: PsiFile, sink: InlayTreeSink) {
            val analysis = computeDefineValueHints(file)
            if (analysis.isEmpty()) return

            val document = file.viewProvider.document ?: return
            analysis.forEach { (hostOffset, value) ->
                if (hostOffset >= document.textLength) return@forEach
                val line = document.getLineNumber(hostOffset)
                sink.addPresentation(
                    position = EndOfLinePosition(line),
                    payloads = null,
                    tooltip = null,
                    hintFormat = HintFormat.default,
                ) {
                    text(value)
                }
            }
        }
    }

}

/**
 * All `#define` lines of [file] that carry a computed value, as host offset paired with the displayable value,
 * in document order.
 *
 * Cached against [PsiModificationTracker.MODIFICATION_COUNT] because collecting the injected ISPP directives
 * walks every preprocessor line of the file, and the highlighting pass may run repeatedly without the document
 * changing in between.
 */
internal fun computeDefineValueHints(file: PsiFile): List<Pair<Int, String>> =
    CachedValuesManager.getCachedValue(file) {
        CachedValueProvider.Result.create(
            file.collectDefineValues(),
            PsiModificationTracker.MODIFICATION_COUNT,
        )
    }

private fun PsiFile.collectDefineValues(): List<Pair<Int, String>> {
    val directives = isppDirectivesWithHostOffset
    if (directives.isEmpty()) return emptyList()

    // Built once for the whole file — IsPreprocessorDirectiveEx.computeValue() would rebuild it per line.
    val resolver = preprocessorValueResolver()
    return directives.mapNotNull { (directive, hostOffset) ->
        val value = (directive as? IsPreprocessorDirectiveEx)?.computedValueText(resolver, hostOffset)
        value?.let { hostOffset to it }
    }
}

/**
 * The displayable value of this `#define`, or `null` when it has none worth showing (see
 * [IsDefineValueInlayHintsProvider] for which cases are excluded).
 */
private fun IsPreprocessorDirectiveEx.computedValueText(
    resolver: IsPreprocessorExprValueResolver,
    hostOffset: Int,
): String? {
    if (!isDefine() || isFunctionMacro() || isArrayElementDefine()) return null
    val expression = getDefineExpressionText() ?: return null
    if (!isComputedExpression(expression)) return null
    return resolver.evaluate(expression, hostOffset)?.display()
}

/**
 * Whether [expression] actually computes something. A bare literal already shows its value in the source, so
 * repeating it as a hint would be noise.
 */
private fun isComputedExpression(expression: String): Boolean =
    when (IsPreprocessorExprParser.parse(expression).ast) {
        is IsPreprocessorExprIntLiteral, is IsPreprocessorExprStrLiteral, is IsPreprocessorExprEmpty -> false
        else -> true
    }
