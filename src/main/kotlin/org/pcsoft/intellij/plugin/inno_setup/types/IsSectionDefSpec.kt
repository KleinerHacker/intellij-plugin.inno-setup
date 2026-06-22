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

package org.pcsoft.intellij.plugin.inno_setup.types

/**
 * Describes one section from the bundled Inno Setup section specification.
 *
 * @property name Section name without square brackets.
 * @property type Parser model used by the section, for example directive or parameter entries.
 * @property required Targets for which at least one section with this name must be present.
 * @property deprecated Targets for which the section is marked as deprecated.
 * @property description HTML-capable section description used by quick documentation.
 * @property attributes Supported directive keys or parameter keys inside the section.
 * @property internationalization Whether keys in the section may use a `lang.` prefix.
 * @property languageFile Whether the section is permitted in Inno Setup language files (`.isl`).
 * @property since First Inno Setup version that supports the section, or `null` when unknown.
 * @property until Last Inno Setup version that supports the section, or `null` when it is still valid.
 */
data class IsSectionDefSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val name: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val type: IsSectionType,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val required: Set<IsSectionSpecTarget> = emptySet(),
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val deprecated: Set<IsSectionSpecTarget> = emptySet(),
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val description: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val attributes: List<IsSectionAttributeSpec>,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val internationalization: Boolean = false,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val languageFile: Boolean = false,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val since: String? = null,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val until: String? = null
)
