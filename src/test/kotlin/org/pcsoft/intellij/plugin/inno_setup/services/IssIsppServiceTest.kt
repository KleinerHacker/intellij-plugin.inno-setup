package org.pcsoft.intellij.plugin.inno_setup.services

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Assert.*
import org.junit.Test
import org.pcsoft.intellij.plugin.inno_setup.types.IssIsppSpec

class IssIsppServiceTest {

    private val spec: IssIsppSpec by lazy {
        val mapper = YAMLMapper.builder().addModule(kotlinModule()).build()
        val stream = IssIsppServiceTest::class.java.getResourceAsStream("/spec/iss-ispp.yaml")
            ?: error("iss-ispp.yaml not found in test classpath")
        mapper.readValue(stream)
    }

    @Test
    fun `directives list is not empty`() {
        assertTrue("Must have directives", spec.directives.isNotEmpty())
    }

    @Test
    fun `predefined variables list is not empty`() {
        assertTrue("Must have predefined variables", spec.predefinedVariables.isNotEmpty())
    }

    @Test
    fun `builtin functions list is not empty`() {
        assertTrue("Must have builtin functions", spec.builtinFunctions.isNotEmpty())
    }

    @Test
    fun `all directives have non-blank name and description`() {
        spec.directives.forEach { d ->
            assertFalse("Directive name must not be blank", d.name.isBlank())
            assertFalse("Directive '${d.name}' description must not be blank", d.description.isBlank())
        }
    }

    @Test
    fun `define directive exists`() {
        val define = spec.directives.find { it.name == "define" }
        assertNotNull("define directive must exist", define)
    }

    @Test
    fun `include directive exists`() {
        val include = spec.directives.find { it.name == "include" }
        assertNotNull("include directive must exist", include)
    }

    @Test
    fun `LINE predefined variable exists`() {
        val lineVar = spec.predefinedVariables.find { it.name == "__LINE__" }
        assertNotNull("__LINE__ must exist", lineVar)
        assertEquals("int", lineVar!!.type)
    }

    @Test
    fun `GetFileVersion builtin function exists`() {
        val fn = spec.builtinFunctions.find { it.name == "GetFileVersion" }
        assertNotNull("GetFileVersion must exist", fn)
        assertTrue("Must have signature", fn!!.signature.isNotBlank())
    }
}
