/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi

import com.intellij.psi.PsiNameIdentifierOwner

/**
 * Extra behaviour for `Key=Value` directive entries. A directive entry in the [CustomMessages]
 * section declares a custom message and acts as a renamable [PsiNameIdentifierOwner] whose name
 * is the message identifier (the part after any optional `lang.` prefix).
 */
interface IsSectionDirectiveEntryEx : PsiNameIdentifierOwner {
    /** Full key text including any `lang.` prefix, e.g. `english.LaunchProgram`. */
    fun keyText(): String

    /** True when this entry sits in a [CustomMessages] section and therefore declares a custom message. */
    fun isCustomMessageDeclaration(): Boolean

    /** The message identifier with any `lang.` prefix stripped, or `null` when not a custom message. */
    fun customMessageName(): String?
}
