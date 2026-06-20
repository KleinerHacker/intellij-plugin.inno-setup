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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.template.action

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.template.IsTemplateFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.template.IsTemplateFileType

/**
 * The "New Inno Setup Template" action creates an empty `.ist` file (its `actionPerformed` is gated behind
 * a modal dialog and cannot run headless). This test verifies the resulting artifact: an empty file that is
 * recognized as an [IsTemplateFile].
 */
class IsTemplateCreateFileActionTest : BasePlatformTestCase() {

    fun testCreatedTemplateFileIsEmptyAndRecognized() {
        val file = myFixture.addFileToProject("part.ist", "")
        assertTrue("A .ist file must be an IsTemplateFile", file is IsTemplateFile)
        assertEquals(IsTemplateFileType.INSTANCE, file.fileType)
        assertEquals("A freshly created template file must be empty", "", file.text)
    }
}
