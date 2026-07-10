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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.formatter

import com.intellij.openapi.command.WriteCommandAction
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.codeStyle.CodeStyleManager
import com.intellij.util.LocalTimeCounter
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.test.IsTimedBasePlatformTestCase

/**
 * Reformat test for the **Code Style live preview** scenario. The preview panel reformats a *non-physical*
 * file (`eventSystemEnabled = false`), for which `PsiDocumentManager.getDocument` returns `null`; the
 * formatter must still apply its edits (via the view provider's document) so the preview reflects option
 * changes.
 *
 * It also guards the regression where an edit that changes content without changing the overall length
 * (bracket tightening `-2` cancelling `=` spacing `+2`) was not committed, leaving the PSI stale.
 */
class IsSectionFormatterPreviewTest : IsTimedBasePlatformTestCase() {

    fun testNonPhysicalPreviewFileIsReformatted() {
        // Same construction the Code Style preview panel uses (see CustomizableLanguageCodeStylePanel).
        val file = PsiFileFactory.getInstance(project).createFileFromText(
            "a.iss", IsScriptFileType.INSTANCE, "[ Setup ]\nAppName=A\n", LocalTimeCounter.currentTime(), false,
        )
        assertFalse("Preview file must be non-physical to reproduce the scenario", file.isPhysical)

        WriteCommandAction.runWriteCommandAction(project) {
            CodeStyleManager.getInstance(project).reformat(file)
        }

        assertEquals("[Setup]\nAppName = A\n", file.text)
    }
}
