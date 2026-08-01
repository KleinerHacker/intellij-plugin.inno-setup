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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor

import com.intellij.ide.navigationToolbar.StructureAwareNavBarModelExtension
import com.intellij.lang.Language
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptLanguage
import javax.swing.Icon

/**
 * Provides navigation bar presentation for Inno Setup structure elements.
 */
class IsStructureAwareNavbar : StructureAwareNavBarModelExtension() {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    override val language: Language
        get() = IsScriptLanguage

    /**
     * Returns presentation metadata used by IntelliJ navigation UI.
     */
    override fun getPresentableText(obj: Any?): String? =
        IsElementPresentation.textOf(hostOf(obj) ?: obj)

    /**
     * Returns the icon shown for this element or file type.
     */
    override fun getIcon(obj: Any?): Icon? = IsElementPresentation.iconOf(obj)

    /**
     * Keeps the navigation bar inside the host script when the caret sits on a `#…` line.
     *
     * ISPP is injected into the preprocessor lines, so the element at the caret belongs to the injected file
     * whose virtual file is a window into the host — and the platform's default extension presents a file by
     * its virtual file name, which for such a window is `<Injected ISPP file>`. Mapping the fragment back to
     * its host script makes a preprocessor line behave like every other line of the script.
     */
    override fun adjustElement(psiElement: PsiElement): PsiElement = hostOf(psiElement) ?: psiElement

    /**
     * The host script element of [obj] if it belongs to an ISPP fragment injected into an Inno Setup script,
     * `null` for anything else (including elements that already live in the host).
     */
    private fun hostOf(obj: Any?): PsiElement? {
        val element = obj as? PsiElement ?: return null
        val file = element.containingFile ?: return null
        val manager = InjectedLanguageManager.getInstance(element.project)
        if (!manager.isInjectedFragment(file)) return null

        val hostFile = manager.getTopLevelFile(element) as? IsScriptFile ?: return null
        return if (element is PsiFile) hostFile else manager.getInjectionHost(element) ?: hostFile
    }
}
