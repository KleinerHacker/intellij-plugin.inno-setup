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
 * Describes one directive key or parameter key of an Inno Setup section.
 *
 * @property name Attribute name as it appears in `.iss` or `.isl` files.
 * @property type Value type used for validation, completion, and reference creation.
 * @property required Targets for which the attribute must be present.
 * @property deprecated Targets for which the attribute is marked as deprecated.
 * @property description HTML-capable description loaded from the section specification.
 * @property array Whether the attribute may occur more than once in the same section entry.
 * @property since First Inno Setup version that supports the attribute, or `null` when unknown.
 * @property until Last Inno Setup version that supports the attribute, or `null` when it is still valid.
 */
data class IsSectionAttributeSpec(
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val name: String,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val type: IsSectionAttributeTypeSpec,
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
    val array: Boolean,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val since: String? = null,
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val until: String? = null
)
