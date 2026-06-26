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

import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Tests for [org.pcsoft.intellij.plugin.inno_setup.language.feature.completion.provider.IsPreprocessorDefineNameProvider]:
 * the scope/visibility keywords and (for `#undef`) existing define names offered in the name position.
 */
class IsPreprocessorDefineNameCompletionTest : IsTimedBasePlatformTestCase() {

    private val setupTail = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    private fun lookup(content: String): List<String> {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        myFixture.completeBasic()
        return myFixture.lookupElementStrings ?: emptyList()
    }

    fun testVisibilityKeywordsOfferedForDefineName() {
        val variants = lookup("#define <caret>\n$setupTail")
        assertTrue("Expected scope keyword 'public', was: $variants", "public" in variants)
        assertTrue("Expected scope keyword 'protected', was: $variants", "protected" in variants)
        assertTrue("Expected scope keyword 'private', was: $variants", "private" in variants)
    }

    fun testVisibilityKeywordsOfferedForUndefName() {
        val variants = lookup("#undef <caret>\n$setupTail")
        assertTrue("Expected scope keyword 'public' for #undef, was: $variants", "public" in variants)
    }

    fun testVisibilityKeywordsNotOfferedAfterScopeAlreadyPresent() {
        val variants = lookup("#define public <caret>\n$setupTail")
        assertFalse("Scope keywords must not be re-offered once a scope is present, was: $variants", "public" in variants)
    }

    fun testUndefOffersPrecedingDefineNames() {
        val variants = lookup("#define Foo 1\n#define Bar 2\n#undef <caret>\n$setupTail")
        assertTrue("Expected preceding define 'Foo', was: $variants", "Foo" in variants)
        assertTrue("Expected preceding define 'Bar', was: $variants", "Bar" in variants)
    }

    fun testUndefWithScopeStillOffersDefineNames() {
        val variants = lookup("#define Foo 1\n#undef private <caret>\n$setupTail")
        assertTrue("A scope-prefixed #undef must still offer existing defines, was: $variants", "Foo" in variants)
    }

    fun testDefineNameDoesNotOfferExistingDefineNames() {
        // A #define name is a new declaration — existing names must not be offered (only scope keywords).
        val variants = lookup("#define Foo 1\n#define <caret>\n$setupTail")
        assertFalse("Existing define names must not be offered as a new #define name, was: $variants", "Foo" in variants)
    }

    fun testDefineNameOffersPrecedingDimArrays() {
        // `#define Arr[i]` assigns an array element, so an earlier #dim array is completable in the name position.
        val variants = lookup("#dim Arr[3]\n#define <caret>\n$setupTail")
        assertTrue("Expected preceding #dim array 'Arr' as a #define name, was: $variants", "Arr" in variants)
    }

    fun testVisibilityKeywordsNotOfferedInExpression() {
        val variants = lookup("#define Second <caret>\n$setupTail")
        assertFalse("Scope keywords must not be offered in the expression position, was: $variants", "public" in variants)
    }
}
