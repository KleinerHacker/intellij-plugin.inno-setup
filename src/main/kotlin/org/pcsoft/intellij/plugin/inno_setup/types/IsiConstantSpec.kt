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

enum class IsiConstantCategorySpec {
    @JsonProperty("directory")
    DIRECTORY,

    @JsonProperty("shell_folder")
    SHELL_FOLDER,

    @JsonProperty("auto")
    AUTO,

    @JsonProperty("special")
    SPECIAL,

    @JsonProperty("parameterized")
    PARAMETERIZED
}

data class IsiBuiltinConstantSpec(
    val name: String,
    val description: String,
    val deprecated: Boolean,
    val category: IsiConstantCategorySpec = IsiConstantCategorySpec.SPECIAL,
    val parameterized: Boolean = false,
    val syntax: String? = null,
    val since: String? = null,
    val until: String? = null
)

data class IsiConstantSpec(val constants: List<IsiBuiltinConstantSpec>)
