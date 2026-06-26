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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.script

import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

class PsiUtilsTest : IsTimedBasePlatformTestCase() {

    fun testRelativeMessagesFileIsParsed() {
        myFixture.addFileToProject("MyLang.isl", "[LangOptions]\nLanguageID=\$0407\n")
        val file = myFixture.configureByText(
            "script.iss",
            "[Languages]\nName: x; MessagesFile: \"MyLang.isl\"\n"
        ) as IsScriptFile
        assertEquals("Relative .isl must be parsed for its LanguageID", 0x0407, file.languageId("MyLang.isl"))
    }

    fun testBundledMessagesFileResolvesToEnglish() {
        // Parsed from the Inno installation if present, otherwise via the static fallback table —
        // both yield English ($0409) for compiler:Default.isl.
        val noContext: IsScriptFile? = null
        assertEquals(0x0409, noContext.languageId("compiler:Default.isl"))
    }

    fun testUnknownMessagesFileReturnsNull() {
        val file = myFixture.configureByText(IsScriptFileType.INSTANCE, "[Setup]\nAppName=Test\n") as IsScriptFile
        assertNull(file.languageId("DoesNotExist.isl"))
    }

}