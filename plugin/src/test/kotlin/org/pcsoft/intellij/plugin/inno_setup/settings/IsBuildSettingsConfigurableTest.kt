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
import com.intellij.ui.components.JBTextField
import org.pcsoft.intellij.plugin.inno_setup.build.IsBuildOutputMode
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfiguration
import org.pcsoft.intellij.plugin.inno_setup.build.config.IsBuildConfigurationService
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import java.io.File

/**
 * Tests the project-level [IsBuildSettingsConfigurable] UI model: combo population, enabled-state,
 * and the isModified/apply/reset round-trip against the per-project [IsBuildSettingsService].
 */
class IsBuildSettingsConfigurableTest : IsTimedBasePlatformTestCase() {

    private lateinit var configurable: IsBuildSettingsConfigurable
    private var previousCompile: Boolean = true
    private var previousMode: String? = null

    private val state get() = IsBuildSettingsService.getInstance(project).state

    private lateinit var configDirectory: File

    private val configService get() = IsBuildConfigurationService.getInstance(project)

    override fun setUp() {
        super.setUp()
        previousCompile = state.compileOnBuild
        previousMode = state.outputMode
        configDirectory = File.createTempFile("inno-settings", "").let { probe ->
            probe.delete()
            File(probe.parentFile, probe.name + "-" + IsBuildConfigurationService.DIRECTORY_NAME)
        }
        configService.directoryOverride = configDirectory
        configurable = IsBuildSettingsConfigurable(project)
        configurable.createComponent()
    }

    override fun tearDown() {
        try {
            configurable.disposeUIResources()
            state.compileOnBuild = previousCompile
            state.outputMode = previousMode
            configService.directoryOverride = null
            configDirectory.listFiles()?.forEach { it.delete() }
            configDirectory.delete()
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

    // ── build configurations ─────────────────────────────────────────────────────

    private val configCombo get() = field<ComboBox<String>>("configCombo")
    private val symbolsField get() = field<JBTextField>("symbolsField")
    private val optionsField get() = field<JBTextField>("optionsField")

    /** Opening the page on a fresh project offers the starter configurations. */
    fun testResetOffersStarterConfigurations() {
        configurable.reset()

        val names = (0 until configCombo.itemCount).map { configCombo.getItemAt(it) }
        assertTrue(IsBuildConfiguration.DEFAULT_RELEASE_NAME in names)
        assertTrue(IsBuildConfiguration.DEFAULT_DEBUG_NAME in names)
    }

    /** The detail fields show the selected configuration. */
    fun testDetailsFollowSelection() {
        configService.replaceAll(listOf(IsBuildConfiguration("Only", "DEBUG", "dist", "/Q")))
        configurable.reset()

        assertEquals("DEBUG", symbolsField.text)
        assertEquals("/Q", optionsField.text)
    }

    /** Editing a detail field marks the page modified and is written on apply. */
    fun testEditingSymbolsIsAppliedToDisk() {
        configService.replaceAll(listOf(IsBuildConfiguration("Only", "DEBUG")))
        configurable.reset()
        assertFalse(configurable.isModified)

        symbolsField.text = "DEBUG,TRACE"
        assertTrue(configurable.isModified)

        configurable.apply()
        assertEquals("DEBUG,TRACE", configService.byName("Only")?.compilerSymbols)
    }

    /** Nothing is written before apply, so cancelling the dialog leaves the files untouched. */
    fun testEditingIsNotWrittenBeforeApply() {
        configService.replaceAll(listOf(IsBuildConfiguration("Only", "DEBUG")))
        configurable.reset()

        symbolsField.text = "CHANGED"

        assertEquals("DEBUG", configService.byName("Only")?.compilerSymbols)
    }

    /** After apply the page is unmodified again. */
    fun testApplyClearsModified() {
        configService.replaceAll(listOf(IsBuildConfiguration("Only", "DEBUG")))
        configurable.reset()
        symbolsField.text = "OTHER"
        configurable.apply()

        assertFalse(configurable.isModified)
    }
}
