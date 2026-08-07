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

package org.pcsoft.intellij.plugin.inno_setup.build.examples

import com.intellij.openapi.components.service
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.openapi.vfs.newvfs.impl.VfsRootAccess
import com.intellij.task.TaskRunnerResults
import com.intellij.util.ThrowableRunnable
import junit.framework.TestCase
import junit.framework.TestSuite
import org.pcsoft.intellij.plugin.inno_setup.build.IsCompilerService
import org.pcsoft.intellij.plugin.inno_setup.build.run.IsScriptRunnerLogic
import org.pcsoft.intellij.plugin.inno_setup.script.settings.IsSettingsService
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import java.io.File

/**
 * Compiles **every official Inno Setup example script** (`Examples/` of `jrsoftware/issrc`) through the
 * plugin's own build pipeline — [IsCompilerService], the very code path the IDE action and the run
 * configuration use — with the real `ISCC.exe` of the locally installed Inno Setup.
 *
 * One test case is generated per example (see [suite]), so a broken example is reported by name instead of
 * hiding behind a single aggregate failure. Both the corpus and the Inno Setup installation are mandatory:
 * the corpus is provided by `:plugin:downloadInnoSetupExamples`, the installation by `-PinnoSetupHome` /
 * `INNO_SETUP_HOME` (see [IsBuildExampleCorpus]). Examples whose payload is not part of the corpus are
 * excluded through `src/test/resources/examples/build-skip.yaml`.
 *
 * A broken environment — no corpus, no `ISCC.exe` — is reported through this same class rather than through a
 * `TestCase` of its own, because the suites are separated by class name (`excludeTestsMatching("*IT")` /
 * `includeTestsMatching("*IT")` in `plugin/build.gradle.kts`) and that filter sees the class of the test
 * *object*, not the name of the enclosing suite. Any other carrier gets both directions wrong: an anonymous
 * `object : TestCase` is called `…$Companion$failing$1`, so it escapes the exclusion and fails the developer
 * suite (which deliberately never downloads the corpus) while being silently dropped from the integration
 * suite it is meant to guard; a named nested class is additionally picked up by Gradle's own test detection —
 * that scans class files and ignores Kotlin visibility — and, having no `(String)` constructor, is replaced
 * by JUnit's synthetic `warning` case, which runs as `junit.framework.TestSuite$1` and fails everywhere.
 *
 * @constructor Creates the test case that builds [script], or — when [failureMessage] is set — the case that
 *   reports the broken environment described by it.
 */
