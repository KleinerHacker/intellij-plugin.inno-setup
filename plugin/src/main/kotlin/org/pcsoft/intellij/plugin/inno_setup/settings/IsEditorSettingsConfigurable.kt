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

package org.pcsoft.intellij.plugin.inno_setup.settings

import com.intellij.openapi.options.SearchableConfigurable
import com.intellij.ui.components.JBCheckBox
import com.intellij.ui.dsl.builder.panel
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.PluginBundle
import org.pcsoft.intellij.plugin.inno_setup.script.settings.IsSettingsService
import javax.swing.JComponent

/**
 * IDE-wide editor settings sub-page ("Inno Setup | Editor") for how `.iss` scripts are presented in the
 * editor. Currently offers the toggle for the `#include` content inlay hints. Stored application-wide in
 * [IsSettingsState] via [IsSettingsService].
 */
class IsEditorSettingsConfigurable : SearchableConfigurable {

    private val state get() = IsSettingsService.getInstance().state

    private var includeContentCheck: JBCheckBox? = null

    /**
     * Returns the stable settings page id used by IntelliJ search.
     */
    override fun getId() = "org.pcsoft.intellij.plugin.inno_setup.settings.editor"

    /**
     * Returns the display name shown in the Settings tree.
     */
    override fun getDisplayName() = PluginBundle.message("settings.editor.display_name")

    /**
     * Builds the Swing component for the editor settings sub-page.
     */
    override fun createComponent(): JComponent {
        val check = JBCheckBox(PluginBundle.message("settings.editor.inlay.include_content"))
        includeContentCheck = check

        return panel {
            group(PluginBundle.message("settings.editor.inlay.group")) {
                row { cell(check) }
                row { comment(PluginBundle.message("settings.editor.inlay.include_content.comment")) }
            }
        }
    }

    /**
     * Checks whether the current UI values differ from the persisted settings.
     */
    override fun isModified(): Boolean =
        includeContentCheck?.isSelected != state.showIncludeContentInlayHints

    /**
     * Stores the current UI values in the persistent settings state.
     */
    override fun apply() {
        state.showIncludeContentInlayHints = includeContentCheck?.isSelected ?: true
    }

    /**
     * Restores UI values from the persistent settings state.
     */
    override fun reset() {
        includeContentCheck?.isSelected = state.showIncludeContentInlayHints
    }

    /**
     * Releases Swing component references owned by this configurable.
     */
    override fun disposeUIResources() {
        includeContentCheck = null
    }
}
