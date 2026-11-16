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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.language.feature.version.IsPreprocessorVersionSupport
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionConstantBody

/**
 * Reports ISPP built-in functions and predefined variables used in an inline `{#…}` emission of a script
 * (e.g. `{#SysPath}` or `{#Format(…)}`) that are not available in the configured minimum Inno Setup version.
 *
 * It complements [org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.preprocessor.IsPreprocessorVersionAnnotator],
 * which handles the `#…` directive lines; both live in the `plugin` module because the minimum version is a
 * script-module setting.
 */
class IsSectionPreprocessorVersionAnnotator : Annotator {

    /**
     * Adds a version warning (entry newer than the minimum) or error (entry removed) to every versioned
     * function or variable of an inline ISPP emission.
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element !is IsSectionConstantBody) return
        val text = element.text ?: return
        if (!text.startsWith("#")) return
        if (IsPreprocessorVersionSupport.stateOf(null, null) != IsPreprocessorVersionSupport.State.AVAILABLE) return

        IsPreprocessorVersionSupport.findUsages(text.substring(1), element.textRange.startOffset + 1).forEach { usage ->
            val message = IsPreprocessorVersionSupport.message(usage.name, usage.since, usage.until) ?: return@forEach
            val removed = IsPreprocessorVersionSupport.stateOf(usage.since, usage.until) ==
                    IsPreprocessorVersionSupport.State.REMOVED
            holder.newAnnotation(if (removed) HighlightSeverity.ERROR else HighlightSeverity.WARNING, message)
                .range(usage.range).create()
        }
    }
}
