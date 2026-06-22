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

package org.pcsoft.intellij.plugin.inno_setup.services

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.intellij.openapi.components.Service
import org.pcsoft.intellij.plugin.inno_setup.types.IsPreprocessorSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsPreprocessorVariableSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsPreprocessorVariableType

/**
 * Application-level service that loads the bundled Inno Setup preprocessor specification.
 */
@Service(Service.Level.APP)
class IsPreprocessorService {
    /**
     * Parsed ISPP specification used for preprocessor completion, validation, and documentation.
     */
    val spec: IsPreprocessorSpec by lazy {
        val mapper = YAMLMapper.builder()
            .addModule(kotlinModule())
            .build()
        val stream = IsPreprocessorService::class.java
            .getResourceAsStream("/spec/is-preprocessor.yaml")
            ?: error("is-preprocessor.yaml not found in resources")
        mapper.readValue(stream)
    }

    /**
     * Predefined variables that carry a value and can therefore be emitted inline via `{#…}`.
     * Excludes the valueless `void` symbols (e.g. `WINDOWS`, `ISPP_INVOKED`), which are only
     * "defined" for conditional compilation (`#ifdef` / `#if defined(...)`) and have no value to emit.
     */
    val emittableVariables: List<IsPreprocessorVariableSpec> by lazy {
        spec.predefinedVariables.filter { it.type != IsPreprocessorVariableType.VOID }
    }
}
