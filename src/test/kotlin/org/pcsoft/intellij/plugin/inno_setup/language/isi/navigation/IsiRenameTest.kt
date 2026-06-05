package org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

/**
 * Rename-refactoring tests for both reference types.
 *
 *  1. ISPP constant:         renaming #define Name  →  all {#Name} uses updated
 *  2. Section cross-ref:     renaming Name: value  →  all Tasks:/Components:/etc. uses updated
 *
 * ISPP rename integration is also exercised in IsiIsppReferenceTest.
 * This file focuses on section-level cross-reference rename.
 */
class IsiRenameTest : BasePlatformTestCase() {

    // ═══════════════════════════════════════════════════════════════════════════
    // ISPP constant rename  (#define Name → {#Name})
    // ═══════════════════════════════════════════════════════════════════════════

    fun testIsppRenameUpdatesReference() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "#define App<caret>Version \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        )
        myFixture.renameElementAtCaret("NewVersion")
        myFixture.checkResult(
            "#define NewVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#NewVersion}\"\n"
        )
    }

    fun testIsppRenameUpdatesMultipleReferences() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "#define App<caret>Name \"MyApp\"\n[Setup]\nAppName={#AppName}\nAppPublisher={#AppName}\n"
        )
        myFixture.renameElementAtCaret("ProductName")
        myFixture.checkResult(
            "#define ProductName \"MyApp\"\n[Setup]\nAppName={#ProductName}\nAppPublisher={#ProductName}\n"
        )
    }

    fun testIsppRenameOnlyUpdatesMatchingName() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
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
            IssFileType.INSTANCE, """
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
        myFixture.checkResult("""
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
            IssFileType.INSTANCE, """
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
        myFixture.checkResult("""
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
            IssFileType.INSTANCE, """
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
        myFixture.checkResult("""
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
            IssFileType.INSTANCE, """
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
        myFixture.checkResult("""
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
