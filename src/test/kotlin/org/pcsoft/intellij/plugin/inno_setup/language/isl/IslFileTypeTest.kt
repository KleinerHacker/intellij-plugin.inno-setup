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

package org.pcsoft.intellij.plugin.inno_setup.language.isl

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssLanguage

/**
 * Tests that `.isl` files are recognised as a distinct file type that reuses the ISS language and
 * parses into an [IslFile] (which is also an [IssFile], so all ISS tooling keeps working).
 */
class IslFileTypeTest : BasePlatformTestCase() {

    fun testFileTypeMetadata() {
        assertEquals("isl", IslFileType.INSTANCE.defaultExtension)
        assertSame(IssLanguage, IslFileType.INSTANCE.language)
    }

    fun testIslContentParsesAsIslFile() {
        val file = myFixture.configureByText(IslFileType.INSTANCE, "[Messages]\nWelcomeLabel1=Hello\n")
        assertTrue("Expected an IslFile, was ${file.javaClass.simpleName}", file is IslFile)
        assertTrue("IslFile must still be an IssFile for shared tooling", file is IssFile)
    }

    fun testIslFileReportsIslFileType() {
        val file = myFixture.configureByText(IslFileType.INSTANCE, "[LangOptions]\nLanguageName=English\n")
        assertSame(IslFileType.INSTANCE, file.fileType)
    }
}
