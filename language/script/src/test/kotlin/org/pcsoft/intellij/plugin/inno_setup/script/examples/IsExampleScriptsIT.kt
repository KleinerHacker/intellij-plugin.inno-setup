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

package org.pcsoft.intellij.plugin.inno_setup.script.examples

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.PsiManager
import com.intellij.psi.PsiRecursiveElementWalkingVisitor
import com.intellij.psi.PsiReference
import com.intellij.psi.PsiReferenceBase
import com.intellij.psi.util.PsiTreeUtil
import com.intellij.testFramework.ExtensionTestUtil
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorSymbolProvider
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorSymbolTracker
import org.pcsoft.intellij.plugin.inno_setup.script.language.feature.include.effectiveScriptView
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.IsSectionErrorFilter
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Validates the plugin against the **official Inno Setup example scripts** (`Examples/` of
 * `jrsoftware/issrc`), the reference corpus every real user starts from.
 *
 * The corpus is downloaded by `:language:script:downloadInnoSetupExamples` before the run and removed
 * afterwards — it is deliberately not checked into this repository. Only the derived, source-free
 * fingerprints under `src/test/resources/examples` are committed.
 *
 * Four properties are asserted over every example: it parses without error, every reference resolves, the
 * editor reports neither errors nor warnings, and the effective script matches the recorded expectation for
 * every relevant combination of externally supplied preprocessor symbols.
 */
class IsExampleScriptsIT : IsTimedBasePlatformTestCase() {

    /** Symbols the registered [IsPreprocessorSymbolProvider] hands out; swapped per combination. */
    private val externalSymbols = mutableMapOf<String, String?>()

    override fun getTestDataPath(): String = IsExampleCorpus.directory?.absolutePath ?: super.getTestDataPath()

    override fun setUp() {
        super.setUp()
        val provider = object : IsPreprocessorSymbolProvider {
            override fun symbols(project: Project, script: VirtualFile?): Map<String, String?> =
                externalSymbols.toMap()
        }
        ExtensionTestUtil.maskExtensions(IsPreprocessorSymbolProvider.EP_NAME, listOf(provider), testRootDisposable)
    }

    /**
     * Every example script must parse cleanly: a `PsiErrorElement` the editor would show means the grammar
     * rejects a construct the official examples use, which no user script may hit either.
     *
     * The error elements [IsSectionErrorFilter] suppresses are excluded — that is the plugin's contract, not
     * a defect: `\[Code]` holds free-form Pascal (no `Key=Value` / `Key: Value` syntax and, per STATUS.md, no
     * Pascal support), and a preprocessor line is parsed by the injected ISPP language instead.
     */
    fun testExamplesParseWithoutErrors() {
        val errorFilter = IsSectionErrorFilter()
        val failures = mutableListOf<String>()
        forEachExample { file, name ->
            PsiTreeUtil.findChildrenOfType(file, PsiErrorElement::class.java)
                .filter { errorFilter.shouldHighlightErrorElement(it) }
                .forEach { failures += "  $name [${it.textOffset}] ${it.errorDescription}" }
        }
        assertTrue("The following examples do not parse:\n" + failures.joinToString("\n"), failures.isEmpty())
    }

    /**
     * Every non-soft reference of every example must resolve. Soft references are excluded by contract: they
     * are allowed to point at something that does not exist (that is what makes them soft).
     */
    fun testExampleReferencesResolve() {
        val unresolved = mutableListOf<String>()
        forEachExample { file, name ->
            file.accept(object : PsiRecursiveElementWalkingVisitor() {
                override fun visitElement(element: PsiElement) {
                    element.references.forEach { reference ->
                        if (!reference.isHard()) return@forEach
                        if (reference.resolve() == null) {
                            unresolved += "  $name [${element.textOffset}] '${reference.canonicalText}' " +
                                    "(${reference.javaClass.simpleName})"
                        }
                    }
                    super.visitElement(element)
                }
            })
        }
        assertTrue(
            "The following references of the examples do not resolve:\n" + unresolved.joinToString("\n"),
            unresolved.isEmpty(),
        )
    }

    /**
     * Highlighting an example must report neither errors nor warnings: the official examples are correct by
     * definition, so anything the annotators flag on them is a false positive of this plugin.
     */
    fun testExamplesHighlightWithoutErrorsOrWarnings() {
        val problems = mutableListOf<String>()
        forEachExample { _, name, virtualFile ->
            myFixture.configureFromExistingVirtualFile(virtualFile)
            myFixture.doHighlighting()
                .filter { it.severity.name == "ERROR" || it.severity.name == "WARNING" }
                .filterNot { it.description.orEmpty().contains(ENVIRONMENT_DEPENDENT) }
                .forEach {
                    problems += "  $name [${it.severity.name} ${it.startOffset}-${it.endOffset}] ${it.description}"
                }
        }
        assertTrue(
            "The following examples report errors or warnings:\n" + problems.joinToString("\n"),
            problems.isEmpty(),
        )
    }

