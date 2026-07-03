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
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement

/**
 * Minimal annotation façade that the Inno Setup annotators target instead of the platform's
 * [AnnotationHolder]/[com.intellij.lang.annotation.AnnotationBuilder]. Those platform types are
 * `@ApiStatus.NonExtendable`, so they must not be *implemented* by the plugin — but they may be
 * *used*. During a real highlighting pass the annotators run against [PlatformAnnotationSink], which
 * delegates to the platform holder it receives; the effective-script analysis instead replays the
 * annotators against a [RecordingAnnotationHolder] to harvest their problems without any editor markup.
 */
interface IsAnnotationSink {
    fun newAnnotation(severity: HighlightSeverity, message: String): IsAnnotationDraft
    fun newSilentAnnotation(severity: HighlightSeverity): IsAnnotationDraft
}

/** Fluent builder counterpart of [IsAnnotationSink]; mirrors the subset of `AnnotationBuilder` the annotators use. */
interface IsAnnotationDraft {
    fun range(range: TextRange): IsAnnotationDraft
    fun range(element: PsiElement): IsAnnotationDraft
    fun range(node: ASTNode): IsAnnotationDraft
    fun textAttributes(key: TextAttributesKey): IsAnnotationDraft
    fun withFix(fix: IntentionAction): IsAnnotationDraft
    fun fileLevel(): IsAnnotationDraft
    fun create()
}

/**
 * Adapts a platform [AnnotationHolder] to [IsAnnotationSink]. This *uses* the platform annotation API
 * (passed in by the IDE during the annotation pass) and never implements its `@NonExtendable` types.
 */
class PlatformAnnotationSink(private val holder: AnnotationHolder) : IsAnnotationSink {
    override fun newAnnotation(severity: HighlightSeverity, message: String): IsAnnotationDraft =
        PlatformAnnotationDraft(holder.newAnnotation(severity, message))

    override fun newSilentAnnotation(severity: HighlightSeverity): IsAnnotationDraft =
        PlatformAnnotationDraft(holder.newSilentAnnotation(severity))

    private class PlatformAnnotationDraft(
        private var builder: com.intellij.lang.annotation.AnnotationBuilder,
    ) : IsAnnotationDraft {
        override fun range(range: TextRange): IsAnnotationDraft = apply { builder = builder.range(range) }
        override fun range(element: PsiElement): IsAnnotationDraft = apply { builder = builder.range(element) }
        override fun range(node: ASTNode): IsAnnotationDraft = apply { builder = builder.range(node) }
        override fun textAttributes(key: TextAttributesKey): IsAnnotationDraft =
            apply { builder = builder.textAttributes(key) }

        override fun withFix(fix: IntentionAction): IsAnnotationDraft = apply { builder = builder.withFix(fix) }
        override fun fileLevel(): IsAnnotationDraft = apply { builder = builder.fileLevel() }
        override fun create() = builder.create()
    }
}
