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

package org.pcsoft.intellij.plugin.inno_setup.script.services

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import com.intellij.openapi.components.Service
import org.pcsoft.intellij.plugin.inno_setup.script.types.IsSectionConstantSpec

/**
 * Application-level service that loads the bundled Inno Setup constants specification.
 */
@Service(Service.Level.APP)
class IsConstantService {
    /**
     * Parsed constant specification used for constant completion, validation, and documentation.
     */
    val spec: IsSectionConstantSpec by lazy {
        val mapper = YAMLMapper.builder()
            .addModule(kotlinModule())
            .build()
        val stream = IsConstantService::class.java
            .getResourceAsStream("/spec/is-const.yaml")
            ?: error("is-const.yaml not found in resources")
        mapper.readValue(stream)
    }
}
