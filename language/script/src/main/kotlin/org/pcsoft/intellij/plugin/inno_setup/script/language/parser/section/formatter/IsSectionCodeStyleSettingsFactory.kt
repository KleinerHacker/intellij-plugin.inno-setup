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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.formatter

import com.intellij.application.options.CodeStyleAbstractConfigurable
import com.intellij.application.options.CodeStyleAbstractPanel
import com.intellij.application.options.TabbedLanguageCodeStylePanel
import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleConfigurable
import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import com.intellij.psi.codeStyle.CustomCodeStyleSettings
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptLanguage

/**
 * The `codeStyleSettingsProvider` half of the *Settings ▸ Editor ▸ Code Style ▸ Inno Setup* page. Binding a
 * [CodeStyleSettingsProvider] to the language via [getLanguage] and returning a real [createConfigurable] is
 * what promotes Inno Setup to its **own top-level node** — without it the platform would file the language's
 * options under the generic *Other Languages* group.
 *
 * It also materialises the [IsSectionCodeStyleSettings] instance on every [CodeStyleSettings]
 * ([createCustomSettings]). The actual option checkboxes and the live-preview code sample are contributed by
 * the companion [IsSectionCodeStyleSettingsProvider] ([com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider]),
 * which feeds the *Spaces* and *Blank Lines* tabs of the panel built here.
 */
class IsSectionCodeStyleSettingsFactory : CodeStyleSettingsProvider() {

    override fun createCustomSettings(settings: CodeStyleSettings): CustomCodeStyleSettings =
        IsSectionCodeStyleSettings(settings)

    override fun getLanguage(): Language = IsScriptLanguage

    override fun getConfigurableDisplayName(): String = PluginBundle.message("code_style.display_name")

    override fun createConfigurable(
        baseSettings: CodeStyleSettings,
        modelSettings: CodeStyleSettings,
    ): CodeStyleConfigurable =
        object : CodeStyleAbstractConfigurable(baseSettings, modelSettings, getConfigurableDisplayName()) {
            override fun createPanel(settings: CodeStyleSettings): CodeStyleAbstractPanel =
                object : TabbedLanguageCodeStylePanel(IsScriptLanguage, currentSettings, settings) {
                    // Only the two tabs that actually carry Inno Setup options.
                    override fun initTabs(settings: CodeStyleSettings) {
                        addSpacesTab(settings)
                        addBlankLinesTab(settings)
                    }
                }
        }
}
