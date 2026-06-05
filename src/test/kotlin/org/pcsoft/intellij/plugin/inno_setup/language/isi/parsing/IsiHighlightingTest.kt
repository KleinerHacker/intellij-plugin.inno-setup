package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

/**
 * Semantic highlighting tests — verifies that IsiAnnotator applies the correct
 * TextAttributesKey to the expected text ranges.
 *
 * HighlightInfo.forcedTextAttributesKey carries the key set via
 * AnnotationHolder.newSilentAnnotation().textAttributes(key).
 */
class IsiHighlightingTest : BasePlatformTestCase() {

    private fun highlights(content: String) =
        myFixture.run {
            configureByText(IssFileType.INSTANCE, content)
            doHighlighting()
        }

    // ── ISPP constant ({#Name}) ───────────────────────────────────────────────

    fun testKnownIsppConstantNameHighlightedAsIsppReferenceName() {
        val text = "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        val all = highlights(text)
        val nameOffset = text.indexOf("{#AppVersion}") + 2  // offset of 'AppVersion' inside {#AppVersion}
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IsiAnnotatorHighlighting.ISPP_REFERENCE_NAME &&
            info.startOffset == nameOffset && info.endOffset == nameOffset + "AppVersion".length
        }
        assertNotNull(
            "The name of a known {#Name} must be highlighted with ISPP_REFERENCE_NAME (blue + italic)", hit
        )
    }

    fun testKnownIsppConstantHashHighlightedAsDirective() {
        val text = "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        val all = highlights(text)
        val hashOffset = text.indexOf("{#AppVersion}") + 1  // offset of '#' inside {#AppVersion}
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IsiAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE &&
            info.startOffset == hashOffset && info.endOffset == hashOffset + 1
        }
        assertNotNull(
            "The '#' inside a known {#Name} must be highlighted with PREPROCESSOR_DIRECTIVE (extension blue)", hit
        )
    }

    fun testUnknownIsppConstantHighlightedAsUnknownReference() {
        val text = "[Files]\nSource: \"app.exe\"; DestDir: \"{#Unknown}\"\n"
        val all = highlights(text)
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IsiAnnotatorHighlighting.UNKNOWN_REFERENCE &&
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
        // Annotator highlights from '#' through the directive keyword as PREPROCESSOR_DIRECTIVE
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IsiAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE &&
            text.substring(info.startOffset, info.endOffset) == "#define"
        }
        assertNotNull(
            "'#define' must be highlighted with PREPROCESSOR_DIRECTIVE (extension blue)", hit
        )
    }

    fun testDefineNameHighlightedAsDefineName() {
        val text = "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        val all = highlights(text)
        val nameOffset = text.indexOf("AppVersion")
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IsiAnnotatorHighlighting.DEFINE_NAME &&
            info.startOffset == nameOffset && info.endOffset == nameOffset + "AppVersion".length
        }
        assertNotNull(
            "The name of a #define must be highlighted with DEFINE_NAME (italic)", hit
        )
    }

    fun testFunctionDefineNameHighlightedAsDefineName() {
        val text = "#define Max(a, b) a > b ? a : b\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        val all = highlights(text)
        val nameOffset = text.indexOf("Max")
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IsiAnnotatorHighlighting.DEFINE_NAME &&
            info.startOffset == nameOffset && info.endOffset == nameOffset + "Max".length
        }
        assertNotNull(
            "The name of a function-like #define (Max) must be highlighted with DEFINE_NAME (italic)", hit
        )
    }

    // ── Section name ──────────────────────────────────────────────────────────

    fun testKnownSectionNameHighlightedAsSectionName() {
        val text = "[Setup]\nAppName=Test\nAppVersion=1.0\n"
        val all = highlights(text)
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IsiAnnotatorHighlighting.SECTION_NAME &&
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
            info.forcedTextAttributesKey == IsiAnnotatorHighlighting.UNKNOWN_REFERENCE &&
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
            info.forcedTextAttributesKey == IsiAnnotatorHighlighting.REFERENCE &&
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
            info.forcedTextAttributesKey == IsiAnnotatorHighlighting.REFERENCE &&
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
            info.forcedTextAttributesKey == IsiAnnotatorHighlighting.PARAM_KEY &&
            text.substring(info.startOffset, info.endOffset) == "AppName"
        }
        assertNotNull(
            "Known directive key 'AppName' must be highlighted with PARAM_KEY attribute", hit
        )
    }
}
