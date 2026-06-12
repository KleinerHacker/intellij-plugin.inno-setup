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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.*
import org.pcsoft.intellij.plugin.inno_setup.services.IsSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionDefSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionSpec

// ── IsScriptFile (Sektionen) ────────────────────────────────────────────────────────

val IsScriptFile.sections: List<IsSectionBlock>
    get() = PsiTreeUtil.getChildrenOfTypeAsList(this, IsSectionBlock::class.java)

fun IsScriptFile.findSection(name: String): IsSectionBlock? =
    sections.firstOrNull { it.nameText.equals(name, ignoreCase = true) }

fun IsScriptFile.findSections(name: String): List<IsSectionBlock> =
    sections.filter { it.nameText.equals(name, ignoreCase = true) }

// The section a caret offset logically belongs to. Unlike containingSection(),
// this still works when trailing/incomplete tokens (e.g. after a dangling ';')
// fell outside the section's PSI range: it returns the last section that starts
// at or before the offset.
fun IsScriptFile.sectionAtOffset(offset: Int): IsSectionBlock? =
    sections.lastOrNull { it.textRange.startOffset <= offset }

// ── IsSectionBlock ───────────────────────────────────────────────────────────────

val IsSectionBlock.nameText: String
    get() = header.title?.text.orEmpty()

fun IsSectionBlock.allParamPairs(): List<IsSectionParamPair> =
    parameterEntryList.flatMap { it.paramPairList }

fun IsSectionBlock.findParamPairs(key: String): List<IsSectionParamPair> =
    allParamPairs().filter { it.keyText().equals(key, ignoreCase = true) }

val IsSectionBlock.nameDeclarations: List<IsSectionParamPair>
    get() = findParamPairs("Name")

fun IsSectionBlock.specSection(spec: IsSectionSpec): IsSectionDefSpec? =
    spec.sections.firstOrNull { it.name.equals(nameText, ignoreCase = true) }

val IsSectionBlock.specSection: IsSectionDefSpec?
    get() = service<IsSpecService>().spec.sections.firstOrNull { it.name.equals(nameText, ignoreCase = true) }

val IsSectionBlock.isParameterSection: Boolean
    get() = specSection?.type == "parameter"

// The parameter entry sharing the caret's line. Used when the caret sits after a
// dangling ';' whose incomplete pair fell outside the entry/section PSI, so the
// keys already present on the line can still be detected.
fun IsSectionBlock.parameterEntryOnLineOf(offset: Int, document: Document): IsSectionParameterEntry? {
    val line = document.getLineNumber(offset)
    return parameterEntryList.lastOrNull {
        it.textRange.startOffset <= offset &&
                document.getLineNumber(it.textRange.startOffset) == line
    }
}

val IsSectionBlock.prevSection: IsSectionBlock?
    get() = PsiTreeUtil.getPrevSiblingOfType(this, IsSectionBlock::class.java)

val IsSectionBlock.nextSection: IsSectionBlock?
    get() = PsiTreeUtil.getNextSiblingOfType(this, IsSectionBlock::class.java)

// ── IsSectionParameterEntry ─────────────────────────────────────────────────────────

val IsSectionParameterEntry.displayName: String
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

// ── IsSectionParamPair ──────────────────────────────────────────────────────────────

val IsSectionParamPair.valueText: String
    get() = paramValue?.text?.trim().orEmpty()

val IsSectionParamPair.valueUnquoted: String
    get() = valueText.removeSurrounding("\"")

val IsSectionParamPair.nextParam: IsSectionParamPair?
    get() = PsiTreeUtil.getNextSiblingOfType(this, IsSectionParamPair::class.java)

// ── IsSectionDirectiveEntry ─────────────────────────────────────────────────────────
// keyText() is provided as a member by IsSectionDirectiveEntryEx (the directiveEntry mixin).

val IsSectionDirectiveEntry.valueText: String
    get() = paramValue?.text?.trim().orEmpty()

// ── IsSectionParamValue ─────────────────────────────────────────────────────────────

val IsSectionParamValue.identifiers: List<PsiElement>
    get() = node.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER)).map { it.psi }

val IsSectionParamValue.singleText: String
    get() = text.trim().removeSurrounding("\"")

// ── PsiElement (Sections) ─────────────────────────────────────────────────────

val PsiElement.containingSection: IsSectionBlock?
    get() = PsiTreeUtil.getParentOfType(this, IsSectionBlock::class.java)

val PsiElement.containingParamPair: IsSectionParamPair?
    get() = PsiTreeUtil.getParentOfType(this, IsSectionParamPair::class.java)

val PsiElement.containingParameterEntry: IsSectionParameterEntry?
    get() = PsiTreeUtil.getParentOfType(this, IsSectionParameterEntry::class.java)

val PsiElement.containingDirectiveEntry: IsSectionDirectiveEntry?
    get() = PsiTreeUtil.getParentOfType(this, IsSectionDirectiveEntry::class.java)

val PsiElement.isInCodeSection: Boolean
    get() = containingSection?.nameText?.equals("Code", ignoreCase = true) == true
