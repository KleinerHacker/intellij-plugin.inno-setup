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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.util.TextRange
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.include.IsAnnotationSink
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsEscapeSequences
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.quickfix.RemoveExtraClosingBraceQuickFix
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionTypes

/**
 * Annotations around the escape sequences of a script value:
 *
 * - warns about `{{…}}`, whose doubled closing brace stays in the text (with a quick fix)
 * - reports a `{` that never closes
 *
 * The escapes themselves are tokens of the section lexer and are coloured by its syntax highlighter. Everything
 * the compiler accepts stays without an error: a lone `}` and a `{{` are ordinary text.
 */
object IsSectionEscapeAnnotations {

    /** Lower-case names of the sections whose values are message texts, where a `{` is free text. */
    private val MESSAGE_SECTIONS: Set<String> = setOf("messages", "custommessages")

    /** Annotates [value]; values of the `[Code]` section are not script values and are skipped. */
    fun annotate(value: IsSectionParamValue, holder: IsAnnotationSink) {
        if (value.isInCodeSection) return

        val isMessage = value.containingSection?.nameText?.lowercase() in MESSAGE_SECTIONS
        warnDoubleClosedBraces(value, holder)
        // A message text is free text; a "{" there is not necessarily the start of a constant.
        if (!isMessage) reportUnclosedConstants(value, holder)
    }

    /** `{{…}}`: Inno Setup yields `{…}}`, the second closing brace is kept. */
    private fun warnDoubleClosedBraces(value: IsSectionParamValue, holder: IsAnnotationSink) {
        val base = value.textRange.startOffset
        IsEscapeSequences.DOUBLE_CLOSED_BRACE.findAll(value.text).forEach { match ->
            val extra = match.range.last
            holder.newAnnotation(HighlightSeverity.WARNING, IsEscapeSequences.DOUBLE_CLOSED_BRACE_MESSAGE)
                .range(TextRange(base + extra, base + extra + 1))
                .withFix(RemoveExtraClosingBraceQuickFix(value, extra))
                .create()
        }
    }

    /** A `{` that is no constant and no `{{` escape: the constant is not closed. */
    private fun reportUnclosedConstants(value: IsSectionParamValue, holder: IsAnnotationSink) {
        value.node.getChildren(TokenSet.create(IsSectionTypes.LBRACE)).forEach { brace ->
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Unclosed constant: '{' has no matching '}' (write '{{' for a literal '{')"
            ).range(brace.textRange).create()
        }
    }
}
