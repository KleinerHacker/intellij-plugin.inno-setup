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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.quickfix

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.findSection
import org.pcsoft.intellij.plugin.inno_setup.types.IsiSpec

class AddMissingSectionsQuickFix(
    private val missingSectionNames: List<String>,
    private val spec: IsiSpec
) : IntentionAction {

    override fun getText(): String {
        val properNames = spec.sections
            .filter { s -> missingSectionNames.any { it.equals(s.name, ignoreCase = true) } }
            .map { it.name }
        return "Add missing section(s): " + properNames.joinToString(", ") { "[$it]" }
    }

    override fun getFamilyName(): String = "Add missing required sections"

    override fun isAvailable(project: Project, editor: Editor?, file: PsiFile): Boolean = file is IssFile

    override fun invoke(project: Project, editor: Editor?, file: PsiFile) {
        val issFile = file as? IssFile ?: return
        val document = PsiDocumentManager.getInstance(project).getDocument(issFile) ?: return
        PsiDocumentManager.getInstance(project).commitDocument(document)

        val codeSection = issFile.findSection("Code")
        val insertOffset = codeSection?.textRange?.startOffset ?: document.textLength

        val text = buildString {
            for (sectionSpec in spec.sections.filter { s ->
                missingSectionNames.any { it.equals(s.name, ignoreCase = true) }
            }) {
                val requiredAttrs = sectionSpec.attributes.filter { it.required }
                append("\n[${sectionSpec.name}]\n")
                when (sectionSpec.type) {
                    "directive" -> requiredAttrs.forEach { attr ->
                        append("${attr.name}=${IssDefaultValueGenerator.defaultFor(attr)}\n")
                    }
                    "parameter" -> if (requiredAttrs.isNotEmpty()) {
                        append(requiredAttrs.joinToString("; ") { "${it.name}: ${IssDefaultValueGenerator.defaultFor(it)}" })
                        append("\n")
                    }
                }
            }
        }

        document.insertString(insertOffset, text)
    }

    override fun startInWriteAction(): Boolean = true
}
