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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.include

import com.intellij.codeInsight.daemon.HighlightDisplayKey
import com.intellij.codeInsight.daemon.QuickFixActionRegistrar
import com.intellij.codeInsight.intention.CommonIntentionAction
import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.codeInspection.LocalQuickFix
import com.intellij.codeInspection.ProblemDescriptor
import com.intellij.codeInspection.ProblemHighlightType
import com.intellij.lang.ASTNode
import com.intellij.lang.annotation.Annotation
import com.intellij.lang.annotation.AnnotationBuilder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.annotation.ProblemGroup
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.editor.markup.TextAttributes
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import java.util.function.Consumer

/** A single recorded annotation: its severity, the affected range, and the message. */
data class RecordedProblem(val severity: HighlightSeverity, val range: TextRange, val message: String)

/**
 * An [AnnotationBuilder] that records nothing visually: its fluent calls are no-ops returning `this`. Only the
 * range, an optional explicit message and the file-level flag are tracked. On [create] a non-file-level ERROR
 * or WARNING with a known range and message is handed to [sink]; everything else (highlighting/INFORMATION,
 * weak warnings, file-level problems) is discarded.
 *
 * Used to replay the real annotators over an in-memory effective script and harvest their problems without
 * touching any editor markup. See [IsEffectiveScriptProblems].
 */
class RecordingAnnotationBuilder(
    private val severity: HighlightSeverity,
    private val message: String?,
    private val sink: (RecordedProblem) -> Unit,
) : AnnotationBuilder {

    private var range: TextRange? = null
    private var fileLevel = false

    override fun range(range: TextRange): AnnotationBuilder = apply { this.range = range }
    override fun range(node: ASTNode): AnnotationBuilder = apply { this.range = node.textRange }
    override fun range(element: PsiElement): AnnotationBuilder = apply { this.range = element.textRange }

    override fun afterEndOfLine(): AnnotationBuilder = this
    override fun fileLevel(): AnnotationBuilder = apply { fileLevel = true }
    override fun gutterIconRenderer(renderer: GutterIconRenderer): AnnotationBuilder = this
    override fun problemGroup(problemGroup: ProblemGroup): AnnotationBuilder = this
    override fun enforcedTextAttributes(enforcedAttributes: TextAttributes): AnnotationBuilder = this
    override fun textAttributes(enforcedAttributes: TextAttributesKey): AnnotationBuilder = this
    override fun highlightType(highlightType: ProblemHighlightType): AnnotationBuilder = this
    override fun tooltip(tooltip: String): AnnotationBuilder = this
    override fun needsUpdateOnTyping(): AnnotationBuilder = this
    override fun needsUpdateOnTyping(value: Boolean): AnnotationBuilder = this

    override fun withFix(fix: IntentionAction): AnnotationBuilder = this
    override fun withFix(fix: CommonIntentionAction): AnnotationBuilder = this
    override fun newFix(fix: IntentionAction): AnnotationBuilder.FixBuilder = NoOpFixBuilder(this)
    override fun newFix(fix: CommonIntentionAction): AnnotationBuilder.FixBuilder = NoOpFixBuilder(this)
    override fun newLocalQuickFix(fix: LocalQuickFix, descriptor: ProblemDescriptor): AnnotationBuilder.FixBuilder =
        NoOpFixBuilder(this)

    override fun withLazyQuickFix(quickFixComputer: Consumer<in QuickFixActionRegistrar>): AnnotationBuilder = this

    override fun create() {
        val r = range ?: return
        if (fileLevel) return
        if (severity != HighlightSeverity.ERROR && severity != HighlightSeverity.WARNING) return
        val msg = message ?: return
        sink(RecordedProblem(severity, r, msg))
    }

    override fun createAnnotation(): Annotation {
        create()
        // The caller in the real annotators always uses create(); an Annotation instance is never consumed.
        throw UnsupportedOperationException("RecordingAnnotationBuilder does not produce Annotation instances")
    }

    /** A [AnnotationBuilder.FixBuilder] that drops every fix registration; the recording run never needs fixes. */
    private class NoOpFixBuilder(private val parent: AnnotationBuilder) : AnnotationBuilder.FixBuilder {
        override fun range(range: TextRange): AnnotationBuilder.FixBuilder = this
        override fun key(key: HighlightDisplayKey): AnnotationBuilder.FixBuilder = this
        override fun batch(): AnnotationBuilder.FixBuilder = this
        override fun universal(): AnnotationBuilder.FixBuilder = this
        override fun registerFix(): AnnotationBuilder = parent
    }
}
