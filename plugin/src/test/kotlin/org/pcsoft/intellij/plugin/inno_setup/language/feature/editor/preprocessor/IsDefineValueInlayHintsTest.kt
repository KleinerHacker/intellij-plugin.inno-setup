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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.preprocessor

import com.intellij.codeInsight.hints.InlayGroup
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Developer tests for [IsDefineValueInlayHintsProvider]: drives the hint computation over real PSI with the
 * ISPP fragments injected into an `.iss` host file and asserts which `#define` lines are annotated and with
 * which value.
 *
 * The rendered inlay text itself is not inspected — the platform's inlay dump utility is `@ApiStatus.Internal`
 * and therefore off limits. Instead the provider's value computation is asserted directly, which is the part
 * that carries the actual logic; that the values end up on the editor's after-line-end inlays is covered by
 * [testHintsAreAnchoredOnTheDefineLine].
 */
class IsDefineValueInlayHintsTest : IsTimedBasePlatformTestCase() {

    /** Configures `main.iss` with [body] and returns the computed hints as `line number to value`. */
    private fun hintsFor(body: String): List<Pair<Int, String>> {
        val host = myFixture.addFileToProject("main.iss", body)
        myFixture.configureFromExistingVirtualFile(host.virtualFile)
        val document = myFixture.editor.document
        return computeDefineValueHints(myFixture.file)
            .map { (offset, value) -> document.getLineNumber(offset) to value }
    }

    /** The values of all computed hints, ignoring their position. */
    private fun valuesFor(body: String): List<String> = hintsFor(body).map { it.second }

    /**
     * A `#define` concatenating two earlier string macros is annotated with the resulting string, so the
     * assembled name is readable without compiling the script.
     */
    fun testConcatenationOfTwoDefinesIsAnnotated() {
        val values = valuesFor(
            """
            [Setup]
            AppName=Test
            #define MyName "MyApp"
            #define MyExe MyName + ".exe"
            """.trimIndent()
        )
        assertEquals(listOf("\"MyApp.exe\""), values)
    }

    /**
     * A chain of macros referencing each other across three levels resolves transitively, so the final macro
     * shows the fully assembled value.
     */
    fun testChainAcrossThreeLevelsIsResolved() {
        val values = valuesFor(
            """
            [Setup]
            AppName=Test
            #define MyName "MyApp"
            #define MyVer "1.5"
            #define MyTitle MyName + " " + MyVer
            #define MyCaption MyTitle + " Setup"
            """.trimIndent()
        )
        assertEquals(listOf("\"MyApp 1.5\"", "\"MyApp 1.5 Setup\""), values)
    }

    /**
     * An arithmetic expression over integer macros is evaluated and annotated with the resulting number.
     */
    fun testIntegerArithmeticIsAnnotated() {
        val values = valuesFor(
            """
            [Setup]
            AppName=Test
            #define Major 2
            #define Minor 3
            #define Build Major * 100 + Minor
            """.trimIndent()
        )
        assertEquals(listOf("203"), values)
    }

    /**
     * A call to a built-in function is not annotated: the value resolver evaluates user-defined macros only
     * and never executes an ISPP built-in, so even a pure one over a known argument stays uncomputable.
     */
    fun testBuiltinCallIsNotAnnotated() {
        val values = valuesFor(
            """
            [Setup]
            AppName=Test
            #define Major 7
            #define MajorStr Str(Major)
            """.trimIndent()
        )
        assertEmpty(values)
    }

    /**
     * A call to a user-defined function-like macro is evaluated with the argument bound, so the calling
     * `#define` shows the resulting value.
     */
    fun testCallOfUserMacroIsAnnotated() {
        val values = valuesFor(
            """
            [Setup]
            AppName=Test
            #define Suffix ".exe"
            #define ExeOf(str Name) Name + Suffix
            #define MyExe ExeOf("MyApp")
            """.trimIndent()
        )
        assertEquals(listOf("\"MyApp.exe\""), values)
    }

    /**
     * A `#define` whose value is a plain string literal produces no hint — the value is already written out in
     * the source, so the hint would only repeat it.
     */
    fun testPlainStringLiteralIsNotAnnotated() {
        val values = valuesFor(
            """
            [Setup]
            AppName=Test
            #define MyVer "1.5"
            """.trimIndent()
        )
        assertEmpty(values)
    }

