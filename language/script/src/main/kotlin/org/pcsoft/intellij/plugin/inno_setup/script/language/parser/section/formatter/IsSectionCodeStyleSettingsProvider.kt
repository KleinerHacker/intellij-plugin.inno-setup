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

import com.intellij.lang.Language
import com.intellij.psi.codeStyle.CodeStyleSettingsCustomizable
import com.intellij.psi.codeStyle.LanguageCodeStyleSettingsProvider
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptLanguage

/**
 * Contributes the *Settings ▸ Editor ▸ Code Style ▸ Inno Setup* page and — via [createCustomSettings] — the
 * [IsSectionCodeStyleSettings] instance the formatter reads. It lives in `:language:script` (rather than in
 * `:plugin`) because the custom settings are a language-level concern: the formatter and its tests depend on
 * them, so they must be registered by the same fragment that registers the formatter itself.
 *
 * Every option (each defaulting to on) is shown as a check box under the *Spacing* and *Blank Lines* tabs; the
 * live preview is fed with a representative script sample.
 */
class IsSectionCodeStyleSettingsProvider : LanguageCodeStyleSettingsProvider() {

    override fun getLanguage(): Language = IsScriptLanguage

    override fun getConfigurableDisplayName(): String = PluginBundle.message("code_style.display_name")

    override fun customizeSettings(consumer: CodeStyleSettingsCustomizable, settingsType: SettingsType) {
        when (settingsType) {
            SettingsType.SPACING_SETTINGS -> {
                val group = PluginBundle.message("code_style.group")
                val ppGroup = PluginBundle.message("code_style.group.preprocessor")
                consumer.showCustomOption(
                    IsSectionCodeStyleSettings::class.java, "NO_SPACE_INSIDE_BRACKETS",
                    PluginBundle.message("code_style.no_space_inside_brackets"), group,
                )
                consumer.showCustomOption(
                    IsSectionCodeStyleSettings::class.java, "SPACE_AROUND_ASSIGN",
                    PluginBundle.message("code_style.space_around_assign"), group,
                )
                consumer.showCustomOption(
                    IsSectionCodeStyleSettings::class.java, "SPACE_AFTER_COLON",
                    PluginBundle.message("code_style.space_after_colon"), group,
                )
                consumer.showCustomOption(
                    IsSectionCodeStyleSettings::class.java, "SPACE_AFTER_SEMICOLON",
                    PluginBundle.message("code_style.space_after_semicolon"), group,
                )
                consumer.showCustomOption(
                    IsSectionCodeStyleSettings::class.java, "TRIM_LEADING_KEY_SPACE",
                    PluginBundle.message("code_style.trim_leading_key_space"), group,
                )
                consumer.showCustomOption(
                    IsSectionCodeStyleSettings::class.java, "SPACE_AROUND_PP_OPERATORS",
                    PluginBundle.message("code_style.space_around_pp_operators"), ppGroup,
                )
            }

            SettingsType.BLANK_LINES_SETTINGS -> {
                consumer.showCustomOption(
                    IsSectionCodeStyleSettings::class.java, "BLANK_LINE_BETWEEN_SECTIONS",
                    PluginBundle.message("code_style.blank_line_between_sections"),
                    PluginBundle.message("code_style.group"),
                )
            }

            else -> {}
        }
    }

    override fun getCodeSample(settingsType: SettingsType): String = CODE_SAMPLE

    private companion object {
        val CODE_SAMPLE = """
            |[ Setup ]
            | AppName=My Program
            | AppVersion=1.5
            |
            |#define FeatureCount 2+3
            |
            |[ Files ]
            | Source:"app.exe";DestDir:"{app}";Flags:ignoreversion
            | Source:"readme.txt";DestDir:"{app}"
        """.trimMargin("|")
    }
}
