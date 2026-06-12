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
import org.pcsoft.intellij.plugin.inno_setup.types.*

class IsSpecServiceTest {

    private val spec: IsSectionSpec by lazy {
        val mapper = YAMLMapper.builder()
            .addModule(kotlinModule())
            .build()
        val stream = IsSpecServiceTest::class.java.getResourceAsStream("/spec/isi-spec.yaml")
            ?: error("isi-spec.yaml not found in test classpath")
        mapper.readValue(stream)
    }

    @Test
    fun `all 18 sections are loaded`() {
        assertEquals(18, spec.sections.size)
    }

    @Test
    fun `all expected section names are present`() {
        val names = spec.sections.map { it.name }.toSet()
        listOf(
            "Setup", "Types", "Components", "Tasks", "Dirs", "Files",
            "Icons", "Registry", "Run", "UninstallRun", "Languages",
            "LangOptions", "Messages", "CustomMessages",
            "InstallDelete", "UninstallDelete", "ISSigKeys", "Code"
        ).forEach { assertTrue("Missing section: $it", it in names) }
    }

    @Test
    fun `Messages and CustomMessages are internationalized directive sections`() {
        listOf("Messages", "CustomMessages").forEach { name ->
            val section = spec.sections.find { it.name == name }!!
            assertEquals("$name must be a directive section", "directive", section.type)
            assertTrue("$name must be internationalized", section.internationalization)
        }
        val custom = spec.sections.find { it.name == "CustomMessages" }!!
        assertTrue("CustomMessages must have no predefined attributes", custom.attributes.isEmpty())
        val messages = spec.sections.find { it.name == "Messages" }!!
        assertTrue("Messages must declare predefined identifiers", messages.attributes.isNotEmpty())
    }

    @Test
    fun `Setup section is directive type`() {
        val setup = spec.sections.find { it.name == "Setup" }!!
        assertEquals("directive", setup.type)
    }

    @Test
    fun `LangOptions is a directive section with all expected attributes`() {
        val lang = spec.sections.find { it.name == "LangOptions" }!!
        assertEquals("directive", lang.type)
        assertTrue("LangOptions section must not be deprecated", lang.deprecated.isEmpty())
        val attrNames = lang.attributes.map { it.name }.toSet()
        listOf(
            "LanguageName", "LanguageID", "LanguageCodePage",
            "DialogFontName", "DialogFontSize", "DialogFontBaseScaleWidth", "DialogFontBaseScaleHeight",
            "WelcomeFontName", "WelcomeFontSize", "RightToLeft",
            "TitleFontName", "TitleFontSize", "CopyrightFontName", "CopyrightFontSize"
        ).forEach { assertTrue("LangOptions missing attribute: $it", it in attrNames) }
    }

    @Test
    fun `LangOptions LanguageID is integer typed`() {
        val lang = spec.sections.find { it.name == "LangOptions" }!!
        val languageId = lang.attributes.find { it.name == "LanguageID" }!!
        val type = languageId.type
        assertTrue("LanguageID must be a native type", type is IsSectionNativeTypeSpec)
        assertEquals("integer", (type as IsSectionNativeTypeSpec).dataType)
    }

