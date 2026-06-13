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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import com.intellij.psi.SmartPointerManager
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.findSection
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionDirectiveEntry

/**
 * Sets `UsePreviousLanguage` in `[Setup]` to `no`, the value Inno Setup requires when the script
 * declares only a single language. Replaces an existing value, inserts the directive into an existing
 * `[Setup]`, or creates a `[Setup]` section if none is present.
 */
class SetUsePreviousLanguageNoQuickFix(file: IsScriptFile) : IntentionAction {

    private val filePointer =
        SmartPointerManager.getInstance(file.project).createSmartPsiElementPointer(file)

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getText(): String = "Set UsePreviousLanguage to 'no'"

    /**
     * Returns the user-visible action text used by IntelliJ.
     */
    override fun getFamilyName(): String = getText()

    /**
     * Checks whether this action can run in the current editor context.
     */
    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean =
        filePointer.element != null

    /**
     * Executes this action against the current PSI file.
     */
    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        val scriptFile = filePointer.element ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(scriptFile.containingFile) ?: return

        val setup = scriptFile.findSection("Setup")
        if (setup == null) {
            document.insertString(0, "[Setup]\nUsePreviousLanguage=no\n\n")
            return
        }

        val entry = setup.directiveEntryList
            .firstOrNull { it.keyText().equals("UsePreviousLanguage", ignoreCase = true) }
        when (val value = entry?.paramValue) {
            null -> when {
                // `UsePreviousLanguage=` present but valueless — append the value after the '='.
                entry != null -> document.insertString(entry.textRange.endOffset, "no")
                // Directive absent — add it to the end of [Setup].
                else -> document.insertString(setup.textRange.endOffset, "UsePreviousLanguage=no\n")
            }

            else -> document.replaceString(value.textRange.startOffset, value.textRange.endOffset, "no")
        }
    }

    /**
     * Indicates whether this action must run inside an IntelliJ write action.
     */
    override fun startInWriteAction(): Boolean = true
}
