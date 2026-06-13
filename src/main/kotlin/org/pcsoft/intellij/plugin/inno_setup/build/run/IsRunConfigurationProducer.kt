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
import com.intellij.execution.actions.LazyRunConfigurationProducer
import com.intellij.execution.configurations.ConfigurationFactory
import com.intellij.openapi.util.Ref
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.build.IsScriptCollector
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile

/**
 * Creates a ghost [IsRunConfiguration] from the current context when the selected file is a
 * top-level `.iss` script. Included scripts are skipped (they cannot be run standalone).
 */
class IsRunConfigurationProducer : LazyRunConfigurationProducer<IsRunConfiguration>() {

    override fun getConfigurationFactory(): ConfigurationFactory =
        IsRunConfigurationType().configurationFactories.first()

    override fun setupConfigurationFromContext(
        configuration: IsRunConfiguration,
        context: ConfigurationContext,
        sourceElement: Ref<PsiElement>
    ): Boolean {
        val psi = context.psiLocation ?: return false
        val file = psi.containingFile as? IsScriptFile ?: return false
        val vf = file.virtualFile ?: return false
        if (IsScriptCollector(context.project).isIncludedByOther(vf)) return false

        configuration.scriptPath = vf.path
        configuration.name = vf.nameWithoutExtension
        configuration.runMode = IsRunMode.DRY
        configuration.actionType = IsRunActionType.INSTALL
        configuration.debugOutput = true
        return true
    }

    override fun isConfigurationFromContext(
        configuration: IsRunConfiguration,
        context: ConfigurationContext
    ): Boolean {
        val psi = context.psiLocation ?: return false
        val file = psi.containingFile as? IsScriptFile ?: return false
        val vf = file.virtualFile ?: return false
        return configuration.scriptPath == vf.path
    }
}
