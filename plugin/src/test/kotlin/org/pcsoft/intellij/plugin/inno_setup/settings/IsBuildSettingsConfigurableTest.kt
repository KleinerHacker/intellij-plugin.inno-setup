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

import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.components.JBCheckBox
import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputMode
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests the project-level [IsBuildSettingsConfigurable] UI model: combo population, enabled-state,
 * and the isModified/apply/reset round-trip against the per-project [IsBuildSettingsService].
 */
class IsBuildSettingsConfigurableTest : IsTimedBasePlatformTestCase() {

    private lateinit var configurable: IsBuildSettingsConfigurable
    private var previousCompile: Boolean = true
    private var previousMode: String? = null

    private val state get() = IsBuildSettingsService.getInstance(project).state

    override fun setUp() {
        super.setUp()
        previousCompile = state.compileOnBuild
        previousMode = state.outputMode
        configurable = IsBuildSettingsConfigurable(project)
        configurable.createComponent()
    }

    override fun tearDown() {
        try {
            configurable.disposeUIResources()
            state.compileOnBuild = previousCompile
            state.outputMode = previousMode
        } finally {
            super.tearDown()
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun <T> field(name: String): T {
        val f = IsBuildSettingsConfigurable::class.java.getDeclaredField(name).apply { isAccessible = true }
        return f.get(configurable) as T
    }

    private val checkBox get() = field<JBCheckBox>("compileOnBuildCheck")
    private val combo get() = field<ComboBox<IsBuildOutputMode>>("outputModeCombo")

    fun testComboContainsAllModes() {
        assertEquals(IsBuildOutputMode.entries.size, combo.itemCount)
    }

    fun testResetReflectsProjectState() {
        state.compileOnBuild = true
        state.outputMode = IsBuildOutputMode.DRY.name
        configurable.reset()
        assertTrue(checkBox.isSelected)
        assertEquals(IsBuildOutputMode.DRY, combo.selectedItem)
        assertTrue(combo.isEnabled)
    }

    fun testComboDisabledWhenCompileOff() {
        state.compileOnBuild = false
        configurable.reset()
        assertFalse(combo.isEnabled)
    }

    fun testIsModifiedAndApplyPersistToProjectState() {
        state.compileOnBuild = true
        state.outputMode = IsBuildOutputMode.BUILD_DIR.name
        configurable.reset()
        assertFalse(configurable.isModified)

        combo.selectedItem = IsBuildOutputMode.SCRIPT
        assertTrue(configurable.isModified)

        configurable.apply()
        assertEquals(IsBuildOutputMode.SCRIPT.name, state.outputMode)
        configurable.reset()
        assertFalse(configurable.isModified)
    }
}
