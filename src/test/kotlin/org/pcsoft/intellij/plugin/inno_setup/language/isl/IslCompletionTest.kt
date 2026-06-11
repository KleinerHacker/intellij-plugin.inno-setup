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

/**
 * Section-name completion must only offer the language-file sections inside `.isl` files.
 */
class IslCompletionTest : BasePlatformTestCase() {

    private fun sectionVariants(): List<String> {
        myFixture.configureByText(IslFileType.INSTANCE, "[<caret>")
        myFixture.completeBasic()
        return myFixture.lookupElementStrings ?: emptyList()
    }

    fun testOnlyLanguageFileSectionsOffered() {
        val variants = sectionVariants()
        assertTrue("Expected 'LangOptions'", "LangOptions" in variants)
        assertTrue("Expected 'Messages'", "Messages" in variants)
        assertTrue("Expected 'CustomMessages'", "CustomMessages" in variants)
    }

    fun testScriptSectionsNotOffered() {
        val variants = sectionVariants()
        assertFalse("'Setup' must not be offered in .isl", "Setup" in variants)
        assertFalse("'Files' must not be offered in .isl", "Files" in variants)
        assertFalse("'Code' must not be offered in .isl", "Code" in variants)
    }
}
