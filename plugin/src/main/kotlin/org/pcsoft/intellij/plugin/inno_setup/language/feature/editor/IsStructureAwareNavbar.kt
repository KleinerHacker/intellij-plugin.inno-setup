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
    override fun getPresentableText(obj: Any?): String? = IsElementPresentation.textOf(obj)

    /**
     * Returns the icon shown for this element or file type.
     */
    override fun getIcon(obj: Any?): Icon? = IsElementPresentation.iconOf(obj)
}