    /**
     * The effective script — includes inlined and every decidable `#if` resolved — must match the recorded
     * expectation for every combination of externally supplied preprocessor symbols the example reacts to.
     * The combinations are derived from the script itself (see `IsExampleCorpus.combinationsOf`), so a new
     * example or a new `#ifdef` is covered automatically.
     */
    fun testEffectiveScriptsMatchExpectedFingerprints() {
        val failures = mutableListOf<String>()
        val generated = mutableListOf<String>()

        forEachExample { file, name ->
            IsExampleCorpus.combinationsOf(file.virtualFile.toIoFile()).forEach { combination ->
                externalSymbols.clear()
                externalSymbols.putAll(combination.defines)
                IsPreprocessorSymbolTracker.getInstance(project).symbolsChanged()

                val actual = IsEffectiveScriptFingerprint.of(file.effectiveScriptView().text)
                val expectationFile = IsExampleCorpus.expectationFile(file.virtualFile.toIoFile(), combination)

                if (!expectationFile.isFile) {
                    if (IsExampleCorpus.updateFingerprints) {
                        expectationFile.parentFile.mkdirs()
                        expectationFile.writeText(actual.render())
                        generated += expectationFile.name
                    } else {
                        failures += "$name@${combination.key}: no expectation recorded. Expected file " +
                                "${expectationFile.path} with:\n${actual.render().prependIndent("    ")}"
                    }
                    return@forEach
                }

                val expected = IsEffectiveScriptFingerprint.parse(expectationFile.readText())
                if (expected != actual) {
                    if (IsExampleCorpus.updateFingerprints) {
                        expectationFile.writeText(actual.render())
                        generated += expectationFile.name
                    } else {
                        failures += "$name@${combination.key}: effective script differs from " +
                                "${expectationFile.name}:\n${expected.diffTo(actual)}"
                    }
                }
            }
        }

        if (generated.isNotEmpty()) {
            println("Wrote ${generated.size} fingerprint file(s): ${generated.joinToString()}")
        }
        assertTrue(
            "The effective scripts of the following examples do not match their expectation " +
                    "(regenerate with -PupdateExampleFingerprints after verifying the change):\n" +
                    failures.joinToString("\n"),
            failures.isEmpty(),
        )
    }

    // ── plumbing ─────────────────────────────────────────────────────────────────────────────

    /**
     * Copies the whole corpus into the fixture project — the payload files (`MyProg.exe`, `License.txt`, …)
     * included, because the file references and `GetVersionNumbersString` computations of the scripts point at
     * them — and runs [body] for every `*.iss` of it.
     */
    private fun forEachExample(body: (IsScriptFile, String, VirtualFile) -> Unit) {
        val directory = IsExampleCorpus.directory
        if (directory == null) {
            println(
                "Skipping ${javaClass.simpleName}: the Inno Setup example corpus is not available. " +
                        "Run this suite through Gradle (./gradlew :language:script:integrationTest), which " +
                        "downloads it."
            )
            return
        }

        val copied = directory.listFiles().orEmpty()
            .filter { it.isFile }
            .sortedBy { it.name }
            .map { myFixture.copyFileToProject(it.name, it.name) }
        val scripts = copied.filter { it.extension.equals("iss", ignoreCase = true) }
        assertTrue("No *.iss files found in the example corpus at $directory", scripts.isNotEmpty())

        scripts.forEach { virtualFile ->
            val file = PsiManager.getInstance(project).findFile(virtualFile) as? IsScriptFile
            assertNotNull("${virtualFile.name} was not recognised as an Inno Setup script", file)
            body(file!!, virtualFile.name, virtualFile)
        }
    }

    /** Overload for the cases that do not need the [VirtualFile]. */
    private fun forEachExample(body: (IsScriptFile, String) -> Unit) =
        forEachExample { file, name, _ -> body(file, name) }

    /** A soft reference may legitimately not resolve, so only hard references are asserted. */
    private fun PsiReference.isHard(): Boolean = (this as? PsiReferenceBase<*>)?.isSoft != true

    /** The corpus file behind a copied fixture file — the expectation resources are keyed by its name. */
    private fun VirtualFile.toIoFile(): java.io.File = java.io.File(IsExampleCorpus.directory, name)

    private companion object {
        /**
         * Warnings about `compiler:` paths that cannot be checked because no Inno Setup installation is
         * configured. They describe the state of the machine running the test, not a property of the script,
         * and would make the suite depend on an Inno Setup installation being present on every dev box and
         * CI runner. The `compiler:` handling itself is covered by the dedicated developer tests.
         */
        const val ENVIRONMENT_DEPENDENT = "Inno Setup installation path not configured"
    }
}
