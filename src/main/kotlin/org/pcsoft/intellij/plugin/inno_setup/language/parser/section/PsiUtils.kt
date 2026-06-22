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
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.*
import org.pcsoft.intellij.plugin.inno_setup.services.IsSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionDefSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionReferenceTypeSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionType

// ── IsScriptFile (Sektionen) ────────────────────────────────────────────────────────

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsScriptFile.sections: List<IsSectionBlock>
    get() = PsiTreeUtil.getChildrenOfTypeAsList(this, IsSectionBlock::class.java)

/**
 * Returns or performs the public behavior represented by this member.
 */
fun IsScriptFile.findSection(name: String): IsSectionBlock? =
    sections.firstOrNull { it.nameText.equals(name, ignoreCase = true) }

/**
 * Returns or performs the public behavior represented by this member.
 */
fun IsScriptFile.findSections(name: String): List<IsSectionBlock> =
    sections.filter { it.nameText.equals(name, ignoreCase = true) }

// The section a caret offset logically belongs to. Unlike containingSection(),
// this still works when trailing/incomplete tokens (e.g. after a dangling ';')
// fell outside the section's PSI range: it returns the last section that starts
// at or before the offset.
/**
 * Returns or performs the public behavior represented by this member.
 */
fun IsScriptFile.sectionAtOffset(offset: Int): IsSectionBlock? =
    sections.lastOrNull { it.textRange.startOffset <= offset }

// ── IsSectionBlock ───────────────────────────────────────────────────────────────

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsSectionBlock.nameText: String
    get() = header.title?.text.orEmpty()

/**
 * Returns or performs the public behavior represented by this member.
 */
fun IsSectionBlock.allParamPairs(): List<IsSectionParamPair> =
    parameterEntryList.flatMap { it.paramPairList }

/**
 * Returns or performs the public behavior represented by this member.
 */
fun IsSectionBlock.findParamPairs(key: String): List<IsSectionParamPair> =
    allParamPairs().filter { it.keyText().equals(key, ignoreCase = true) }

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsSectionBlock.nameDeclarations: List<IsSectionParamPair>
    get() = findParamPairs("Name")

/**
 * Returns or performs the public behavior represented by this member.
 */
fun IsSectionBlock.specSection(spec: IsSectionSpec): IsSectionDefSpec? =
    spec.sections.firstOrNull { it.name.equals(nameText, ignoreCase = true) }

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsSectionBlock.specSection: IsSectionDefSpec?
    get() = service<IsSpecService>().spec.sections.firstOrNull { it.name.equals(nameText, ignoreCase = true) }

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsSectionBlock.isParameterSection: Boolean
    get() = specSection?.type == IsSectionType.PARAMETER

// The parameter entry sharing the caret's line. Used when the caret sits after a
// dangling ';' whose incomplete pair fell outside the entry/section PSI, so the
// keys already present on the line can still be detected.
/**
 * Returns or performs the public behavior represented by this member.
 */
fun IsSectionBlock.parameterEntryOnLineOf(offset: Int, document: Document): IsSectionParameterEntry? {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    val line = document.getLineNumber(offset)
    return parameterEntryList.lastOrNull {
        it.textRange.startOffset <= offset &&
                document.getLineNumber(it.textRange.startOffset) == line
    }
}

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsSectionBlock.prevSection: IsSectionBlock?
    get() = PsiTreeUtil.getPrevSiblingOfType(this, IsSectionBlock::class.java)

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsSectionBlock.nextSection: IsSectionBlock?
    get() = PsiTreeUtil.getNextSiblingOfType(this, IsSectionBlock::class.java)

// ── IsSectionParameterEntry ─────────────────────────────────────────────────────────

