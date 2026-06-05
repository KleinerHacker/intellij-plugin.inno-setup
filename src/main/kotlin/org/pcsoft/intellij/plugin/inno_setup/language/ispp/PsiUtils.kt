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

package org.pcsoft.intellij.plugin.inno_setup.language.ispp

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiIsppLine
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirective
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx

// ── IssFile (ISPP-Brücke) ──────────────────────────────────────────────────────

fun IssFile.isppDirectives(): List<IsppDirective> {
    val mgr = InjectedLanguageManager.getInstance(project)
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IsiIsppLine::class.java)
        .flatMap { line ->
            val result = mutableListOf<IsppDirective>()
            mgr.enumerate(line) { injectedPsi, _ ->
                if (injectedPsi is IsppFile) {
                    result.addAll(PsiTreeUtil.getChildrenOfTypeAsList(injectedPsi, IsppDirective::class.java))
                }
            }
            result
        }
}

/**
 * All ISPP directives paired with the host-file offset of the line they live on.
 * Because each `#define` line is injected as its own fragment, the host offset (the start of the
 * containing [IsiIsppLine], a direct child of this file) is the authority for declaration order.
 */
fun IssFile.isppDirectivesWithHostOffset(): List<Pair<IsppDirective, Int>> {
    val mgr = InjectedLanguageManager.getInstance(project)
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IsiIsppLine::class.java)
        .flatMap { line ->
            val result = mutableListOf<Pair<IsppDirective, Int>>()
            mgr.enumerate(line) { injectedPsi, _ ->
                if (injectedPsi is IsppFile) {
                    PsiTreeUtil.getChildrenOfTypeAsList(injectedPsi, IsppDirective::class.java)
                        .forEach { result.add(it to line.textRange.startOffset) }
                }
            }
            result
        }
}

fun IssFile.definedConstants(): List<Pair<String, String?>> =
    isppDirectives()
        .filter { (it as? IsppDirectiveEx)?.isDefine() == true }
        .mapNotNull { directive ->
            val ex = directive as? IsppDirectiveEx ?: return@mapNotNull null
            val name = ex.getDefineName()?.ifEmpty { null } ?: return@mapNotNull null
            name to ex.getDefineValue()
        }