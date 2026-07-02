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

package org.pcsoft.intellij.plugin.inno_setup.script.types

import com.intellij.icons.AllIcons
import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

/**
 * A single recognised Windows locale, deserialized from `is-lang-code.yaml`. [id] is the Pascal-hex
 * LCID (e.g. `$0407`), [displayName] the English locale name, and [flag] the base name of an SVG
 * under `/icons/flags/` — or `null`, in which case a neutral globe icon is used.
 *
 * Languages bundled with Inno Setup have [builtin] = `true` and additionally provide [issName] and
 * [messagesFile] — the values used for a \[Languages] entry.
 */
data class IsLanguageDataSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val displayName: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val id: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val flag: String? = null,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val builtin: Boolean = false,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val issName: String? = null,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val messagesFile: String? = null
) {
    /** Flag icon for this locale, or a globe fallback when no flag is available. */
    val icon: Icon
        get() = flag?.let { IconLoader.getIcon("/icons/flags/$it.svg", IsLanguageDataSpec::class.java) }
            ?: AllIcons.General.Web

    /** The ready-to-use \[Languages] entry line for a built-in language. */
    fun toIssEntry(): String = "Name: \"$issName\"; MessagesFile: \"$messagesFile\""
}

/** Root of `is-lang-code.yaml`: all recognised Windows locales. */
data class IsLanguageCodeSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val languages: List<IsLanguageDataSpec>
)
