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

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.service
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.language.feature.version.IsPreprocessorVersionSupport
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.psi.IsPreprocessorTypes
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.services.IsPreprocessorService

/**
 * Reports ISPP directives, built-in functions and predefined variables that are not available in the
 * configured minimum Inno Setup version (e.g. the function `Format`, which exists since Inno Setup 7.1).
 *
 * This lives in the `plugin` module because the configured minimum version is a script-module setting,
 * which the independent `preprocessor` module must not know about.
 */
class IsPreprocessorVersionAnnotator : Annotator {

    /**
     * Adds version warnings (entry newer than the minimum) or errors (entry removed) to a directive and to the
     * functions and variables used in its value.
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is IsPreprocessorDirective) return
        if (IsPreprocessorVersionSupport.stateOf(null, null) != IsPreprocessorVersionSupport.State.AVAILABLE) return

        annotateDirectiveKeyword(element, holder)

        val value = element.value ?: return
        IsPreprocessorVersionSupport.findUsages(value.text, value.textRange.startOffset).forEach { usage ->
            report(holder, usage.range, usage.name, usage.since, usage.until)
        }
    }

    private fun annotateDirectiveKeyword(directive: IsPreprocessorDirective, holder: AnnotationHolder) {
        val hash = directive.node.findChildByType(IsPreprocessorTypes.HASH) ?: return
        val keyword = directive.node.findChildByType(IsPreprocessorTypes.IDENTIFIER) ?: return
        val spec = service<IsPreprocessorService>().spec.directives
            .firstOrNull { it.name.equals(keyword.text, ignoreCase = true) } ?: return
        val range = TextRange(hash.startOffset, keyword.textRange.endOffset)
        report(holder, range, "#${spec.name}", spec.since, spec.until)
    }

    private fun report(holder: AnnotationHolder, range: TextRange, name: String, since: String?, until: String?) {
        val message = IsPreprocessorVersionSupport.message(name, since, until) ?: return
        val removed = IsPreprocessorVersionSupport.stateOf(since, until) == IsPreprocessorVersionSupport.State.REMOVED
        holder.newAnnotation(if (removed) HighlightSeverity.ERROR else HighlightSeverity.WARNING, message)
            .range(range).create()
    }
}
