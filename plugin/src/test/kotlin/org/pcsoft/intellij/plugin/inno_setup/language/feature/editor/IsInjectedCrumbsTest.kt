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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor

import com.intellij.codeInsight.breadcrumbs.FileBreadcrumbsCollector
import com.intellij.injected.editor.VirtualFileWindow
import com.intellij.psi.PsiDocumentManager
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * With the caret on a `#…` line the platform asks the breadcrumbs collector with the *injected* ISPP file.
 * Its PSI is ISPP, so the platform's own collector finds nothing and the breadcrumbs bar falls back to the
 * injected file's name — the `<Injected ISPP file>` crumb. [IsFileBreadcrumbsCollector] therefore resolves
 * everything on the host, so a preprocessor line behaves like any other line of the script.
 */
class IsInjectedCrumbsTest : IsTimedBasePlatformTestCase() {

    private val script = "[Files]\n" +
            "#if Debug\n" +
            "Source: \"debug.exe\"; DestDir: \"{app}\"\n" +
            "#endif\n"

    /** Configures the script with the caret inside the `#if` line, i.e. inside the ISPP injection. */
    private fun configureInsideInjection() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, script.replace("#if Debug", "#if De<caret>bug"))
    }

    fun testCaretOnPreprocessorLineUsesInjectedFile() {
        configureInsideInjection()

        assertTrue(
            "Precondition: the fixture file is the injected ISPP fragment",
            myFixture.file.virtualFile is VirtualFileWindow
        )
        assertSame(
            "Our collector must handle the injected file as well",
            IsFileBreadcrumbsCollector::class.java,
            FileBreadcrumbsCollector.findBreadcrumbsCollector(project, myFixture.file.virtualFile).javaClass
        )
    }

    fun testNoInjectedFileCrumbOnPreprocessorLine() {
        configureInsideInjection()
        val texts = crumbTexts()

        assertTrue("The section must be shown: $texts", texts.any { it == "Files" })
        assertTrue("No injected-fragment crumb: $texts", texts.none { it.contains("Injected") })
    }

    private fun crumbTexts(): List<String> = collector().computeCrumbs(
        myFixture.file.virtualFile, injectedDocument(), myFixture.caretOffset, true
    ).map { it.text }

    private fun collector() = IsFileBreadcrumbsCollector(project)

    private fun injectedDocument() = PsiDocumentManager.getInstance(project).getDocument(myFixture.file)!!
}
