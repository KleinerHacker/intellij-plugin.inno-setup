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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionPreprocessorLine

// ── IsScriptFile (ISPP-Brücke) ──────────────────────────────────────────────────────

val IsScriptFile.isppDirectives: List<IsPreprocessorDirective>
    get() {
        val mgr = InjectedLanguageManager.getInstance(project)
        return PsiTreeUtil.getChildrenOfTypeAsList(this, IsSectionPreprocessorLine::class.java)
            .flatMap { line ->
                val result = mutableListOf<IsPreprocessorDirective>()
                mgr.enumerate(line) { injectedPsi, _ ->
                    if (injectedPsi is IsPreprocessorFile) {
                        result.addAll(
                            PsiTreeUtil.getChildrenOfTypeAsList(
                                injectedPsi,
                                IsPreprocessorDirective::class.java
                            )
                        )
                    }
                }
                result
            }
    }

/**
 * All ISPP directives paired with the host-file offset of the line they live on.
 * Because each `#define` line is injected as its own fragment, the host offset (the start of the
 * containing [IsSectionPreprocessorLine], a direct child of this file) is the authority for declaration order.
 */
val IsScriptFile.isppDirectivesWithHostOffset: List<Pair<IsPreprocessorDirective, Int>>
    get() {
        val mgr = InjectedLanguageManager.getInstance(project)
        return PsiTreeUtil.getChildrenOfTypeAsList(this, IsSectionPreprocessorLine::class.java)
            .flatMap { line ->
                val result = mutableListOf<Pair<IsPreprocessorDirective, Int>>()
                mgr.enumerate(line) { injectedPsi, _ ->
                    if (injectedPsi is IsPreprocessorFile) {
                        PsiTreeUtil.getChildrenOfTypeAsList(injectedPsi, IsPreprocessorDirective::class.java)
                            .forEach { result.add(it to line.textRange.startOffset) }
                    }
                }
                result
            }
    }

val IsScriptFile.definedConstants: List<Pair<String, String?>>
    get() = isppDirectives
        .filter { (it as? IsPreprocessorDirectiveEx)?.isDefine() == true }
        .mapNotNull { directive ->
            val ex = directive as? IsPreprocessorDirectiveEx ?: return@mapNotNull null
            val name = ex.getDefineName()?.ifEmpty { null } ?: return@mapNotNull null
            name to ex.getDefineValue()
        }