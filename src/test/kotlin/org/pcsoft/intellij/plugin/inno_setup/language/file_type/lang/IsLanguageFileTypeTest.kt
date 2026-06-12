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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptLanguage

/**
 * Tests that `.isl` files are recognised as a distinct file type that reuses the ISS language and
 * parses into an [IsLanguageFile] (which is also an [IsScriptFile], so all ISS tooling keeps working).
 */
class IsLanguageFileTypeTest : BasePlatformTestCase() {

    fun testFileTypeMetadata() {
        assertEquals("isl", IsLanguageFileType.INSTANCE.defaultExtension)
        assertSame(IsScriptLanguage, IsLanguageFileType.INSTANCE.language)
    }

    fun testIslContentParsesAsIslFile() {
        val file = myFixture.configureByText(IsLanguageFileType.INSTANCE, "[Messages]\nWelcomeLabel1=Hello\n")
        assertTrue("Expected an IsLanguageFile, was ${file.javaClass.simpleName}", file is IsLanguageFile)
        assertTrue("IsLanguageFile must still be an IsScriptFile for shared tooling", file is IsScriptFile)
    }

    fun testIslFileReportsIslFileType() {
        val file = myFixture.configureByText(IsLanguageFileType.INSTANCE, "[LangOptions]\nLanguageName=English\n")
        assertSame(IsLanguageFileType.INSTANCE, file.fileType)
    }
}
