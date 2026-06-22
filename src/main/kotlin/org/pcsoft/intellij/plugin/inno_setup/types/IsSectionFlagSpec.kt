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
 * Describes a validation relationship between two flags.
 *
 * @property flag Other flag affected by the relationship.
 * @property type Kind of conflict or dependency that applies to [flag].
 */
data class IsSectionFlagConflictSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val flag: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val type: IsSectionFlagType
)

/**
 * Describes a flag that can appear in a section attribute value.
 *
 * @property name Flag identifier as it appears in the script.
 * @property description HTML-capable description loaded from the section specification.
 * @property deprecated Targets for which the flag is marked as deprecated.
 * @property conflicts Relationships with other flags in the same value.
 * @property since First Inno Setup version that supports the flag, or `null` when unknown.
 * @property until Last Inno Setup version that supports the flag, or `null` when it is still valid.
 */
data class IsSectionFlagSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val name: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val description: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val deprecated: Set<IsSectionSpecTarget> = emptySet(),
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val conflicts: List<IsSectionFlagConflictSpec> = emptyList(),
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val since: String? = null,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val until: String? = null
)
