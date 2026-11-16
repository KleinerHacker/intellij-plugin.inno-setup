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
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.include.IsAnnotationSink
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionConstant
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParamValue
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionTypes
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.quickfix.RemoveExtraClosingBraceQuickFix

/**
 * Annotations around the escape sequences of a script value (see [IsSectionEscapes]):
 *
 * - colours the `%` escapes that are plain text for the lexer: `%7d`, `%2c`, `%7c` in constant arguments and
 *   `%n`, `%%`, `%1`–`%9` in the values of `[Messages]` and `[CustomMessages]`
 * - warns about `{{…}}`, whose doubled closing brace stays in the text (with a quick fix)
 * - reports a `{` that never closes
 *
 * Everything the compiler accepts stays without an error: a lone `}` and a `{{` are ordinary text.
 */
object IsSectionEscapeAnnotations {

    /** Annotates [value]; values of the `[Code]` section are not script values and are skipped. */
    fun annotate(value: IsSectionParamValue, holder: IsAnnotationSink) {
        if (value.isInCodeSection) return

        val isMessage = value.containingSection?.nameText?.lowercase() in IsSectionEscapes.MESSAGE_SECTIONS
        val constants = PsiTreeUtil.findChildrenOfType(value, IsSectionConstant::class.java)

        highlightConstantArgumentEscapes(constants, holder)
        if (isMessage) highlightMessageEscapes(value, constants.map { it.textRange }, holder)
        warnDoubleClosedBraces(value, holder)
        // A message text is free text; a "{" there is not necessarily the start of a constant.
        if (!isMessage) reportUnclosedConstants(value, holder)
    }

    /** `%7d`, `%2c` and `%7c` inside the text of a constant. */
    private fun highlightConstantArgumentEscapes(constants: Collection<IsSectionConstant>, holder: IsAnnotationSink) {
        constants.forEach { constant ->
            val start = constant.textRange.startOffset
            IsSectionEscapes.CONSTANT_ARGUMENT_ESCAPE.findAll(constant.text).forEach { match ->
                highlight(TextRange(start + match.range.first, start + match.range.last + 1), holder)
            }
        }
    }

    /** `%n`, `%%` and `%1`–`%9` outside of constants in the value of a message section. */
    private fun highlightMessageEscapes(value: IsSectionParamValue, constants: List<TextRange>, holder: IsAnnotationSink) {
        val base = value.textRange.startOffset
        IsSectionEscapes.MESSAGE_ESCAPE.findAll(value.text).forEach { match ->
            val range = TextRange(base + match.range.first, base + match.range.last + 1)
            if (constants.none { it.contains(range) }) highlight(range, holder)
        }
    }

    /** `{{…}}`: Inno Setup yields `{…}}`, the second closing brace is kept. */
    private fun warnDoubleClosedBraces(value: IsSectionParamValue, holder: IsAnnotationSink) {
        val base = value.textRange.startOffset
        IsSectionEscapes.DOUBLE_CLOSED_BRACE.findAll(value.text).forEach { match ->
            val extra = match.range.last
            holder.newAnnotation(
                HighlightSeverity.WARNING,
                "'{{…}}' is not an escaped '{…}': '{{' becomes '{', but a closing '}' is never escaped, " +
                        "so the result is '{…}}' with a stray '}'. Use a single '}'"
            ).range(TextRange(base + extra, base + extra + 1))
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

    private fun highlight(range: TextRange, holder: IsAnnotationSink) {
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range)
            .textAttributes(IsSectionSyntaxHighlighting.ESCAPE_SEQUENCE)
            .create()
    }
}
