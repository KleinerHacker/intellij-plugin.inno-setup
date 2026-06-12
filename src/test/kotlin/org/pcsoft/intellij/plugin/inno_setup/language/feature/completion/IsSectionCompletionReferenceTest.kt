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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Completion tests at cross-section reference points (Tasks:, Components:, Types:, Languages:).
 *
 * Caret is placed AFTER the colon+space with no prefix typed, so prefix-filtering
 * does not hide any names.  Two or more names are always defined so IntelliJ shows
 * a popup (single-item completion would be auto-applied, making lookupElementStrings null).
 */
class IsSectionCompletionReferenceTest : BasePlatformTestCase() {

    // ── Tasks cross-reference completion ─────────────────────────────────────

    fun testTasksCompletionShowsAllTaskNames() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE, """
            [Setup]
            AppName=Test
            AppVersion=1.0

            [Tasks]
            Name: maintask; Description: "Main Task"
            Name: othertask; Description: "Other Task"

            [Files]
            Source: "app.exe"; DestDir: "{app}"; Tasks: <caret>
        """.trimIndent()
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected a completion popup for Tasks: parameter", variants)
        assertTrue("Expected 'maintask' in task name suggestions", "maintask" in variants!!)
        assertTrue("Expected 'othertask' in task name suggestions", "othertask" in variants)
    }

    fun testTasksCompletionDoesNotShowComponentNames() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE, """
            [Setup]
            AppName=Test
            AppVersion=1.0

            [Tasks]
            Name: maintask; Description: "Main Task"
            Name: othertask; Description: "Other Task"

            [Components]
            Name: maincomp; Description: "Main Component"
            Name: extracomp; Description: "Extra Component"

            [Files]
            Source: "app.exe"; DestDir: "{app}"; Tasks: <caret>
        """.trimIndent()
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings ?: emptyList()
        assertTrue("Tasks: completion must show task names", "maintask" in variants)
        assertFalse("Tasks: completion must not show component names", "maincomp" in variants)
        assertFalse("Tasks: completion must not show component names", "extracomp" in variants)
    }

    // ── Components cross-reference completion ─────────────────────────────────

    fun testComponentsCompletionShowsAllComponentNames() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE, """
            [Setup]
            AppName=Test
            AppVersion=1.0

            [Components]
            Name: core; Description: "Core"
            Name: extra; Description: "Extra"

            [Files]
            Source: "app.exe"; DestDir: "{app}"; Components: <caret>
        """.trimIndent()
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected a completion popup for Components: parameter", variants)
        assertTrue("Expected 'core' in component name suggestions", "core" in variants!!)
        assertTrue("Expected 'extra' in component name suggestions", "extra" in variants)
    }

    // ── Types cross-reference completion ─────────────────────────────────────

    fun testTypesCompletionShowsAllTypeNames() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE, """
            [Setup]
            AppName=Test
            AppVersion=1.0

            [Types]
            Name: full; Description: "Full Installation"
            Name: compact; Description: "Compact Installation"

            [Components]
            Name: core; Description: "Core"; Types: <caret>
        """.trimIndent()
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected a completion popup for Types: parameter", variants)
        assertTrue("Expected 'full' in type name suggestions", "full" in variants!!)
        assertTrue("Expected 'compact' in type name suggestions", "compact" in variants)
    }

    // ── Languages cross-reference completion ──────────────────────────────────

    fun testLanguagesCompletionShowsAllLanguageNames() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE, """
            [Setup]
            AppName=Test
            AppVersion=1.0

            [Languages]
            Name: english; MessagesFile: "compiler:Default.isl"
            Name: german; MessagesFile: "compiler:Languages\German.isl"

            [Tasks]
            Name: mytask; Description: "Task"; Languages: <caret>
        """.trimIndent()
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings
        assertNotNull("Expected a completion popup for Languages: parameter", variants)
        assertTrue("Expected 'english' in language name suggestions", "english" in variants!!)
        assertTrue("Expected 'german' in language name suggestions", "german" in variants)
    }

    // ── Non-reference keys must not get cross-ref completion ──────────────────

    fun testSourceParamDoesNotGetCrossRefCompletion() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE, """
            [Setup]
            AppName=Test
            AppVersion=1.0

            [Tasks]
            Name: maintask; Description: "Task"
            Name: othertask; Description: "Task2"

            [Files]
            Source: ma<caret>; DestDir: "{app}"
        """.trimIndent()
        )
        myFixture.completeBasic()
        val variants = myFixture.lookupElementStrings ?: emptyList()
        assertFalse(
            "Source: value must not offer task names as cross-ref completions",
            "maintask" in variants
        )
    }
}