class IsExampleScriptBuildIT private constructor(
    private val script: File?,
    private val failureMessage: String?,
) : IsTimedBasePlatformTestCase() {

    /** Creates the case that builds [script]. */
    constructor(script: File) : this(script, null) {
        // JUnit 3 identifies a dynamically created test case by its name; it also feeds the temp directories
        // of the fixture, so it is reduced to path-safe characters.
        name = "testBuild_" + script.name.replace(Regex("[^A-Za-z0-9]"), "_")
    }

    /** The `/O…` target of this build; created in [setUp] and removed in [tearDown]. */
    private lateinit var outputDir: File

    private var previousInstallationPath: String? = null

    /**
     * ISCC is a real, long-running external process. Running it from the EDT would block the dispatch thread
     * for the whole compilation, so the test body runs on a background thread instead.
     */
    override fun runInDispatchThread(): Boolean = false

    override fun setUp() {
        super.setUp()

        // Nothing to prepare for the environment report — there is no script to build.
        if (failureMessage != null) return

        // The corpus is compiled where it was downloaded to (plugin/build/inno-setup-examples) rather than
        // copied into the fixture project: ISCC resolves an example's includes and payload relative to that
        // directory, so a lone copied *.iss would not compile. A platform test may only touch a fixed set of
        // roots, though, and the corpus is not among them — every access ends in VfsRootAccessNotAllowedError.
        //
        // On a developer machine this passed by accident: the allowed roots contain the user home, and a
        // project checked out below it is covered incidentally. On CI the workspace sits on D: while the home
        // is on C:, so the corpus falls outside every root and all example builds failed at once.
        IsBuildExampleCorpus.directory?.let {
            VfsRootAccess.allowRootAccess(testRootDisposable, it.absolutePath)
        }

        val settings = IsSettingsService.getInstance().state
        previousInstallationPath = settings.installationPath
        settings.installationPath = IsBuildExampleCorpus.installationDir?.absolutePath
        outputDir = FileUtil.createTempDirectory("inno-build-${script!!.nameWithoutExtension}", null, true)
    }

    override fun tearDown() {
        try {
            if (failureMessage == null) {
                IsSettingsService.getInstance().state.installationPath = previousInstallationPath
                outputDir.deleteRecursively()
            }
        } finally {
            super.tearDown()
        }
    }

    /**
     * JUnit 3 resolves the test body reflectively from the test name; the cases of this suite are created
     * programmatically and have no such method, so the supplied [testRunnable] — which would perform that
     * lookup — is replaced by a direct call of the single test body.
     *
     * `super` is [IsTimedBasePlatformTestCase], so the per-method timeout stays in place.
     */
    @Throws(Throwable::class)
    override fun runTestRunnable(testRunnable: ThrowableRunnable<Throwable>) {
        super.runTestRunnable { failureMessage?.let { fail(it) } ?: buildExample() }
    }

    /**
     * Use case: a user opens one of the official example scripts and builds it.
     *
     * The script is compiled exactly as the IDE would compile it — through
     * [IsCompilerService.compileScriptForRun] with an explicit output directory and a forced rebuild — and
     * must succeed: the examples are correct by definition, so a compiler error means the plugin handed ISCC
     * something wrong. The produced installer is asserted as well, because ISCC also reports success when it
     * was told to write nothing.
     *
     * The compile is guarded by [CompilerWatchdog] so a script that waits for input can never stall the run.
     */
    private fun buildExample() {
        // Only reachable for a case created through the public constructor, which always carries a script;
        // the environment report never gets here (see runTestRunnable).
        val script = script!!
        val virtualFile = LocalFileSystem.getInstance().refreshAndFindFileByIoFile(script)
        assertNotNull("The example ${script.name} is not visible in the virtual file system", virtualFile)

        val watchdog = CompilerWatchdog()
        val result = try {
            project.service<IsCompilerService>().compileScriptForRun(
                virtualFile as VirtualFile,
                IsScriptRunnerLogic.outputArg(outputDir.absolutePath),
                hasArtifact = false,
                force = true,
            )
        } finally {
            watchdog.cancel()
        }

        assertFalse(
            "Compiling ${script.name} exceeded the ${BUILD_TIMEOUT_SECONDS}s budget and ISCC was killed. " +
                    "A script that blocks the compiler — typically one that is interactive at compile time, " +
                    "like a message box raised from '#expr' — must be listed in " +
                    "src/test/resources/examples/build-skip.yaml with a reason.",
            watchdog.firedOnTimeout,
        )
        assertEquals(
            "Building the official example ${script.name} failed — see the compiler output above",
            TaskRunnerResults.SUCCESS,
            result,
        )
        assertNotNull(
            "Building ${script.name} reported success but produced no installer in $outputDir",
            IsScriptRunnerLogic.findSetupExe(outputDir),
        )
    }

    /**
     * Kills the running `ISCC.exe` — and with `/T` the whole process tree it spawned — once
     * [BUILD_TIMEOUT_SECONDS] have elapsed, then records that it struck in [firedOnTimeout].
     *
     * Without this, a script that is interactive at compile time (ISPP can start arbitrary programs through
     * `#expr`, and one of the official examples raises a message box that way) leaves ISCC waiting for a
     * click forever. On a developer machine that is an annoying dialog; on a CI agent, where nobody can ever
     * answer it, it stalls the pipeline until the job's own timeout kills it — long after the cause stopped
     * being visible. Killing the compiler turns that into an ordinary, quickly reported test failure that
     * names the offending script.
     *
     * The watchdog starts on construction; [cancel] must be called once the compile returns.
     */
    private class CompilerWatchdog {

        /** Whether the budget was exhausted and the compiler had to be killed. */
        @Volatile
        var firedOnTimeout: Boolean = false
            private set

        private val thread = Thread({
            try {
                Thread.sleep(BUILD_TIMEOUT_SECONDS * 1000)
            } catch (_: InterruptedException) {
                return@Thread // cancelled because the compile finished in time
            }
            firedOnTimeout = true
            killCompiler()
        }, "is-example-build-watchdog").apply { isDaemon = true; start() }

        /** Stops the watchdog; a no-op when it has already struck. */
        fun cancel() = thread.interrupt()

        /**
         * `/T` also terminates the children ISCC started, which is what actually holds the compile up — the
         * blocking dialog belongs to a child process, not to ISCC itself. Failures are ignored on purpose:
         * the process may well have exited in the meantime, and the timeout is reported by the test anyway.
         */
        private fun killCompiler() {
            runCatching {
                ProcessBuilder("taskkill", "/F", "/T", "/IM", "ISCC.exe")
                    .redirectErrorStream(true)
                    .start()
                    .waitFor()
            }
        }
    }

    companion object {

        /**
         * Time budget for compiling a single example. Generous — a large example legitimately takes a few
         * seconds — but far below any CI job timeout. Overridable with
         * `-Dis.test.example.build.timeout.seconds`.
         */
        private val BUILD_TIMEOUT_SECONDS: Long =
            System.getProperty("is.test.example.build.timeout.seconds")?.toLongOrNull() ?: 180L

        /**
         * Builds the suite: one [IsExampleScriptBuildIT] per example of the corpus.
         *
         * A missing corpus or a missing `ISCC.exe` is not skipped but reported as a failing test — a build
         * suite that silently compiles nothing would be indistinguishable from a passing one.
         */
        @JvmStatic
        fun suite(): TestSuite {
            val suite = TestSuite(IsExampleScriptBuildIT::class.java.name)

            val directory = IsBuildExampleCorpus.directory
            if (directory == null) {
                suite.addTest(
                    failing(
                        "testExampleCorpusAvailable",
                        "The Inno Setup example corpus is not available. Run this suite through Gradle " +
                                "(./gradlew :plugin:integrationTest), which downloads it.",
                    )
                )
                return suite
            }
            if (IsBuildExampleCorpus.isccExecutable == null) {
                suite.addTest(
                    failing(
                        "testInnoSetupInstalled",
                        "No ISCC.exe found in '${IsBuildExampleCorpus.installationDir}'. The build tests " +
                                "compile with a real Inno Setup — install it or point the build at it with " +
                                "-PinnoSetupHome=<dir> / INNO_SETUP_HOME.",
                    )
                )
                return suite
            }

            IsBuildExampleCorpus.scripts
                .filterNot { it.name in IsBuildExampleCorpus.skipped }
                .forEach { suite.addTest(IsExampleScriptBuildIT(it)) }
            return suite
        }

        /**
         * A test case that always fails with [message] — used to report a broken environment. It is an
         * instance of the suite's own class on purpose; see the class documentation.
         */
        private fun failing(testName: String, message: String): TestCase =
            IsExampleScriptBuildIT(null, message).apply { name = testName }
    }
}
