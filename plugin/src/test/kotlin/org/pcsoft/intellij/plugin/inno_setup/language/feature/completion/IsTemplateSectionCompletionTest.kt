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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion

import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.template.IsTemplateFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Section-name completion after `[` in a free-text `.ist` template file.
 */
class IsTemplateSectionCompletionTest : IsTimedBasePlatformTestCase() {

    fun testSectionNamesOfferedAfterBracket() {
        myFixture.configureByText(IsTemplateFileType.INSTANCE, "[<caret>")
        val items = myFixture.completeBasic()
        val strings = items?.map { it.lookupString } ?: emptyList()
        assertTrue("Expected [Setup] to be offered, was: $strings", "Setup" in strings)
        assertTrue("Expected [Files] to be offered, was: $strings", "Files" in strings)
    }
}
