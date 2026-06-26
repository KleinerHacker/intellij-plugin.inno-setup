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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section

import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorSyntaxHighlighting

/**
 * Semantic highlighting tests — verifies that IsSectionAnnotator applies the correct
 * TextAttributesKey to the expected text ranges.
 *
 * HighlightInfo.forcedTextAttributesKey carries the key set via
 * AnnotationHolder.newSilentAnnotation().textAttributes(key).
 */
class IsSectionHighlightingTest : IsTimedBasePlatformTestCase() {

    private fun highlights(content: String) =
        myFixture.run {
            configureByText(IsScriptFileType.INSTANCE, content)
            doHighlighting()
        }

    // ── ISPP constant ({#Name}) ───────────────────────────────────────────────

    fun testKnownIsppConstantNameHighlightedAsIsppReferenceName() {
        val text = "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        val all = highlights(text)
        val nameOffset = text.indexOf("{#AppVersion}") + 2  // offset of 'AppVersion' inside {#AppVersion}
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.ISPP_REFERENCE_NAME &&
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
            info.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE &&
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
            info.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE &&
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
            info.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE &&
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
            info.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.DEFINE_NAME &&
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
            info.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.DEFINE_NAME &&
                    info.startOffset == nameOffset && info.endOffset == nameOffset + "Max".length
        }
        assertNotNull(
            "The name of a function-like #define (Max) must be highlighted with DEFINE_NAME (italic)", hit
        )
    }

    // ── ISPP injected token highlighting (strings / numbers) ──────────────────
    // Inside the ISPP injection these colours are applied through IsPreprocessorAnnotator
    // (the injected SyntaxHighlighter lexer does not paint reliably in the host editor).

    fun testPreprocessorStringHighlighted() {
        val text = "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        val all = highlights(text)
        val strOffset = text.indexOf("\"1.0\"")
        val strEnd = strOffset + "\"1.0\"".length
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IsPreprocessorSyntaxHighlighting.STRING &&
                    info.startOffset >= strOffset && info.endOffset <= strEnd
        }
        assertNotNull(
            "The string \"1.0\" in a #define must be highlighted with the ISPP STRING attribute", hit
        )
    }

    fun testPreprocessorNumberHighlighted() {
        val text = "#define Count 42\n[Files]\nSource: \"app.exe\"; DestDir: \"{app}\"\n"
        val all = highlights(text)
        val numOffset = text.indexOf("42")
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IsPreprocessorSyntaxHighlighting.NUMBER &&
                    info.startOffset == numOffset && info.endOffset == numOffset + "42".length
        }
        assertNotNull(
            "The number 42 in a #define must be highlighted with the ISPP NUMBER attribute", hit
        )
    }

    // ── Section name ──────────────────────────────────────────────────────────

    fun testKnownSectionNameHighlightedAsSectionName() {
        val text = "[Setup]\nAppName=Test\nAppVersion=1.0\n"
        val all = highlights(text)
        val hit = all.firstOrNull { info ->
            info.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.SECTION_NAME &&
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
            info.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE &&
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
            info.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.REFERENCE &&
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
            info.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.REFERENCE &&
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
            info.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.PARAM_KEY &&
                    text.substring(info.startOffset, info.endOffset) == "AppName"
        }
        assertNotNull(
            "Known directive key 'AppName' must be highlighted with PARAM_KEY attribute", hit
        )
    }
}
