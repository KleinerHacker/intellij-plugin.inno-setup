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

package org.pcsoft.intellij.plugin.inno_setup.build.config

import com.intellij.execution.RunManager
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsRunConfigurationType
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorSymbolTracker
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Developer tests for [IsRunConfigurationSelectionListener].
 *
 * The listener is the link between "the user picked another build configuration" and "the editor shows the
 * branches of that configuration". Restarting the daemon is not enough on its own — the analysis is cached
 * per document revision — so what is asserted here is the symbol tracker moving.
 */
class IsRunConfigurationSelectionListenerTest : IsTimedBasePlatformTestCase() {

    private val tracker get() = IsPreprocessorSymbolTracker.getInstance(project)

    private fun listener() = IsRunConfigurationSelectionListener(project)

    /** Switching the selected run configuration in the toolbar must invalidate the cached analysis. */
    fun testSelectionChangeBumpsTheSymbolTracker() {
        val before = tracker.modificationCount

        listener().runConfigurationSelected(null)

        assertTrue("A new selection must invalidate the analysis", tracker.modificationCount > before)
    }

    /** Editing the run configuration (its build configuration field) must invalidate the analysis too. */
    fun testConfigurationChangeBumpsTheSymbolTracker() {
        val settings = RunManager.getInstance(project)
            .createConfiguration("Test", IsRunConfigurationType().configurationFactories.first())
        val before = tracker.modificationCount

        listener().runConfigurationChanged(settings)

        assertTrue("An edited run configuration must invalidate the analysis", tracker.modificationCount > before)
    }

    /** Removing the selected run configuration takes its symbols away and must invalidate the analysis. */
    fun testConfigurationRemovalBumpsTheSymbolTracker() {
        val settings = RunManager.getInstance(project)
            .createConfiguration("Test", IsRunConfigurationType().configurationFactories.first())
        val before = tracker.modificationCount

        listener().runConfigurationRemoved(settings)

        assertTrue("A removed run configuration must invalidate the analysis", tracker.modificationCount > before)
    }
}
