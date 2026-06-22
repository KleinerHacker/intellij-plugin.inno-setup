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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.include

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.CachedValueProvider
import com.intellij.psi.util.CachedValuesManager
import com.intellij.psi.util.PsiModificationTracker
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorAnnotator
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.IsSectionAnnotator
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionPreprocessorLine

/**
 * Validates a script the way it actually compiles: as the **combined** effective script, with every resolved
 * `#include` replaced by the included text. The real section and preprocessor annotators are replayed over the
 * in-memory effective file via a [RecordingAnnotationHolder], and the resulting ERROR/WARNING problems are
 * mapped — through the effective script's source map — onto the topmost host `#include` that pulled the
 * offending line in.
 *
 * Problems in text that belongs to the host (main) file itself are dropped: those lines already run through the
 * normal annotator pass on the real file. So only problems that exist **because of** an inclusion (and only in
 * the combined context) are reported, each on its triggering `#include`. The result is cached per host file.
 */
object IsEffectiveScriptProblems {

    /** ERROR/WARNING problems introduced by `#include`s, grouped by the host directive that introduced them. */
    fun forHost(hostFile: IsScriptFile): Map<IsPreprocessorDirective, List<RecordedProblem>> =
        CachedValuesManager.getCachedValue(hostFile) {
            CachedValueProvider.Result.create(compute(hostFile), PsiModificationTracker.MODIFICATION_COUNT)
        }

    private fun compute(hostFile: IsScriptFile): Map<IsPreprocessorDirective, List<RecordedProblem>> {
        val attributed = hostFile.toAttributedEffectiveScript() ?: return emptyMap()
        val effective = attributed.file

        // Problems recorded in the coordinate system of the effective file.
        val problems = collectSectionProblems(effective) + collectPreprocessorProblems(hostFile, effective)

        // Attribute each problem to the topmost host #include that produced its line; drop host-text problems.
        val result = HashMap<IsPreprocessorDirective, MutableList<RecordedProblem>>()
        problems.forEach { problem ->
            val directive = attributed.originAt(problem.range.startOffset)?.element ?: return@forEach
            result.getOrPut(directive) { mutableListOf() } += problem
        }
        return result
    }

    /** Replays [IsSectionAnnotator] over every element of [effective] except the file node itself. */
    private fun collectSectionProblems(effective: IsScriptFile): List<RecordedProblem> {
        val holder = RecordingAnnotationHolder(effective)
        val annotator = IsSectionAnnotator()
        // The file-level element triggers mandatory-section/effective-script checks that don't belong here (and
        // would recurse into the effective machinery); its problems are file-level and dropped anyway — skip it.
        PsiTreeUtil.processElements(effective) { element ->
            if (element !== effective) safeAnnotate { annotator.annotate(element, holder) }
            true
        }
        return holder.problems
    }

    /**
     * Replays [IsPreprocessorAnnotator] over the ISPP injected into the remaining `#…` lines of [effective],
     * translating the injected ranges back into effective-file offsets.
     */
    private fun collectPreprocessorProblems(hostFile: IsScriptFile, effective: IsScriptFile): List<RecordedProblem> {
        val injMgr = InjectedLanguageManager.getInstance(hostFile.project)
        val annotator = IsPreprocessorAnnotator()
        val out = mutableListOf<RecordedProblem>()

        PsiTreeUtil.getChildrenOfTypeAsList(effective, IsSectionPreprocessorLine::class.java).forEach { line ->
            injMgr.enumerate(line) { injectedPsi, _ ->
                if (injectedPsi !is IsPreprocessorFile) return@enumerate
                val holder = RecordingAnnotationHolder(injectedPsi)
                PsiTreeUtil.processElements(injectedPsi) { element ->
                    safeAnnotate { annotator.annotate(element, holder) }
                    true
                }
                holder.problems.forEach { problem ->
                    val hostRange = runCatching { injMgr.injectedToHost(injectedPsi, problem.range) }.getOrNull()
                        ?: return@forEach
                    out += problem.copy(range = hostRange)
                }
            }
        }
        return out
    }

    /**
     * Runs a single [annotate] call defensively: the effective file is backed by an in-memory `LightVirtualFile`
     * without a real parent, so individual checks (ISL/MessagesFile existence, …) may throw. Such isolated
     * failures must not abort the whole recording run.
     */
    private inline fun safeAnnotate(annotate: () -> Unit) {
        try {
            annotate()
        } catch (_: Exception) {
            // Ignore: one failing element check must not stop the combined analysis.
        }
    }
}
