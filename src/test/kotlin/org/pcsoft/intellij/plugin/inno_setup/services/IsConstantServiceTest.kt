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

package org.pcsoft.intellij.plugin.inno_setup.services

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Assert.*
import org.junit.Test
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionConstantCategorySpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionConstantSpec

class IsConstantServiceTest {

    private val spec: IsSectionConstantSpec by lazy {
        val mapper = YAMLMapper.builder().addModule(kotlinModule()).build()
        val stream = IsConstantServiceTest::class.java.getResourceAsStream("/spec/is-const.yaml")
            ?: error("is-const.yaml not found in test classpath")
        mapper.readValue(stream)
    }

    @Test
    fun `constants list is not empty`() {
        assertTrue("Must have constants", spec.constants.isNotEmpty())
    }

    @Test
    fun `all constants have non-blank name and description`() {
        spec.constants.forEach { c ->
            assertFalse("Constant name must not be blank", c.name.isBlank())
            assertFalse("Constant '${c.name}' description must not be blank", c.description.isBlank())
        }
    }

    @Test
    fun `app constant exists in directory category`() {
        val app = spec.constants.find { it.name == "app" }
        assertNotNull("app constant must exist", app)
        assertEquals(IsSectionConstantCategorySpec.DIRECTORY, app!!.type)
        assertTrue("app must not be deprecated", app.deprecated.isEmpty())
    }

    @Test
    fun `autopf constant exists in auto category`() {
        val autopf = spec.constants.find { it.name == "autopf" }
        assertNotNull("autopf constant must exist", autopf)
        assertEquals(IsSectionConstantCategorySpec.AUTO, autopf!!.type)
    }

    @Test
    fun `reg constant is parameterized with syntax`() {
        val reg = spec.constants.find { it.name == "reg" }
        assertNotNull("reg constant must exist", reg)
        assertTrue("reg must be parameterized", reg!!.parameterized)
        assertNotNull("reg must have syntax", reg.syntax)
    }

    @Test
    fun `deprecated constants are marked`() {
        val pf = spec.constants.find { it.name == "pf" }
        assertNotNull("pf constant must exist", pf)
        assertTrue("pf must be deprecated", pf!!.deprecated.isNotEmpty())
    }

    @Test
    fun `parameterized constants have syntax`() {
        spec.constants.filter { it.parameterized }.forEach { c ->
            assertNotNull("Parameterized constant '${c.name}' must have syntax", c.syntax)
        }
    }
}
