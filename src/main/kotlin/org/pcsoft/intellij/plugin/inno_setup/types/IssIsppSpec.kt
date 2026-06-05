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

data class IssIsppDirectiveSpec(
    val name: String,
    val syntax: String,
    val description: String,
    val deprecated: Boolean = false
)

data class IssIsppVariableSpec(
    val name: String,
    val type: String,
    val description: String
)

data class IssIsppFunctionSpec(
    val name: String,
    val signature: String,
    val description: String
)

data class IssIsppSpec(
    val directives: List<IssIsppDirectiveSpec>,
    @field:JsonProperty("predefined_variables") val predefinedVariables: List<IssIsppVariableSpec>,
    @field:JsonProperty("builtin_functions") val builtinFunctions: List<IssIsppFunctionSpec>
)
