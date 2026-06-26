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

import com.intellij.codeInsight.lookup.LookupElement
import com.intellij.codeInsight.lookup.LookupElementPresentation
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Verifies that deprecated spec members are rendered struck-through in the completion popup
 * (`LookupElementBuilder.withStrikeoutness`). Only constants currently carry deprecated data in the
 * bundled spec, so deprecation is asserted there; for the other element kinds the same wiring is
 * exercised by asserting non-deprecated members are *not* struck out.
 */
class IsSectionDeprecatedCompletionTest : IsTimedBasePlatformTestCase() {

    private val VALID_SETUP = "[Setup]\nAppName=Test\nAppVersion=1.0\n"

    /** Renders the lookup element with the given string and returns its strikeout flag, or null if absent. */
    private fun strikeoutOf(items: Array<LookupElement>, lookupString: String): Boolean? {
        val el = items.firstOrNull { it.lookupString == lookupString } ?: return null
        val presentation = LookupElementPresentation()
        el.renderElement(presentation)
        return presentation.isStrikeout
    }

    private fun complete(content: String): Array<LookupElement> {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        return myFixture.completeBasic() ?: emptyArray()
    }

    fun testDeprecatedConstantIsStruckOut() {
        val items = complete(VALID_SETUP + "[Files]\nSource: \"a.exe\"; DestDir: \"{<caret>\"\n")
        assertEquals("Deprecated constant {pf} must be struck out in completion", true, strikeoutOf(items, "pf"))
    }

    fun testNonDeprecatedConstantIsNotStruckOut() {
        val items = complete(VALID_SETUP + "[Files]\nSource: \"a.exe\"; DestDir: \"{<caret>\"\n")
        assertEquals("Non-deprecated constant {app} must not be struck out", false, strikeoutOf(items, "app"))
    }

    fun testNonDeprecatedAttributeIsNotStruckOut() {
        val items = complete("[Setup]\n<caret>\n")
        assertEquals("Non-deprecated attribute 'AppName' must not be struck out", false, strikeoutOf(items, "AppName"))
    }

    fun testNonDeprecatedFlagIsNotStruckOut() {
        val items = complete(VALID_SETUP + "[Files]\nSource: \"a.exe\"; DestDir: \"{app}\"; Flags: <caret>\n")
        assertEquals(
            "Non-deprecated flag 'ignoreversion' must not be struck out", false,
            strikeoutOf(items, "ignoreversion")
        )
    }
}
