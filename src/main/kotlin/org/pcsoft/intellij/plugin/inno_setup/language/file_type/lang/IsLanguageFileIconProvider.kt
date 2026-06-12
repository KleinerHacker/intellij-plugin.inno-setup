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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang

import com.intellij.ide.FileIconProvider
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiManager
import org.pcsoft.intellij.plugin.inno_setup.language.feature.IsLanguageIconHelper
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsIcons
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import javax.swing.Icon

/**
 * Decorates ISL file icons in the project tree with a 25 % flag-overlay (bottom-right) that
 * reflects the language declared in the file's `[LangOptions] LanguageID` directive.
 */
class IsLanguageFileIconProvider : FileIconProvider {

    override fun getIcon(file: VirtualFile, flags: Int, project: Project?): Icon? {
        if (file.extension?.equals("isl", ignoreCase = true) != true) return null
        project ?: return null
        val psiFile = PsiManager.getInstance(project).findFile(file) as? IsScriptFile ?: return null
        return IsLanguageIconHelper.baseWithFlagOverlay(IsIcons.ScriptFile, psiFile)
    }
}
