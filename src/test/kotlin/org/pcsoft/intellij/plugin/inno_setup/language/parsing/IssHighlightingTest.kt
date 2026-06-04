package org.pcsoft.intellij.plugin.inno_setup.language.parsing

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

/**
 * Semantic highlighting tests — verifies that IssAnnotator applies the correct
 * TextAttributesKey to the expected text ranges.
 *
 * HighlightInfo.forcedTextAttributesKey carries the key set via
 * AnnotationHolder.newSilentAnnotation().textAttributes(key).
 */
class IssHighlightingTest : BasePlatformTestCase() {

    private fun highlights(content: String) =
        myFixture.run {
            configureByText(IssFileType.INSTANCE, content)
            doHighlighting()
        }

    // ── ISPP constant ({#Name}) ───────────────────────────────────────────────

    fun testKnownIsppConstantHighlightedAsReference() {
        val text = "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        val all = highlights(text)
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IssAnnotatorHighlighting.REFERENCE &&
            text.substring(info.startOffset, info.endOffset) == "{#AppVersion}"
        }
        assertNotNull(
            "Known {#AppVersion} must be highlighted with REFERENCE attribute (CLASS_REFERENCE color)", hit
        )
    }

    fun testKnownIsppConstantHashHighlightedAsKeyword() {
        val text = "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        val all = highlights(text)
        val hashOffset = text.indexOf("{#AppVersion}") + 1  // offset of '#' inside {#AppVersion}
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IssAnnotatorHighlighting.PREPROCESSOR_KEYWORD &&
            info.startOffset == hashOffset && info.endOffset == hashOffset + 1
        }
        assertNotNull(
            "The '#' inside a known {#Name} must be highlighted with PREPROCESSOR_KEYWORD (KEYWORD color)", hit
        )
    }

    fun testUnknownIsppConstantHighlightedAsUnknownReference() {
        val text = "[Files]\nSource: \"app.exe\"; DestDir: \"{#Unknown}\"\n"
        val all = highlights(text)
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IssAnnotatorHighlighting.UNKNOWN_REFERENCE &&
            text.substring(info.startOffset, info.endOffset) == "{#Unknown}"
        }
        assertNotNull(
            "Unknown {#Unknown} must be highlighted with UNKNOWN_REFERENCE attribute (wrong-reference color)", hit
        )
    }

    // ── #define keyword ───────────────────────────────────────────────────────

    fun testPreprocessorDefineKeywordHighlighted() {
        val text = "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        val all = highlights(text)
        // Annotator highlights from '#' through the directive keyword as PREPROCESSOR_KEYWORD
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IssAnnotatorHighlighting.PREPROCESSOR_KEYWORD &&
            text.substring(info.startOffset, info.endOffset) == "#define"
        }
        assertNotNull(
            "'#define' must be highlighted with PREPROCESSOR_KEYWORD (KEYWORD color)", hit
        )
    }

    // ── Section name ──────────────────────────────────────────────────────────

    fun testKnownSectionNameHighlightedAsSectionName() {
        val text = "[Setup]\nAppName=Test\nAppVersion=1.0\n"
        val all = highlights(text)
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IssAnnotatorHighlighting.SECTION_NAME &&
            text.substring(info.startOffset, info.endOffset) == "Setup"
        }
        assertNotNull(
            "Known section name 'Setup' must be highlighted with SECTION_NAME attribute (CLASS_NAME color)", hit
        )
    }

    fun testUnknownSectionNameHighlightedAsUnknownReference() {
        val text = "[UnknownSection]\n"
        val all = highlights(text)
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IssAnnotatorHighlighting.UNKNOWN_REFERENCE &&
            text.substring(info.startOffset, info.endOffset) == "UnknownSection"
        }
        assertNotNull(
            "Unknown section name must be highlighted with UNKNOWN_REFERENCE attribute", hit
        )
    }

    // ── Section cross-reference values (Tasks:, Components:, etc.) ────────────

    fun testTasksReferenceValueHighlightedAsReference() {
        val text = """
            [Setup]
            AppName=Test
            AppVersion=1.0

            [Tasks]
            Name: maintask; Description: "Main Task"

            [Files]
            Source: "app.exe"; DestDir: "{app}"; Tasks: maintask
        """.trimIndent() + "\n"
        val all = highlights(text)
        val taskOffset = text.lastIndexOf("maintask")
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IssAnnotatorHighlighting.REFERENCE &&
            info.startOffset == taskOffset &&
            info.endOffset == taskOffset + "maintask".length
        }
        assertNotNull(
            "Task name 'maintask' in Tasks: parameter must be highlighted with REFERENCE attribute", hit
        )
    }

    fun testComponentsReferenceValueHighlightedAsReference() {
        val text = """
            [Setup]
            AppName=Test
            AppVersion=1.0

            [Components]
            Name: maincomp; Description: "Main"

            [Files]
            Source: "app.exe"; DestDir: "{app}"; Components: maincomp
        """.trimIndent() + "\n"
        val all = highlights(text)
        val offset = text.lastIndexOf("maincomp")
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IssAnnotatorHighlighting.REFERENCE &&
            info.startOffset == offset &&
            info.endOffset == offset + "maincomp".length
        }
        assertNotNull(
            "Component name in Components: parameter must be highlighted with REFERENCE attribute", hit
        )
    }

    // ── Param key ─────────────────────────────────────────────────────────────

    fun testKnownParamKeyHighlightedAsParamKey() {
        val text = "[Setup]\nAppName=Test\nAppVersion=1.0\n"
        val all = highlights(text)
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IssAnnotatorHighlighting.PARAM_KEY &&
            text.substring(info.startOffset, info.endOffset) == "AppName"
        }
        assertNotNull(
            "Known directive key 'AppName' must be highlighted with PARAM_KEY attribute", hit
        )
    }
}
