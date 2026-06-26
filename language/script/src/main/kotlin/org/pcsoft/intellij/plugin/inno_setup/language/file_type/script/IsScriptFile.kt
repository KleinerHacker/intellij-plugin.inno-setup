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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.script

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.feature.include.IsEffectiveScriptProblems
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorHost
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorIncludeProblem
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionConstant

/**
 * PSI file for Inno Setup script files (`.iss`). Implements [IsPreprocessorHost] so the (lower-layer) ISPP
 * preprocessor module can ask the script for its two host-aware behaviours without depending on section PSI.
 */
open class IsScriptFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, IsScriptLanguage), IsPreprocessorHost {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getFileType(): FileType = IsScriptFileType.INSTANCE

    /** A `#define`/`#dim` name is "used" when referenced as a `{#Name}` constant anywhere in this script. */
    override fun isPreprocessorNameReferencedAsConstant(name: String): Boolean =
        PsiTreeUtil.findChildrenOfType(this, IsSectionConstant::class.java).any { constant ->
            val body = constant.constantBody.text.trim()
            body.startsWith("#") && body.trimStart('#').trim().equals(name, ignoreCase = true)
        }

    /** Problems an `#include` introduces into the combined effective script (flag conflicts, missing files, …). */
    override fun includeProblems(directive: IsPreprocessorDirective): List<IsPreprocessorIncludeProblem> =
        IsEffectiveScriptProblems.forHost(this)[directive]
            ?.map { IsPreprocessorIncludeProblem(it.severity, it.message) }
            ?: emptyList()
}