/**
 * Returns or performs the public behavior represented by this member.
 */
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

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsSectionParamPair.valueText: String
    get() = paramValue?.text?.trim().orEmpty()

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsSectionParamPair.valueUnquoted: String
    get() = valueText.removeSurrounding("\"")

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsSectionParamPair.nextParam: IsSectionParamPair?
    get() = PsiTreeUtil.getNextSiblingOfType(this, IsSectionParamPair::class.java)

/**
 * The section that this pair's value references, when its key maps to a `reference`-typed attribute
 * in the section spec (e.g. `Tasks` in `\[Icons]` → `Tasks`), or `null` otherwise. Derived from the
 * YAML spec (`type: { kind: reference, section: … }`), the single source of truth — so a key only
 * counts as a reference in the sections where the spec actually declares it as one.
 */
val IsSectionParamPair.referenceTargetSection: String?
    get() {
        val attr = containingSection?.specSection
            ?.attributes?.firstOrNull { it.name.equals(keyText(), ignoreCase = true) }
        return (attr?.type as? IsSectionReferenceTypeSpec)?.section
    }

// ── IsSectionDirectiveEntry ─────────────────────────────────────────────────────────
// keyText() is provided as a member by IsSectionDirectiveEntryEx (the directiveEntry mixin).

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsSectionDirectiveEntry.valueText: String
    get() = paramValue?.text?.trim().orEmpty()

// ── IsSectionParamValue ─────────────────────────────────────────────────────────────

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsSectionParamValue.identifiers: List<PsiElement>
    get() = node.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER)).map { it.psi }

/**
 * Returns or performs the public behavior represented by this member.
 */
val IsSectionParamValue.singleText: String
    get() = text.trim().removeSurrounding("\"")

// ── IsSectionConstantBody ───────────────────────────────────────────────────────────

// For a {cm:Name,Arg1,Arg2} constant body, the message name and its range within the body.
// The name is the text between the ':' and the first ',' (surrounding whitespace trimmed); the
// printf-style arguments after the first comma are ignored. Text-based on purpose: the lexer can
// merge "Name,Arg" into a single VALUE_CHAR token (',' is a value char), so the bare IDENTIFIER
// token after the colon is not a reliable anchor. Returns null when this is not a {cm:…} body.
fun IsSectionConstantBody.customMessageNameRange(): Pair<String, TextRange>? {
    val t = text
    if (!t.regionMatches(0, "cm:", 0, 3, ignoreCase = true)) return null
    val afterColon = t.indexOf(':') + 1
    val comma = t.indexOf(',', afterColon)
    val rawEnd = if (comma >= 0) comma else t.length
    var start = afterColon
    while (start < rawEnd && t[start].isWhitespace()) start++
    var end = rawEnd
    while (end > start && t[end - 1].isWhitespace()) end--
    if (start >= end) return null
    return t.substring(start, end) to TextRange(start, end)
}

// ── PsiElement (Sections) ─────────────────────────────────────────────────────

/**
 * Returns or performs the public behavior represented by this member.
 */
val PsiElement.containingSection: IsSectionBlock?
    get() = PsiTreeUtil.getParentOfType(this, IsSectionBlock::class.java)

/**
 * Returns or performs the public behavior represented by this member.
 */
val PsiElement.containingParamPair: IsSectionParamPair?
    get() = PsiTreeUtil.getParentOfType(this, IsSectionParamPair::class.java)

/**
 * Returns or performs the public behavior represented by this member.
 */
val PsiElement.containingParameterEntry: IsSectionParameterEntry?
    get() = PsiTreeUtil.getParentOfType(this, IsSectionParameterEntry::class.java)

/**
 * Returns or performs the public behavior represented by this member.
 */
val PsiElement.containingDirectiveEntry: IsSectionDirectiveEntry?
    get() = PsiTreeUtil.getParentOfType(this, IsSectionDirectiveEntry::class.java)

/**
 * Returns or performs the public behavior represented by this member.
 */
val PsiElement.isInCodeSection: Boolean
    get() = containingSection?.nameText?.equals("Code", ignoreCase = true) == true
