package org.pcsoft.intellij.plugin.inno_setup.services

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Assert.*
import org.junit.Test
import org.pcsoft.intellij.plugin.inno_setup.types.IssConstantCategorySpec
import org.pcsoft.intellij.plugin.inno_setup.types.IssConstantSpec

class IssConstantServiceTest {

    private val spec: IssConstantSpec by lazy {
        val mapper = YAMLMapper.builder().addModule(kotlinModule()).build()
        val stream = IssConstantServiceTest::class.java.getResourceAsStream("/spec/iss-const.yaml")
            ?: error("iss-const.yaml not found in test classpath")
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
        assertEquals(IssConstantCategorySpec.DIRECTORY, app!!.category)
        assertFalse("app must not be deprecated", app.deprecated)
    }

    @Test
    fun `autopf constant exists in auto category`() {
        val autopf = spec.constants.find { it.name == "autopf" }
        assertNotNull("autopf constant must exist", autopf)
        assertEquals(IssConstantCategorySpec.AUTO, autopf!!.category)
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
        assertTrue("pf must be deprecated", pf!!.deprecated)
    }

    @Test
    fun `parameterized constants have syntax`() {
        spec.constants.filter { it.parameterized }.forEach { c ->
            assertNotNull("Parameterized constant '${c.name}' must have syntax", c.syntax)
        }
    }
}
