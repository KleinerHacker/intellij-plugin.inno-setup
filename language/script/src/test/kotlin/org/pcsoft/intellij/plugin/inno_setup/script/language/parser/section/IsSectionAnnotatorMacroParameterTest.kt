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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section

import com.intellij.lang.annotation.HighlightSeverity
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Tests the fully modelled parameter list of a function-like `#define`: declared types (`int`/`str`/`any`),
 * by-reference parameters (`*`), default values, and how all of that behaves through several nested macro
 * levels.
 */
class IsSectionAnnotatorMacroParameterTest : IsTimedBasePlatformTestCase() {

    /** The error descriptions of [content] that contain [fragment]. */
    private fun errorsContaining(content: String, fragment: String): List<String> {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        return myFixture.doHighlighting()
            .filter { it.severity == HighlightSeverity.ERROR }
            .mapNotNull { it.description }
            .filter { it.contains(fragment, ignoreCase = true) }
    }

    /** All error descriptions of [content]. */
    private fun errors(content: String): List<String> {
        myFixture.configureByText(IsScriptFileType.INSTANCE, content)
        return myFixture.doHighlighting()
            .filter { it.severity == HighlightSeverity.ERROR }
            .mapNotNull { it.description }
    }

    // ── declared parameter types ──────────────────────────────────────────────

    /** A declared `int` parameter rejects a string argument even when the body would tolerate it. */
    fun testDeclaredIntParameterRejectsStringArgument() {
        val hits = errorsContaining(
            "#define Echo(int A) A\n#define X Echo(\"a\")\n[Setup]\nAppName={#X}\n",
            "must be int",
        )
        assertFalse("A declared int parameter must reject a string argument", hits.isEmpty())
    }

    /** A declared `str` parameter accepts a string argument without complaint. */
    fun testDeclaredStrParameterAcceptsStringArgument() {
        val hits = errorsContaining(
            "#define Echo(str S) S\n#define X Echo(\"a\")\n[Setup]\nAppName={#X}\n",
            "of macro",
        )
        assertTrue("A correctly typed argument must not be flagged, but got: $hits", hits.isEmpty())
    }

    /** `any` explicitly widens a parameter, so neither argument type may be reported. */
    fun testAnyParameterAcceptsEveryArgument() {
        val hits = errorsContaining(
            "#define Echo(any V) 1\n#define X Echo(\"a\")\n#define Y Echo(2)\n[Setup]\nAppName={#X}\n",
            "of macro",
        )
        assertTrue("An 'any' parameter must accept every argument, but got: $hits", hits.isEmpty())
    }

    /** The declared type must not leak into the parameter name — `int A` declares `A`, not `int A`. */
    fun testTypedParameterIsUsableInTheBody() {
        val hits = errorsContaining(
            "#define Twice(int A) A * 2\n[Setup]\nAppName=T\n",
            "Unresolved",
        )
        assertTrue("A typed parameter must be usable in the body, but got: $hits", hits.isEmpty())
    }

    // ── default values ────────────────────────────────────────────────────────

    /** A parameter with a default may be omitted at the call site. */
    fun testOptionalParameterMayBeOmitted() {
        val hits = errorsContaining(
            "#define Mul(int A, int B = 10) A * B\n#define X Mul(2)\n[Setup]\nAppName={#X}\n",
            "expects",
        )
        assertTrue("An omitted optional argument must be accepted, but got: $hits", hits.isEmpty())
    }

    /** A mandatory parameter still has to be supplied — the default must not make everything optional. */
    fun testMandatoryParameterIsStillRequired() {
        val hits = errorsContaining(
            "#define Mul(int A, int B = 10) A * B\n#define X Mul()\n[Setup]\nAppName={#X}\n",
            "expects",
        )
        assertFalse("A missing mandatory argument must be reported", hits.isEmpty())
    }

    /** Supplying more arguments than declared stays an error. */
    fun testTooManyArgumentsAreReported() {
        val hits = errorsContaining(
            "#define Mul(int A, int B = 10) A * B\n#define X Mul(1, 2, 3)\n[Setup]\nAppName={#X}\n",
            "expects",
        )
        assertFalse("A superfluous argument must be reported", hits.isEmpty())
    }

    /** The default value is used for the omitted argument, so the macro's value stays computable. */
    fun testDefaultValueIsUsedForTheOmittedArgument() {
        val hits = errorsContaining(
            "#define Concat(str A, str B = \"!\") A + B\n#define X Concat(\"a\")\n" +
                    "#define Y X + 1\n[Setup]\nAppName=T\nAppVersion=1.0\n",
            "cannot combine a string operand",
        )
        assertFalse("The default \"!\" makes the result a string, so `X + 1` must be flagged", hits.isEmpty())
    }

    // ── by-reference parameters ───────────────────────────────────────────────

