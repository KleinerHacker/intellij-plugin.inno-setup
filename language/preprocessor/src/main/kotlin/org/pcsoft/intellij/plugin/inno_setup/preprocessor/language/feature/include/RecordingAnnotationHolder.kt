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

import com.intellij.lang.annotation.HighlightSeverity

/**
 * An [IsAnnotationSink] that captures the problems the real annotators would raise — without producing any
 * editor markup. Every `newAnnotation`/`newSilentAnnotation` is routed into a [RecordingAnnotationBuilder]; the
 * collected ERROR/WARNING [RecordedProblem]s are exposed via [problems].
 *
 * Deliberately implements only the plugin-owned [IsAnnotationSink] façade — never the platform's
 * `@NonExtendable` `AnnotationHolder` — so the effective-script analysis can replay the annotators safely.
 */
class RecordingAnnotationHolder : IsAnnotationSink {

    private val collected = mutableListOf<RecordedProblem>()

    /** The ERROR/WARNING problems recorded so far, in encounter order. */
    val problems: List<RecordedProblem> get() = collected

    override fun newAnnotation(severity: HighlightSeverity, message: String): IsAnnotationDraft =
        RecordingAnnotationBuilder(severity, message) { collected += it }

    override fun newSilentAnnotation(severity: HighlightSeverity): IsAnnotationDraft =
        RecordingAnnotationBuilder(severity, null) { collected += it }
}
