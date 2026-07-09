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

import com.intellij.psi.codeStyle.CodeStyleSettings
import com.intellij.psi.codeStyle.CodeStyleSettingsProvider
import com.intellij.psi.codeStyle.CustomCodeStyleSettings

/**
 * Registers the [IsSectionCodeStyleSettings] instance on every [CodeStyleSettings] (via the primary
 * `codeStyleSettingsProvider` extension point, which the platform always consults when materialising custom
 * settings).
 *
 * It deliberately does **not** declare a language and provides no configurable: the visible
 * *Code Style ▸ Inno Setup* page, its options and preview are contributed by
 * [IsSectionCodeStyleSettingsProvider]. Binding this factory to a language would make the platform build a
 * second, page-less code-style node for it.
 */
class IsSectionCodeStyleSettingsFactory : CodeStyleSettingsProvider() {

    override fun createCustomSettings(settings: CodeStyleSettings): CustomCodeStyleSettings =
        IsSectionCodeStyleSettings(settings)

    /** No standalone page — the visible page is [IsSectionCodeStyleSettingsProvider]'s language page. */
    override fun hasSettingsPage(): Boolean = false
}
