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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Tests for [IsSectionMover] — "Move Statement Up/Down" must swap whole ISS sections
 * (including the trailing blank line each section owns) instead of single lines, and must
 * be blocked at the first/last section boundary.
 */
class IsSectionSectionMoverTest : BasePlatformTestCase() {

    private fun move(action: String, before: String, after: String) {
        myFixture.configureByText(IsScriptFileType.INSTANCE, before)
        myFixture.performEditorAction(action)
        myFixture.checkResult(after)
    }

    /** Three sections, caret on the middle section header (`\[Files]`). */
    private val threeSections =
        """
        [Setup]
        AppName=My App

        [Fi<caret>les]
        Source: "a.txt"

        [Run]
        Filename: "b.exe"
        """.trimIndent() + "\n"

    fun testMoveMiddleSectionDown() {
        move(
            "MoveStatementDown",
            threeSections,
            """
            [Setup]
            AppName=My App

            [Run]
            Filename: "b.exe"
            [Files]
            Source: "a.txt"

            """.trimIndent() + "\n"
        )
    }

    fun testMoveMiddleSectionUp() {
        move(
            "MoveStatementUp",
            threeSections,
            """
            [Files]
            Source: "a.txt"

            [Setup]
            AppName=My App

            [Run]
            Filename: "b.exe"
            """.trimIndent() + "\n"
        )
    }

    fun testMoveFirstSectionUpIsBlocked() {
        val content =
            """
            [Set<caret>up]
            AppName=My App

            [Files]
            Source: "a.txt"
            """.trimIndent() + "\n"
        // first section can not move further up -> unchanged
        move("MoveStatementUp", content, content.replace("<caret>", ""))
    }

    fun testMoveLastSectionDownIsBlocked() {
        val content =
            """
            [Setup]
            AppName=My App

            [Fil<caret>es]
            Source: "a.txt"
            """.trimIndent() + "\n"
        // last section can not move further down -> unchanged
        move("MoveStatementDown", content, content.replace("<caret>", ""))
    }

    fun testMoveFirstSectionDown() {
        move(
            "MoveStatementDown",
            """
            [Set<caret>up]
            AppName=My App

            [Files]
            Source: "a.txt"
            """.trimIndent() + "\n",
            """
            [Files]
            Source: "a.txt"
            [Setup]
            AppName=My App

            """.trimIndent() + "\n"
        )
    }

    /** Caret on a parameter line (not the header) keeps the plain line-wise move. */
    fun testCaretOnParameterLineMovesSingleLine() {
        move(
            "MoveStatementUp",
            """
            [Setup]
            AppName=My App
            Other<caret>Key=Value
            """.trimIndent() + "\n",
            """
            [Setup]
            OtherKey=Value
            AppName=My App
            """.trimIndent() + "\n"
        )
    }
}
