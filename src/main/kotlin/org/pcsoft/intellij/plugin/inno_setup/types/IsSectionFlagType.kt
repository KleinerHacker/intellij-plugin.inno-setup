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

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * Validation relationship type for a flag conflict entry.
 */
enum class IsSectionFlagType {
    /** The referenced flag must not be combined with the current flag. */
    @JsonProperty("forbidden")
    FORBIDDEN,

    /** The referenced flag is ignored when the current flag is present. */
    @JsonProperty("ignored")
    IGNORED,

    /** The flag is redundant because another flag in the same value implicitly sets it (or nullifies its effect). */
    @JsonProperty("redundant")
    REDUNDANT,

    /** The flag must be combined with the referenced flag; if that flag is missing it is an error. */
    @JsonProperty("requires")
    REQUIRES
}