    /**
     * A `#define` whose value is a plain integer literal produces no hint, for the same reason as a string
     * literal.
     */
    fun testPlainIntLiteralIsNotAnnotated() {
        val values = valuesFor(
            """
            [Setup]
            AppName=Test
            #define Major 2
            """.trimIndent()
        )
        assertEmpty(values)
    }

    /**
     * A function-like macro has no single static value — its result depends on the arguments of each call —
     * and is therefore never annotated.
     */
    fun testFunctionMacroIsNotAnnotated() {
        val values = valuesFor(
            """
            [Setup]
            AppName=Test
            #define Suffix ".exe"
            #define ExeOf(str Name) Name + Suffix
            """.trimIndent()
        )
        assertEmpty(values)
    }

    /**
     * An array element `#define` is not annotated: it assigns into an array slot rather than declaring a macro
     * with one value.
     */
    fun testArrayElementDefineIsNotAnnotated() {
        val values = valuesFor(
            """
            [Setup]
            AppName=Test
            #define Base "a"
            #dim Names[2]
            #define Names[0] Base + "b"
            """.trimIndent()
        )
        assertEmpty(values)
    }

    /**
     * A value-less `#define` (a pure conditional-compilation symbol) has nothing to show and is not annotated.
     */
    fun testValuelessDefineIsNotAnnotated() {
        val values = valuesFor(
            """
            [Setup]
            AppName=Test
            #define WithDebug
            """.trimIndent()
        )
        assertEmpty(values)
    }

    /**
     * An expression reading the environment produces no hint rather than a wrong or empty one — its value is
     * only known to the compiler.
     */
    fun testEnvironmentDependentValueIsNotAnnotated() {
        val values = valuesFor(
            """
            [Setup]
            AppName=Test
            #define BuildTag GetEnv("BUILD_TAG")
            """.trimIndent()
        )
        assertEmpty(values)
    }

    /**
     * An expression referencing an undeclared macro stays uncomputable and is not annotated.
     */
    fun testUnresolvedReferenceIsNotAnnotated() {
        val values = valuesFor(
            """
            [Setup]
            AppName=Test
            #define MyExe Unknown + ".exe"
            """.trimIndent()
        )
        assertEmpty(values)
    }

    /**
     * The hint is anchored on the line of its own `#define`, so it is rendered at the end of exactly that line.
     */
    fun testHintsAreAnchoredOnTheDefineLine() {
        val hints = hintsFor(
            """
            [Setup]
            AppName=Test
            #define MyName "MyApp"
            #define MyExe MyName + ".exe"
            """.trimIndent()
        )
        assertEquals(listOf(3 to "\"MyApp.exe\""), hints)
    }

    /**
     * A file without any preprocessor directive contributes no hints at all.
     */
    fun testScriptWithoutDirectivesHasNoHints() {
        assertEmpty(valuesFor("[Setup]\nAppName=Test\n"))
    }

    /**
     * The `group` attribute of the `codeInsight.declarativeInlayProvider` registration names a real
     * [InlayGroup] constant.
     *
     * Neither `build` nor `verifyPlugin` validates that attribute — an invalid value would only surface at
     * runtime, where the extension bean fails and the provider silently never registers.
     */
    fun testRegisteredInlayGroupExists() {
        assertNotNull(
            "plugin.xml registers the provider in this group",
            @Suppress("UnstableApiUsage") InlayGroup.valueOf(REGISTERED_GROUP)
        )
    }

    /**
     * The provider is actually reachable through its extension point: a highlighting pass over a script with a
     * computed `#define` puts an after-line-end inlay into the editor.
     *
     * This is the end-to-end counterpart to the value assertions above — they call the computation directly and
     * would stay green even if the registration in `plugin.xml` were broken.
     */
    fun testProviderIsRegisteredAndRendersInlay() {
        val host = myFixture.addFileToProject(
            "main.iss",
            """
            [Setup]
            AppName=Test
            #define MyName "MyApp"
            #define MyExe MyName + ".exe"
            """.trimIndent()
        )
        myFixture.configureFromExistingVirtualFile(host.virtualFile)
        myFixture.doHighlighting()

        val document = myFixture.editor.document
        val inlays = myFixture.editor.inlayModel.getAfterLineEndElementsInRange(0, document.textLength)
        assertFalse("The declarative provider contributed an inlay", inlays.isEmpty())
    }

    private companion object {
        /** Kept in sync with the `group` attribute in `plugin.xml`. */
        const val REGISTERED_GROUP = "VALUES_GROUP"
    }
}
