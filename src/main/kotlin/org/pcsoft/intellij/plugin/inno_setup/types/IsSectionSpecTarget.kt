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

package org.pcsoft.intellij.plugin.inno_setup.types

import com.fasterxml.jackson.annotation.JsonProperty

/**
 * The file type a spec rule (`required` / `deprecated`) applies to. Modelled as a set in the spec so
 * the same attribute can, e.g., be required only in language files (`.isl`) but not in scripts (`.iss`).
 */
enum class IsSectionSpecTarget {
    @JsonProperty("iss")
    ISS,

    @JsonProperty("isl")
    ISL,
}

/** Whether a spec rule's target set covers [target]. */
fun Set<IsSectionSpecTarget>.appliesTo(target: IsSectionSpecTarget): Boolean = target in this