    @Test
    fun `LangOptions removed font directives carry until 6_4`() {
        val lang = spec.sections.find { it.name == "LangOptions" }!!
        listOf("TitleFontName", "TitleFontSize", "CopyrightFontName", "CopyrightFontSize").forEach { name ->
            val attr = lang.attributes.find { it.name == name }!!
            assertEquals("$name must be marked removed in 6.4", "6.4", attr.until)
        }
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
            assertTrue("$attrName must be required", attr!!.required.appliesTo(IsSectionSpecTarget.ISS))
        }
    }

    @Test
    fun `Source and DestDir in Files are required`() {
        val files = spec.sections.find { it.name == "Files" }!!
        listOf("Source", "DestDir").forEach { attrName ->
            val attr = files.attributes.find { it.name == attrName }
            assertNotNull("$attrName must exist in Files", attr)
            assertTrue("$attrName must be required", attr!!.required.appliesTo(IsSectionSpecTarget.ISS))
        }
    }

    @Test
    fun `Root and Subkey in Registry are required`() {
        val registry = spec.sections.find { it.name == "Registry" }!!
        listOf("Root", "Subkey").forEach { attrName ->
            val attr = registry.attributes.find { it.name == attrName }
            assertNotNull("$attrName must exist in Registry", attr)
            assertTrue("$attrName must be required", attr!!.required.appliesTo(IsSectionSpecTarget.ISS))
        }
    }

    @Test
    fun `Types attribute in Components is reference to Types section with array`() {
        val components = spec.sections.find { it.name == "Components" }!!
        val attr = components.attributes.find { it.name == "Types" }!!
        assertTrue("Types must be a reference type", attr.type is IsSectionReferenceTypeSpec)
        assertEquals("Types", (attr.type as IsSectionReferenceTypeSpec).section)
        assertTrue("Types must be array", attr.array)
    }

    @Test
    fun `AppName in Setup is native string type`() {
        val setup = spec.sections.find { it.name == "Setup" }!!
        val attr = setup.attributes.find { it.name == "AppName" }!!
        assertTrue("AppName must be native type", attr.type is IsSectionNativeTypeSpec)
        assertEquals("string", (attr.type as IsSectionNativeTypeSpec).dataType)
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

    @Test
    fun `Setup section is required in iss`() {
        val setup = spec.sections.find { it.name == "Setup" }!!
        assertTrue("Setup must be required in scripts", setup.required.appliesTo(IsSectionSpecTarget.ISS))
        assertFalse("Setup is not required in language files", setup.required.appliesTo(IsSectionSpecTarget.ISL))
    }

    @Test
    fun `only Setup is required in iss`() {
        spec.sections.filter { it.name != "Setup" }.forEach { section ->
            assertFalse(
                "Section '${section.name}' must not be required in scripts",
                section.required.appliesTo(IsSectionSpecTarget.ISS)
            )
        }
    }

    @Test
    fun `LangOptions and its language identity attributes are required in isl only`() {
        val lang = spec.sections.find { it.name == "LangOptions" }!!
        assertTrue("LangOptions must be required in .isl", lang.required.appliesTo(IsSectionSpecTarget.ISL))
        assertFalse("LangOptions is not required in scripts", lang.required.appliesTo(IsSectionSpecTarget.ISS))
        listOf("LanguageName", "LanguageID").forEach { name ->
            val attr = lang.attributes.find { it.name == name }!!
            assertTrue("$name must be required in .isl", attr.required.appliesTo(IsSectionSpecTarget.ISL))
            assertFalse("$name is not required in scripts", attr.required.appliesTo(IsSectionSpecTarget.ISS))
        }
    }

    @Test
    fun `Flags attribute in Files is flag type`() {
        val files = spec.sections.find { it.name == "Files" }!!
        val flags = files.attributes.find { it.name == "Flags" }!!
        assertTrue("Flags must be IsSectionFlagTypeSpec", flags.type is IsSectionFlagTypeSpec)
        val flagType = flags.type as IsSectionFlagTypeSpec
        assertTrue("Must have at least one flag", flagType.flags.isNotEmpty())
    }

    @Test
    fun `all sections with Flags attribute use flag type`() {
        val sectionsWithFlags =
            listOf("Types", "Components", "Tasks", "Dirs", "Files", "Icons", "Registry", "Run", "UninstallRun")
        sectionsWithFlags.forEach { sectionName ->
            val section = spec.sections.find { it.name == sectionName }!!
            val flagsAttr = section.attributes.find { it.name == "Flags" }
            assertNotNull("Section '$sectionName' must have a Flags attribute", flagsAttr)
            assertTrue(
                "Flags in '$sectionName' must be IsSectionFlagTypeSpec",
                flagsAttr!!.type is IsSectionFlagTypeSpec
            )
        }
    }

    @Test
    fun `all flags in all sections have non-blank names and descriptions`() {
        spec.sections.flatMap { it.attributes }
            .filter { it.type is IsSectionFlagTypeSpec }
            .flatMap { (it.type as IsSectionFlagTypeSpec).flags }
            .forEach { flag ->
                assertFalse("Flag name must not be blank", flag.name.isBlank())
                assertFalse("Flag '${flag.name}' description must not be blank", flag.description.isBlank())
            }
    }

    @Test
    fun `32bit and 64bit flags in Files have error conflict with each other`() {
        val files = spec.sections.find { it.name == "Files" }!!
        val flagType = files.attributes.find { it.name == "Flags" }!!.type as IsSectionFlagTypeSpec
        val flag32 = flagType.flags.find { it.name == "32bit" }!!
        assertTrue(
            "32bit must have error-conflict with 64bit",
            flag32.conflicts.any { it.flag == "64bit" && it.type == IsSectionFlagType.FORBIDDEN }
        )
        val flag64 = flagType.flags.find { it.name == "64bit" }!!
        assertTrue(
            "64bit must have error-conflict with 32bit",
            flag64.conflicts.any { it.flag == "32bit" && it.type == IsSectionFlagType.FORBIDDEN }
        )
    }

    @Test
    fun `runminimized and runmaximized in Icons have error conflict`() {
        val icons = spec.sections.find { it.name == "Icons" }!!
        val flagType = icons.attributes.find { it.name == "Flags" }!!.type as IsSectionFlagTypeSpec
        val runMin = flagType.flags.find { it.name == "runminimized" }!!
        assertTrue(
            "runminimized must have error-conflict with runmaximized",
            runMin.conflicts.any { it.flag == "runmaximized" && it.type == IsSectionFlagType.FORBIDDEN }
        )
    }

    @Test
    fun `nowait and waituntilterminated in Run have error conflict`() {
        val run = spec.sections.find { it.name == "Run" }!!
        val flagType = run.attributes.find { it.name == "Flags" }!!.type as IsSectionFlagTypeSpec
        val nowait = flagType.flags.find { it.name == "nowait" }!!
        assertTrue(
            "nowait must have error-conflict with waituntilterminated",
            nowait.conflicts.any { it.flag == "waituntilterminated" && it.type == IsSectionFlagType.FORBIDDEN }
        )
    }
}