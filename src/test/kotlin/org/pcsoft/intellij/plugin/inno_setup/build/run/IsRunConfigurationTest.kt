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

package org.pcsoft.intellij.plugin.inno_setup.build.run

import com.intellij.execution.configurations.RuntimeConfigurationException
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import java.io.File.createTempFile

class IsRunConfigurationTest : BasePlatformTestCase() {

    private fun config(): IsRunConfiguration {
        val type = IsRunConfigurationType()
        return IsRunConfiguration(project, type.configurationFactories.first(), "test")
    }

    fun testValidationFailsForBlankScriptPath() {
        val cfg = config()
        cfg.scriptPath = ""
        assertThrows(RuntimeConfigurationException::class.java) { cfg.checkConfiguration() }
    }

    fun testValidationFailsForMissingFile() {
        val cfg = config()
        cfg.scriptPath = "/nonexistent/path/setup.iss"
        assertThrows(RuntimeConfigurationException::class.java) { cfg.checkConfiguration() }
    }

    fun testValidationFailsForUninstallWithoutDir() {
        val cfg = config()
        val f = myFixture.addFileToProject("setup.iss", "[Setup]\n").virtualFile
        cfg.scriptPath = f.path
        cfg.actionType = IsRunActionType.UNINSTALL
        cfg.uninstallerDir = ""
        assertThrows(RuntimeConfigurationException::class.java) { cfg.checkConfiguration() }
    }

    fun testValidationPassesForValidInstallConfig() {
        val cfg = config()
        // Use a temp file on the real FS so File.isFile() returns true.
        val tmpFile = createTempFile("setup", ".iss")
        tmpFile.deleteOnExit()
        try {
            cfg.scriptPath = tmpFile.absolutePath
            cfg.actionType = IsRunActionType.INSTALL
            cfg.checkConfiguration() // must not throw
        } finally {
            tmpFile.delete()
        }
    }

    fun testDefaultValues() {
        val cfg = config()
        assertEquals(IsRunMode.DRY, cfg.runMode)
        assertEquals(IsRunActionType.INSTALL, cfg.actionType)
        assertTrue(cfg.debugOutput)
        assertTrue(cfg.languageOverride.isEmpty())
        assertTrue(cfg.uninstallerDir.isEmpty())
    }
}
