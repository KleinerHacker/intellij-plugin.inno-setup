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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.include

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.ASTNode
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

/** A single recorded annotation: its severity, the affected range, and the message. */
data class RecordedProblem(val severity: HighlightSeverity, val range: TextRange, val message: String)

/**
 * An [IsAnnotationDraft] that records nothing visually: its fluent calls are no-ops returning `this`. Only the
 * range, an optional explicit message and the file-level flag are tracked. On [create] a non-file-level ERROR
 * or WARNING with a known range and message is handed to [sink]; everything else (highlighting/INFORMATION,
 * weak warnings, file-level problems) is discarded.
 *
 * Used to replay the real annotators over an in-memory effective script and harvest their problems without
 * touching any editor markup. See [IsEffectiveScriptProblems]. Deliberately implements only the plugin-owned
 * [IsAnnotationDraft] façade — never the platform's `@NonExtendable` `AnnotationBuilder`.
 */
class RecordingAnnotationBuilder(
    private val severity: HighlightSeverity,
    private val message: String?,
    private val sink: (RecordedProblem) -> Unit,
) : IsAnnotationDraft {

    private var range: TextRange? = null
    private var fileLevel = false

    override fun range(range: TextRange): IsAnnotationDraft = apply { this.range = range }
    override fun range(node: ASTNode): IsAnnotationDraft = apply { this.range = node.textRange }
    override fun range(element: PsiElement): IsAnnotationDraft = apply { this.range = element.textRange }

    override fun fileLevel(): IsAnnotationDraft = apply { fileLevel = true }
    override fun textAttributes(key: TextAttributesKey): IsAnnotationDraft = this
    override fun withFix(fix: IntentionAction): IsAnnotationDraft = this

    override fun create() {
        val r = range ?: return
        if (fileLevel) return
        if (severity != HighlightSeverity.ERROR && severity != HighlightSeverity.WARNING) return
        val msg = message ?: return
        sink(RecordedProblem(severity, r, msg))
    }
}