    /** A by-reference parameter needs a macro name ISPP can write back into, not a literal. */
    fun testByReferenceParameterRejectsALiteral() {
        val hits = errorsContaining(
            "#define Fill(int *Out) Out\n#define X Fill(1)\n[Setup]\nAppName={#X}\n",
            "by reference",
        )
        assertFalse("A literal in a by-reference position must be reported", hits.isEmpty())
    }

    /** Passing a macro name to a by-reference parameter is the correct usage and must not be flagged. */
    fun testByReferenceParameterAcceptsAMacroName() {
        val hits = errorsContaining(
            "#define Target 0\n#define Fill(int *Out) Out\n#define X Fill(Target)\n[Setup]\nAppName={#X}\n",
            "by reference",
        )
        assertTrue("A macro name is a valid by-reference argument, but got: $hits", hits.isEmpty())
    }

    // ── deep macros ───────────────────────────────────────────────────────────

    /** A three-level macro chain mixing declared types, `any`, a default and a built-in call stays clean. */
    fun testDeepMacroChainProducesNoErrors() {
        val hits = errors(
            "#define Inner(int N) Str(N) + \"!\"\n" +
                    "#define Middle(str S, int N = 2) S + Inner(N)\n" +
                    "#define Outer(str Prefix, any N) Prefix + Middle(\"x\", N)\n" +
                    "#define Result Outer(\"p\", 3)\n" +
                    "[Setup]\nAppName=T\nAppVersion=1.0\n"
        )
        assertTrue("A well-typed three-level macro chain must produce no errors, but got: $hits", hits.isEmpty())
    }

    /** The declared type of the outermost level is enforced at its own call site. */
    fun testDeepMacroChainReportsWrongArgumentTypeOnTheOuterLevel() {
        val hits = errorsContaining(
            "#define Inner(int N) Str(N) + \"!\"\n" +
                    "#define Middle(str S, int N = 2) S + Inner(N)\n" +
                    "#define Outer(str Prefix, any N) Prefix + Middle(\"x\", N)\n" +
                    "#define Result Outer(1, 3)\n" +
                    "[Setup]\nAppName={#Result}\n",
            "must be str",
        )
        assertFalse("A wrongly typed argument of the outer macro must be reported", hits.isEmpty())
    }

    /** A parameter of an outer macro passed on to an inner one is checked against the inner declaration. */
    fun testDeepMacroChainReportsWrongArgumentTypeOnTheInnerLevel() {
        val hits = errorsContaining(
            "#define Inner(int N) N * 2\n" +
                    "#define Middle(str S) Inner(S)\n" +
                    "#define Result Middle(\"x\")\n" +
                    "[Setup]\nAppName={#Result}\n",
            "must be int",
        )
        assertFalse("Passing a str parameter into an int parameter must be reported", hits.isEmpty())
    }

    /** A missing argument on an inner level is reported there, not swallowed by the outer call. */
    fun testDeepMacroChainReportsMissingArgumentOnTheInnerLevel() {
        val hits = errorsContaining(
            "#define Inner(int A, int B) A + B\n" +
                    "#define Middle(int N) Inner(N)\n" +
                    "#define Result Middle(1)\n" +
                    "[Setup]\nAppName={#Result}\n",
            "expects",
        )
        assertFalse("A missing argument of the inner macro must be reported", hits.isEmpty())
    }

    /** The result type of the outermost level flows into its caller and is type-checked there. */
    fun testDeepMacroChainResultTypeFlowsIntoTheCaller() {
        val hits = errorsContaining(
            "#define Inner(int N) Str(N)\n" +
                    "#define Outer(int N) Inner(N) + \"!\"\n" +
                    "#define Result Outer(1) * 2\n" +
                    "[Setup]\nAppName={#Result}\n",
            "cannot be applied to a string operand",
        )
        assertFalse("The str result of the chain must be rejected by `* 2`, but got no error", hits.isEmpty())
    }

    /** A recursive macro must be cut by the cycle guard instead of recursing until the stack blows. */
    fun testRecursiveMacroIsHandledWithoutStackOverflow() {
        val hits = errorsContaining(
            "#define Rec(int N) Rec(N) + 1\n#define X Rec(1)\n[Setup]\nAppName={#X}\n",
            "of macro",
        )
        assertTrue("A recursive macro must not produce a spurious argument error, but got: $hits", hits.isEmpty())
    }

    /** A by-reference parameter deep inside the chain is still checked at its own call site. */
    fun testDeepMacroChainChecksByReferenceOnTheInnerLevel() {
        val hits = errorsContaining(
            "#define Fill(int *Out) Out\n" +
                    "#define Middle(int N) Fill(1)\n" +
                    "#define Result Middle(1)\n" +
                    "[Setup]\nAppName={#Result}\n",
            "by reference",
        )
        assertFalse("The by-reference violation inside Middle must be reported", hits.isEmpty())
    }
}
