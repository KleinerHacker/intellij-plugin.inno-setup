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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.reference

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

/**
 * Rename-refactoring tests for both reference types.
 *
 *  1. ISPP constant:         renaming #define Name  →  all {#Name} uses updated
 *  2. Section cross-ref:     renaming Name: value  →  all Tasks:/Components:/etc. uses updated
 *
 * ISPP rename integration is also exercised in IsSectionPreprocessorReferenceTest.
 * This file focuses on section-level cross-reference rename.
 */
class IsSectionRenameTest : BasePlatformTestCase() {

    // ═══════════════════════════════════════════════════════════════════════════
    // ISPP constant rename  (#define Name → {#Name})
    // ═══════════════════════════════════════════════════════════════════════════

    fun testIsppRenameUpdatesReference() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define App<caret>Version \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        )
        myFixture.renameElementAtCaret("NewVersion")
        myFixture.checkResult(
            "#define NewVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#NewVersion}\"\n"
        )
    }

    fun testIsppRenameUpdatesMultipleReferences() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define App<caret>Name \"MyApp\"\n[Setup]\nAppName={#AppName}\nAppPublisher={#AppName}\n"
        )
        myFixture.renameElementAtCaret("ProductName")
        myFixture.checkResult(
            "#define ProductName \"MyApp\"\n[Setup]\nAppName={#ProductName}\nAppPublisher={#ProductName}\n"
        )
    }

    fun testIsppRenameOnlyUpdatesMatchingName() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define App<caret>Ver \"1.0\"\n#define AppName \"X\"\n[Setup]\nAppVersion={#AppVer}\nAppName={#AppName}\n"
        )
        myFixture.renameElementAtCaret("Release")
        myFixture.checkResult(
            "#define Release \"1.0\"\n#define AppName \"X\"\n[Setup]\nAppVersion={#Release}\nAppName={#AppName}\n"
        )
    }

    // ═══════════════════════════════════════════════════════════════════════════
    // Section cross-reference rename  (Name: value → Tasks: value)
    // ═══════════════════════════════════════════════════════════════════════════

    fun testRenameSectionNameUpdatesTasksReference() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE, """
                [Setup]
                AppName=Test
                AppVersion=1.0

                [Tasks]
                Name: main<caret>task; Description: "Main Task"

                [Files]
                Source: "app.exe"; DestDir: "{app}"; Tasks: maintask
            """.trimIndent()
        )
        myFixture.renameElementAtCaret("newtask")
        myFixture.checkResult(
            """
                [Setup]
                AppName=Test
                AppVersion=1.0

                [Tasks]
                Name: newtask; Description: "Main Task"

                [Files]
                Source: "app.exe"; DestDir: "{app}"; Tasks: newtask
            """.trimIndent()
        )
    }

    fun testRenameSectionNameUpdatesMultipleReferences() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE, """
                [Setup]
                AppName=Test
                AppVersion=1.0

                [Tasks]
                Name: main<caret>task; Description: "Main Task"

                [Files]
                Source: "a.exe"; DestDir: "{app}"; Tasks: maintask
                Source: "b.exe"; DestDir: "{app}"; Tasks: maintask
            """.trimIndent()
        )
        myFixture.renameElementAtCaret("renamed")
        myFixture.checkResult(
            """
                [Setup]
                AppName=Test
                AppVersion=1.0

                [Tasks]
                Name: renamed; Description: "Main Task"

                [Files]
                Source: "a.exe"; DestDir: "{app}"; Tasks: renamed
                Source: "b.exe"; DestDir: "{app}"; Tasks: renamed
            """.trimIndent()
        )
    }

    fun testRenameSectionNameDoesNotAffectOtherTaskName() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE, """
                [Setup]
                AppName=Test
                AppVersion=1.0

                [Tasks]
                Name: main<caret>task; Description: "Main"
                Name: othertask; Description: "Other"

                [Files]
                Source: "a.exe"; DestDir: "{app}"; Tasks: maintask
                Source: "b.exe"; DestDir: "{app}"; Tasks: othertask
            """.trimIndent()
        )
        myFixture.renameElementAtCaret("renamed")
        myFixture.checkResult(
            """
                [Setup]
                AppName=Test
                AppVersion=1.0

                [Tasks]
                Name: renamed; Description: "Main"
                Name: othertask; Description: "Other"

                [Files]
                Source: "a.exe"; DestDir: "{app}"; Tasks: renamed
                Source: "b.exe"; DestDir: "{app}"; Tasks: othertask
            """.trimIndent()
        )
    }

    fun testRenameComponentNameUpdatesComponentsReference() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE, """
                [Setup]
                AppName=Test
                AppVersion=1.0

                [Components]
                Name: main<caret>comp; Description: "Main Component"

                [Files]
                Source: "app.exe"; DestDir: "{app}"; Components: maincomp
            """.trimIndent()
        )
        myFixture.renameElementAtCaret("corecomp")
        myFixture.checkResult(
            """
                [Setup]
                AppName=Test
                AppVersion=1.0

                [Components]
                Name: corecomp; Description: "Main Component"

                [Files]
                Source: "app.exe"; DestDir: "{app}"; Components: corecomp
            """.trimIndent()
        )
    }
}
