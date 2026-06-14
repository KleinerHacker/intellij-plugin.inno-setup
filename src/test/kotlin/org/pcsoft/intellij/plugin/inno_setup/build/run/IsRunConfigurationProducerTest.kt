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

import com.intellij.execution.actions.ConfigurationContext
import com.intellij.openapi.actionSystem.CommonDataKeys
import com.intellij.openapi.actionSystem.impl.SimpleDataContext
import com.intellij.psi.PsiElement
import com.intellij.testFramework.fixtures.BasePlatformTestCase

class IsRunConfigurationProducerTest : BasePlatformTestCase() {

    private fun makeConfig() = IsRunConfiguration(project, IsRunConfigurationType().configurationFactories.first(), "")

    fun testIsConfigurationFromContextMatchesSamePath() {
        val file = myFixture.addFileToProject("main.iss", "[Setup]\nAppName=x\n")
        myFixture.openFileInEditor(file.virtualFile)
        val psi = file.findElementAt(0)!!
        val ctx = context(psi)
        val cfg = makeConfig()
        cfg.scriptPath = file.virtualFile.path
        val producer = IsRunConfigurationProducer()
        assertTrue(producer.isConfigurationFromContext(cfg, ctx))
    }

    fun testIsConfigurationFromContextMismatchesDifferentPath() {
        val file = myFixture.addFileToProject("main.iss", "[Setup]\n")
        myFixture.openFileInEditor(file.virtualFile)
        val psi = file.findElementAt(0)!!
        val ctx = context(psi)
        val cfg = makeConfig()
        cfg.scriptPath = "/other/path.iss"
        val producer = IsRunConfigurationProducer()
        assertFalse(producer.isConfigurationFromContext(cfg, ctx))
    }

    fun testCollectorRecognizesIncludedScript() {
        // Verify that IsScriptCollector correctly identifies included scripts —
        // which is the gate the producer uses to decide whether to produce a config.
        myFixture.addFileToProject("main.iss", "#include \"part.iss\"\n")
        val part = myFixture.addFileToProject("part.iss", "[Files]\n")
        val collector = org.pcsoft.intellij.plugin.inno_setup.build.IsScriptCollector(project)
        assertTrue(collector.isIncludedByOther(part.virtualFile))
    }

    fun testCollectorRecognizesTopLevelScript() {
        val file = myFixture.addFileToProject("standalone.iss", "[Setup]\n")
        val collector = org.pcsoft.intellij.plugin.inno_setup.build.IsScriptCollector(project)
        assertFalse(collector.isIncludedByOther(file.virtualFile))
    }

    private fun context(psi: PsiElement): ConfigurationContext {
        val dataCtx = SimpleDataContext.builder()
            .add(CommonDataKeys.PROJECT, project)
            .add(CommonDataKeys.PSI_FILE, psi.containingFile)
            .add(CommonDataKeys.PSI_ELEMENT, psi)
            .build()
        return ConfigurationContext.getFromContext(dataCtx, com.intellij.openapi.actionSystem.ActionPlaces.UNKNOWN)
    }
}
