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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing

import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFileType

class IsSectionAnnotatorPreprocessorTest : BasePlatformTestCase() {

    fun testKnownIsppConstantProducesNoError() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        )
        val highlights = myFixture.doHighlighting()
        val errors = highlights.filter {
            it.severity.name == "ERROR" && it.description?.contains("Unknown constant") == true
        }
        assertTrue(
            "Known ISPP constant {#AppVersion} should produce no 'Unknown constant' error",
            errors.isEmpty()
        )
    }

    fun testUnknownIsppConstantProducesError() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Files]\nSource: \"app.exe\"; DestDir: \"{#Unknown}\"\n"
        )
        val highlights = myFixture.doHighlighting()
        val errors = highlights.filter {
            it.severity.name == "ERROR" && it.description?.contains("Unknown constant") == true
        }
        assertTrue("Unknown ISPP constant {#Unknown} should produce an error", errors.isNotEmpty())
    }

    // ── Unused #define ────────────────────────────────────────────────────────

    fun testUnusedDefineProducesWeakWarning() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define UnusedConst \"value\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("never used", ignoreCase = true) == true
        }
        assertTrue("Unused #define must produce a 'never used' WEAK_WARNING", hit)
    }

    fun testUnusedDefineIsHighlightedAsUnused() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define UnusedConst \"value\"\n[Setup]\nAppName=Test\nAppVersion=1.0\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.forcedTextAttributesKey == IsSectionAnnotatorHighlighting.UNUSED &&
                    it.severity == HighlightSeverity.WEAK_WARNING
        }
        assertTrue("Unused #define must use the UNUSED text attribute", hit)
    }

    fun testUsedDefineViaConstantReferenceProducesNoUnusedWarning() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define AppVer \"1.0\"\n[Setup]\nAppName=Test\nAppVersion={#AppVer}\n"
        )
        val highlights = myFixture.doHighlighting()
        val hit = highlights.any {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("never used", ignoreCase = true) == true
        }
        assertFalse("#define used via {#Name} must not produce a 'never used' warning", hit)
    }

    fun testUsedDefineViaCrossReferenceProducesNoUnusedWarning() {
        // Base is used by Full's expression; Full is used via {#Full}.
        // Neither must be flagged as unused.
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "#define Base \"1\"\n#define Full Base\n[Setup]\nAppName={#Full}\nAppVersion=1.0\n"
        )
        val highlights = myFixture.doHighlighting()
        val baseWarnings = highlights.filter {
            it.severity == HighlightSeverity.WEAK_WARNING &&
                    it.description?.contains("'Base'", ignoreCase = true) == true &&
                    it.description?.contains("never used", ignoreCase = true) == true
        }
        assertTrue(
            "'Base' used in another #define expression must not be flagged as unused",
            baseWarnings.isEmpty()
        )
    }
}
