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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.services

import com.fasterxml.jackson.dataformat.yaml.YAMLMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Assert.*
import org.junit.Test
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.parseBuiltinSignature
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.IsPreprocessorFunctionReturnType
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.IsPreprocessorSpec
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.IsPreprocessorVariableType

class IsPreprocessorServiceTest {

    private val spec: IsPreprocessorSpec by lazy {
        val mapper = YAMLMapper.builder().addModule(kotlinModule()).build()
        val stream = IsPreprocessorServiceTest::class.java.getResourceAsStream("/spec/is-preprocessor.yaml")
            ?: error("is-preprocessor.yaml not found in test classpath")
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
        assertEquals(IsPreprocessorVariableType.INT, lineVar!!.type)
    }

    @Test
    fun `GetFileVersionString builtin function exists`() {
        val fn = spec.builtinFunctions.find { it.name == "GetFileVersionString" }
        assertNotNull("GetFileVersionString must exist", fn)
        assertTrue("Must have signature", fn!!.signature.isNotBlank())
    }

    @Test
    fun `builtin functions cover the official ISPP index`() {
        val expected = setOf(
            "AddBackslash", "AddQuotes", "ChangeFileExt", "ComparePackedVersion", "Copy", "CopyFile",
            "DecodeVer", "Defined", "Delete", "DeleteFile", "DeleteFileNow", "DimOf", "DirExists",
            "EmitLanguagesSection", "EncodeVer", "EntryCount", "Error", "Exec", "ExecAndGetFirstLine",
            "ExtractFileDir", "ExtractFileExt", "ExtractFileName", "ExtractFilePath", "FileClose",
            "FileEof", "FileExists", "FileOpen", "FileRead", "FileReset", "FileSize", "Find", "FindClose",
            "FindCode", "FindFirst", "FindGetFileName", "FindNext", "FindSection", "FindSectionEnd",
            "ForceDirectories", "GetDateTimeString", "GetEnv", "GetFileCompanyString",
            "GetFileCopyrightString", "GetFileDateTimeString", "GetFileDescriptionString",
            "GetFileOriginalFilenameString", "GetFileProductVersionString", "GetFileVersionString",
            "GetMD5OfFile", "GetMD5OfString", "GetMD5OfUnicodeString", "GetPackedVersion", "GetSHA1OfFile",
            "GetSHA1OfString", "GetSHA1OfUnicodeString", "GetSHA256OfFile", "GetSHA256OfString",
            "GetSHA256OfUnicodeString", "GetStringFileInfo", "GetVersionComponents", "GetVersionNumbers",
            "GetVersionNumbersString", "Insert", "Int", "Is64BitPEImage", "IsWin64", "Len", "LowerCase",
            "Max", "Message", "Min", "PackVersionComponents", "PackVersionNumbers", "Pos", "Power",
            "ReadIni", "ReadReg", "RemoveBackslashUnlessRoot", "RemoveFileExt", "RPos", "SamePackedVersion",
            "SameStr", "SameText", "SaveStringToFile", "SaveToFile", "SetSetupSetting", "SetupSetting",
            "Str", "StringChange", "StrToVersion", "Trim", "TypeOf", "UnpackVersionComponents",
            "UnpackVersionNumbers", "UpperCase", "VersionToStr", "Warning", "WriteIni", "YesNo"
        )
        val actual = spec.builtinFunctions.map { it.name }.toSet()
        val missing = expected - actual
        assertTrue("Missing built-in functions: $missing", missing.isEmpty())
    }

    @Test
    fun `builtin function names are unique`() {
        val names = spec.builtinFunctions.map { it.name }
        assertEquals("Built-in function names must be unique", names.size, names.toSet().size)
    }

    @Test
    fun `sampled builtin functions have expected return types`() {
        fun ret(name: String) = spec.builtinFunctions.first { it.name == name }.returnType
        assertEquals(IsPreprocessorFunctionReturnType.STR, ret("Str"))
        assertEquals(IsPreprocessorFunctionReturnType.INT, ret("Int"))
        assertEquals(IsPreprocessorFunctionReturnType.INT, ret("FileExists"))
        assertEquals(IsPreprocessorFunctionReturnType.STR, ret("Trim"))
        assertEquals(IsPreprocessorFunctionReturnType.INT, ret("Power"))
        assertEquals(IsPreprocessorFunctionReturnType.VOID, ret("Warning"))
    }

    @Test
    fun `forbidden variable names list is loaded`() {
        assertTrue("Must have forbidden variable names", spec.forbiddenVariableNames.isNotEmpty())
        spec.forbiddenVariableNames.forEach { f ->
            assertFalse("Forbidden name must not be blank", f.name.isBlank())
            assertFalse("Forbidden name '${f.name}' description must not be blank", f.description.isBlank())
        }
    }

    /**
     * Every built-in signature of the specification must be parsable into the structured parameter model —
     * otherwise the argument count / argument type validation would silently skip that function.
     */
    @Test
    fun `every builtin signature can be parsed`() {
        spec.builtinFunctions.forEach { function ->
            val signature = parseBuiltinSignature(function.signature)
            assertNotNull("Signature of '${function.name}' must be parsable: ${function.signature}", signature)
            assertEquals(
                "Parsed name of '${function.signature}'",
                function.name,
                signature!!.name,
            )
            signature.parameters.forEach { parameter ->
                assertFalse(
                    "Parameter name in '${function.signature}' must not be blank",
                    parameter.name.isBlank(),
                )
            }
        }
    }

    /**
     * Optional parameters must always come last, otherwise the required-argument count derived from the
     * signature would not describe a contiguous prefix of the parameter list.
     */
    @Test
    fun `optional builtin parameters are trailing`() {
        spec.builtinFunctions.forEach { function ->
            val parameters = parseBuiltinSignature(function.signature)!!.parameters
            val firstOptional = parameters.indexOfFirst { it.optional }
            if (firstOptional >= 0) {
                assertTrue(
                    "Optional parameters of '${function.name}' must be trailing",
                    parameters.drop(firstOptional).all { it.optional },
                )
            }
        }
    }

    @Test
    fun `defined is a forbidden variable name`() {
        assertNotNull(
            "'defined' must be a reserved/forbidden macro name",
            spec.forbiddenVariableNames.find { it.name == "defined" }
        )
    }
}
