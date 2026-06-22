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
 * Structural kind of a section, mirroring the `type` enum in `is-spec.json`.
 *
 * @property typeName The lowercase wire name used in the spec YAML and shown in documentation/completion.
 */
enum class IsSectionType(val typeName: String) {
    /** `Key=Value` sections such as `\[Setup]`, `\[Messages]`, `\[LangOptions]`. */
    @JsonProperty("directive")
    DIRECTIVE("directive"),

    /** `Key: Value; …` parameter lists such as `\[Files]`, `\[Icons]`, `\[Registry]`. */
    @JsonProperty("parameter")
    PARAMETER("parameter"),

    /** The free-form Pascal `\[Code]` section. */
    @JsonProperty("code")
    CODE("code"),
}
