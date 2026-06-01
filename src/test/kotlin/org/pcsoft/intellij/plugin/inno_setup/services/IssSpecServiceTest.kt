package org.pcsoft.intellij.plugin.inno_setup.services

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Assert.*
import org.junit.Test
import org.pcsoft.intellij.plugin.inno_setup.types.InnoSetupSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IssNativeType
import org.pcsoft.intellij.plugin.inno_setup.types.IssReferenceType

class IssSpecServiceTest {

    private val spec: InnoSetupSpec by lazy {
        val mapper = YAMLMapper.builder()
            .addModule(kotlinModule())
            .build()
        val stream = IssSpecServiceTest::class.java.getResourceAsStream("/spec/iss-spec.yaml")
            ?: error("iss-spec.yaml not found in test classpath")
        mapper.readValue(stream)
    }

    @Test
    fun `all 14 sections are loaded`() {
        assertEquals(14, spec.sections.size)
    }

    @Test
    fun `all expected section names are present`() {
        val names = spec.sections.map { it.name }.toSet()
        listOf(
            "Setup", "Types", "Components", "Tasks", "Dirs", "Files",
            "Icons", "Registry", "Run", "UninstallRun", "Languages",
            "InstallDelete", "UninstallDelete", "Code"
        ).forEach { assertTrue("Missing section: $it", it in names) }
    }

    @Test
    fun `Setup section is directive type`() {
        val setup = spec.sections.find { it.name == "Setup" }!!
        assertEquals("directive", setup.type)
    }

    @Test
    fun `parameter sections have correct type`() {
        val parameterSections = listOf(
            "Types", "Components", "Tasks", "Dirs", "Files",
            "Icons", "Registry", "Run", "UninstallRun",
            "Languages", "InstallDelete", "UninstallDelete"
        )
        parameterSections.forEach { name ->
            val section = spec.sections.find { it.name == name }!!
            assertEquals("Section '$name' should be parameter", "parameter", section.type)
        }
    }

    @Test
    fun `Code section is code type with no attributes`() {
        val code = spec.sections.find { it.name == "Code" }!!
        assertEquals("code", code.type)
        assertTrue("Code section must have no attributes", code.attributes.isEmpty())
    }

    @Test
    fun `AppName and AppVersion in Setup are required`() {
        val setup = spec.sections.find { it.name == "Setup" }!!
        listOf("AppName", "AppVersion").forEach { attrName ->
            val attr = setup.attributes.find { it.name == attrName }
            assertNotNull("$attrName must exist in Setup", attr)
            assertTrue("$attrName must be required", attr!!.required)
        }
    }

    @Test
    fun `Source and DestDir in Files are required`() {
        val files = spec.sections.find { it.name == "Files" }!!
        listOf("Source", "DestDir").forEach { attrName ->
            val attr = files.attributes.find { it.name == attrName }
            assertNotNull("$attrName must exist in Files", attr)
            assertTrue("$attrName must be required", attr!!.required)
        }
    }

    @Test
    fun `Root and Subkey in Registry are required`() {
        val registry = spec.sections.find { it.name == "Registry" }!!
        listOf("Root", "Subkey").forEach { attrName ->
            val attr = registry.attributes.find { it.name == attrName }
            assertNotNull("$attrName must exist in Registry", attr)
            assertTrue("$attrName must be required", attr!!.required)
        }
    }

    @Test
    fun `Types attribute in Components is reference to Types section with array`() {
        val components = spec.sections.find { it.name == "Components" }!!
        val attr = components.attributes.find { it.name == "Types" }!!
        assertTrue("Types must be a reference type", attr.type is IssReferenceType)
        assertEquals("Types", (attr.type as IssReferenceType).section)
        assertTrue("Types must be array", attr.array)
    }

    @Test
    fun `AppName in Setup is native string type`() {
        val setup = spec.sections.find { it.name == "Setup" }!!
        val attr = setup.attributes.find { it.name == "AppName" }!!
        assertTrue("AppName must be native type", attr.type is IssNativeType)
        assertEquals("string", (attr.type as IssNativeType).dataType)
    }

    @Test
    fun `no section has null or blank name`() {
        spec.sections.forEach { section ->
            assertFalse("Section name must not be blank", section.name.isBlank())
        }
    }

    @Test
    fun `no section has null or blank description`() {
        spec.sections.forEach { section ->
            assertFalse("Section '${section.name}' must have a description", section.description.isBlank())
        }
    }
}