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

import com.intellij.lang.ASTNode
import com.intellij.lang.annotation.*
import com.intellij.lang.annotation.Annotation
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile

/**
 * An [AnnotationHolder] that captures the problems the real annotators would raise — without producing any
 * editor markup. Every `newAnnotation`/`newSilentAnnotation` is routed into a [RecordingAnnotationBuilder]; the
 * collected ERROR/WARNING [RecordedProblem]s are exposed via [problems].
 *
 * Deliberately implements only the modern builder entry points; the deprecated `createXxxAnnotation` factory
 * methods are unused by the annotators replayed here and throw if called.
 */
class RecordingAnnotationHolder(private val file: PsiFile) : AnnotationHolder {

    private val collected = mutableListOf<RecordedProblem>()

    /** The ERROR/WARNING problems recorded so far, in encounter order. */
    val problems: List<RecordedProblem> get() = collected

    override fun newAnnotation(severity: HighlightSeverity, message: String): AnnotationBuilder =
        RecordingAnnotationBuilder(severity, message) { collected += it }

    override fun newSilentAnnotation(severity: HighlightSeverity): AnnotationBuilder =
        RecordingAnnotationBuilder(severity, null) { collected += it }

    override fun getCurrentAnnotationSession(): AnnotationSession = AnnotationSession(file)

    override fun isBatchMode(): Boolean = false

    // ── Deprecated factory API — not used by the replayed annotators. ──────────────────────────────────
    private fun unsupported(): Nothing =
        throw UnsupportedOperationException("RecordingAnnotationHolder only supports newAnnotation/newSilentAnnotation")

    override fun createErrorAnnotation(elt: PsiElement, message: String?): Annotation = unsupported()
    override fun createErrorAnnotation(node: ASTNode, message: String?): Annotation = unsupported()
    override fun createErrorAnnotation(range: TextRange, message: String?): Annotation = unsupported()
    override fun createWarningAnnotation(elt: PsiElement, message: String?): Annotation = unsupported()
    override fun createWarningAnnotation(node: ASTNode, message: String?): Annotation = unsupported()
    override fun createWarningAnnotation(range: TextRange, message: String?): Annotation = unsupported()
    override fun createWeakWarningAnnotation(elt: PsiElement, message: String?): Annotation = unsupported()
    override fun createWeakWarningAnnotation(range: TextRange, message: String?): Annotation = unsupported()
    override fun createInfoAnnotation(elt: PsiElement, message: String?): Annotation = unsupported()
    override fun createInfoAnnotation(node: ASTNode, message: String?): Annotation = unsupported()
    override fun createInfoAnnotation(range: TextRange, message: String?): Annotation = unsupported()
    override fun createAnnotation(severity: HighlightSeverity, range: TextRange, message: String?): Annotation =
        unsupported()

    override fun createAnnotation(
        severity: HighlightSeverity,
        range: TextRange,
        message: String?,
        htmlTooltip: String?,
    ): Annotation = unsupported()
}
