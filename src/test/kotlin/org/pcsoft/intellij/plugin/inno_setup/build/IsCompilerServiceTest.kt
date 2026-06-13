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

package org.pcsoft.intellij.plugin.inno_setup.build

import com.intellij.build.events.MessageEvent
import com.intellij.build.events.impl.FileMessageEventImpl
import com.intellij.build.events.impl.MessageEventImpl
import com.intellij.build.events.impl.OutputBuildEventImpl
import com.intellij.execution.process.ProcessOutputType
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.service
import com.intellij.openapi.fileEditor.FileDocumentManager
import com.intellij.task.TaskRunnerResults
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.settings.IsSettingsService
import java.io.File

/**
 * Tests [IsCompilerService] behaviour that does not require a real ISCC process: command-line
 * assembly, build-event construction, document saving, and graceful failure when unconfigured.
 */
class IsCompilerServiceTest : BasePlatformTestCase() {

    private val group = "Inno Setup"

    private fun problem(warning: Boolean = false, withPos: Boolean = true) =
        IsBuildOutputParser.Line.Problem(
            message = "Unknown directive",
            detail = "Error on line 5 in a.iss: Unknown directive",
            file = if (withPos) "a.iss" else null,
            line = if (withPos) 4 else null,
            column = null,
            warning = warning
        )

    // ── command line ─────────────────────────────────────────────────────────────

    fun testCommandLineWithoutOutputArg() {
        val cmd = IsCompilerService.buildCommandLine(File("ISCC.exe"), "C:\\proj\\setup.iss", "C:\\proj", null)
        assertEquals(listOf("C:\\proj\\setup.iss"), cmd.parametersList.parameters)
        assertEquals("C:\\proj", cmd.workDirectory?.path)
    }

    fun testCommandLineWithOutputArgComesFirst() {
        val cmd = IsCompilerService.buildCommandLine(File("ISCC.exe"), "setup.iss", null, "/O-")
        assertEquals(listOf("/O-", "setup.iss"), cmd.parametersList.parameters)
    }

    // ── problemEvents ────────────────────────────────────────────────────────────

    fun testProblemEventsWithFileEmitsNavigableMessageAndOutput() {
        val p = problem()
        val events = IsCompilerService.problemEvents("id", group, p, File(p.file!!))
        assertEquals(2, events.size)

        val output = events[0] as OutputBuildEventImpl
        assertEquals(p.detail + "\n", output.message)
        assertEquals("errors go to stderr", ProcessOutputType.STDERR, output.outputType)

        val msg = events[1] as FileMessageEventImpl
        assertEquals("tree node shows the error text", p.message, msg.message)
        assertEquals("console shows the full raw line", p.detail, msg.description)
        assertEquals(MessageEvent.Kind.ERROR, msg.kind)
    }

    fun testProblemEventsWithoutFileEmitsPositionlessMessage() {
        val p = problem(withPos = false)
        val events = IsCompilerService.problemEvents("id", group, p, null)
        assertTrue(events[0] is OutputBuildEventImpl)
        val msg = events[1] as MessageEventImpl
        assertEquals(p.message, msg.message)
        assertEquals(p.detail, msg.description)
    }

    fun testProblemEventsWarningUsesWarningKindAndStdout() {
        val p = problem(warning = true)
        val events = IsCompilerService.problemEvents("id", group, p, File(p.file!!))
        assertEquals(ProcessOutputType.STDOUT, (events[0] as OutputBuildEventImpl).outputType)
        assertEquals(MessageEvent.Kind.WARNING, (events[1] as FileMessageEventImpl).kind)
    }

    // ── document saving & graceful failure ───────────────────────────────────────

    fun testNoInstallationFailsGracefully() {
        val state = IsSettingsService.getInstance().state
        val previous = state.installationPath
        try {
            state.installationPath = null
            myFixture.addFileToProject("setup.iss", "[Setup]\nAppName=x\n")
            assertNull(project.service<IsCompilerService>().isccExecutable())
            assertEquals(TaskRunnerResults.FAILURE, project.service<IsCompilerService>().compileProject())
        } finally {
            state.installationPath = previous
        }
    }

    fun testCompileSavesUnsavedDocuments() {
        val state = IsSettingsService.getInstance().state
        val previous = state.installationPath
        try {
            state.installationPath = null // avoid launching a real ISCC; saving happens beforehand
            val file = myFixture.addFileToProject("setup.iss", "[Setup]\nAppName=x\n").virtualFile
            val doc = FileDocumentManager.getInstance().getDocument(file)!!
            WriteCommandAction.runWriteCommandAction(project) { doc.setText("[Setup]\nAppName=changed\n") }
            assertTrue("precondition: document is unsaved", doc in FileDocumentManager.getInstance().unsavedDocuments)

            project.service<IsCompilerService>().compileProject()

            assertFalse("build must flush documents", doc in FileDocumentManager.getInstance().unsavedDocuments)
        } finally {
            state.installationPath = previous
        }
    }
}
