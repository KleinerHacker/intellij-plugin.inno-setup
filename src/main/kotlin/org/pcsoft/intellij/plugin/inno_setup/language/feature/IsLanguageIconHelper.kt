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

package org.pcsoft.intellij.plugin.inno_setup.language.feature

import com.intellij.icons.AllIcons
import com.intellij.openapi.components.service
import com.intellij.ui.LayeredIcon
import com.intellij.util.IconUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.findSection
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.valueText
import org.pcsoft.intellij.plugin.inno_setup.services.IsLanguageDataService
import javax.swing.Icon
import kotlin.math.roundToInt

/**
 * Shared utilities for resolving language flag icons from ISL files.
 */
object IsLanguageIconHelper {

    /** Flag icon for [psiFile] based on its `[LangOptions] LanguageID`, or a globe fallback. */
    fun flagIconForIslFile(psiFile: IsScriptFile?): Icon {
        psiFile ?: return AllIcons.General.Web
        val langOptions = psiFile.findSection("LangOptions") ?: return AllIcons.General.Web
        val langIdText = langOptions.directiveEntryList
            .firstOrNull { it.keyText().equals("LanguageID", ignoreCase = true) }
            ?.valueText?.trim() ?: return AllIcons.General.Web
        val numericId = IsLanguageDataService.parseId(langIdText) ?: return AllIcons.General.Web
        val spec = service<IsLanguageDataService>().fromId(numericId) ?: return AllIcons.General.Web
        return spec.icon
    }

    /**
     * Creates a composite icon: [base] with a 25 % flag-overlay at the bottom-right corner.
     * Returns `null` when the flag icon cannot be determined (no overlay needed).
     */
    fun baseWithFlagOverlay(base: Icon, psiFile: IsScriptFile?): Icon? {
        psiFile ?: return null
        val langOptions = psiFile.findSection("LangOptions") ?: return null
        val langIdText = langOptions.directiveEntryList
            .firstOrNull { it.keyText().equals("LanguageID", ignoreCase = true) }
            ?.valueText?.trim() ?: return null
        val numericId = IsLanguageDataService.parseId(langIdText) ?: return null
        val spec = service<IsLanguageDataService>().fromId(numericId) ?: return null
        if (spec.flag == null) return null

        val flagIcon = spec.icon
        val overlaySize = maxOf(4, (base.iconWidth * 0.25).roundToInt())
        val scaled = IconUtil.scale(flagIcon, null, overlaySize.toFloat() / flagIcon.iconWidth)
        val layered = LayeredIcon(2)
        layered.setIcon(base, 0)
        layered.setIcon(scaled, 1, base.iconWidth - overlaySize, base.iconHeight - overlaySize)
        return layered
    }
}
