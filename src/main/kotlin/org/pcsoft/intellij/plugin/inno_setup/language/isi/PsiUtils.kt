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

package org.pcsoft.intellij.plugin.inno_setup.language.isi

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.*
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.IsiSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsiSectionSpec

// ── IssFile (Sektionen) ────────────────────────────────────────────────────────

val IssFile.sections: List<IsiSection>
    get() = PsiTreeUtil.getChildrenOfTypeAsList(this, IsiSection::class.java)

fun IssFile.findSection(name: String): IsiSection? =
    sections.firstOrNull { it.nameText.equals(name, ignoreCase = true) }

fun IssFile.findSections(name: String): List<IsiSection> =
    sections.filter { it.nameText.equals(name, ignoreCase = true) }

// The section a caret offset logically belongs to. Unlike containingSection(),
// this still works when trailing/incomplete tokens (e.g. after a dangling ';')
// fell outside the section's PSI range: it returns the last section that starts
// at or before the offset.
fun IssFile.sectionAtOffset(offset: Int): IsiSection? =
    sections.lastOrNull { it.textRange.startOffset <= offset }

// ── IsiSection ───────────────────────────────────────────────────────────────

val IsiSection.nameText: String
    get() = sectionHeader.sectionName?.text.orEmpty()

fun IsiSection.allParamPairs(): List<IsiParamPair> =
    parameterEntryList.flatMap { it.paramPairList }

fun IsiSection.findParamPairs(key: String): List<IsiParamPair> =
    allParamPairs().filter { it.keyText().equals(key, ignoreCase = true) }

val IsiSection.nameDeclarations: List<IsiParamPair>
    get() = findParamPairs("Name")

fun IsiSection.specSection(spec: IsiSpec): IsiSectionSpec? =
    spec.sections.firstOrNull { it.name.equals(nameText, ignoreCase = true) }

val IsiSection.specSection: IsiSectionSpec?
    get() = service<IssSpecService>().spec.sections.firstOrNull { it.name.equals(nameText, ignoreCase = true) }

val IsiSection.isParameterSection: Boolean
    get() = specSection?.type == "parameter"

// The parameter entry sharing the caret's line. Used when the caret sits after a
// dangling ';' whose incomplete pair fell outside the entry/section PSI, so the
// keys already present on the line can still be detected.
fun IsiSection.parameterEntryOnLineOf(offset: Int, document: Document): IsiParameterEntry? {
    val line = document.getLineNumber(offset)
    return parameterEntryList.lastOrNull {
        it.textRange.startOffset <= offset &&
                document.getLineNumber(it.textRange.startOffset) == line
    }
}

// ── IsiParameterEntry ─────────────────────────────────────────────────────────

val IsiParameterEntry.displayName: String
    get() {
        val pairs = paramPairList
        if (pairs.isEmpty())
            return "…"

        val root = pairs.firstOrNull { it.keyText().equals("root", ignoreCase = true) }
        if (root != null) {
            val subkey = pairs.firstOrNull { it.keyText().equals("subkey", ignoreCase = true) }
            val rootText = root.valueUnquoted.trim()

            return if (subkey != null) "$rootText\\${subkey.valueUnquoted.trim()}" else rootText
        }

        for (key in listOf("name", "source", "filename")) {
            val value = pairs.firstOrNull { it.keyText().equals(key, ignoreCase = true) }
                ?.valueUnquoted?.trim() ?: continue
            if (value.isNotEmpty())
                return value.stripIssPrefix
        }

        return pairs.first().valueUnquoted.trim().stripIssPrefix.ifEmpty { "…" }
    }

private val String.stripIssPrefix: String
    get() = if (startsWith("{")) {
        val end = indexOf('}')
        if (end > 0 && getOrNull(end + 1) == '\\') substring(end + 2) else this
    } else this

// ── IsiParamPair ──────────────────────────────────────────────────────────────

val IsiParamPair.valueText: String
    get() = paramValue?.text?.trim().orEmpty()

val IsiParamPair.valueUnquoted: String
    get() = valueText.removeSurrounding("\"")

// ── IsiDirectiveEntry ─────────────────────────────────────────────────────────
// keyText() is provided as a member by IsiDirectiveEntryEx (the directiveEntry mixin).

val IsiDirectiveEntry.valueText: String
    get() = paramValue?.text?.trim().orEmpty()

// ── IsiParamValue ─────────────────────────────────────────────────────────────

val IsiParamValue.identifiers: List<PsiElement>
    get() = node.getChildren(TokenSet.create(IsiTypes.IDENTIFIER)).map { it.psi }

val IsiParamValue.singleText: String
    get() = text.trim().removeSurrounding("\"")

// ── PsiElement (Sections) ─────────────────────────────────────────────────────

val PsiElement.containingSection: IsiSection?
    get() = PsiTreeUtil.getParentOfType(this, IsiSection::class.java)

val PsiElement.containingParamPair: IsiParamPair?
    get() = PsiTreeUtil.getParentOfType(this, IsiParamPair::class.java)

val PsiElement.containingParameterEntry: IsiParameterEntry?
    get() = PsiTreeUtil.getParentOfType(this, IsiParameterEntry::class.java)

val PsiElement.containingDirectiveEntry: IsiDirectiveEntry?
    get() = PsiTreeUtil.getParentOfType(this, IsiDirectiveEntry::class.java)

val PsiElement.isInCodeSection: Boolean
    get() = containingSection?.nameText?.equals("Code", ignoreCase = true) == true
