package org.pcsoft.intellij.plugin.inno_setup.language.isi

import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Document
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.*
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.InnoSetupSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IssSectionSpec

// ── IsiSection ───────────────────────────────────────────────────────────────

fun IsiSection.nameText(): String = sectionHeader.sectionName?.text.orEmpty()

fun IsiSection.allParamPairs(): List<IsiParamPair> =
    parameterEntryList.flatMap { it.paramPairList }

fun IsiSection.findParamPairs(key: String): List<IsiParamPair> =
    allParamPairs().filter { it.keyText().equals(key, ignoreCase = true) }

fun IsiSection.findParamPair(key: String): IsiParamPair? =
    findParamPairs(key).firstOrNull()

fun IsiSection.nameDeclarations(): List<IsiParamPair> = findParamPairs("Name")

fun IsiSection.firstParamPair(): IsiParamPair? = allParamPairs().firstOrNull()

fun IsiSection.specSection(spec: InnoSetupSpec): IssSectionSpec? =
    spec.sections.firstOrNull { it.name.equals(nameText(), ignoreCase = true) }

fun IsiSection.specSection(): IssSectionSpec? =
    service<IssSpecService>().spec.sections.firstOrNull { it.name.equals(nameText(), ignoreCase = true) }

fun IsiSection.isParameterSection(): Boolean = specSection()?.type == "parameter"

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

fun IsiParameterEntry.firstParamPair(): IsiParamPair? = paramPairList.firstOrNull()

fun IsiParameterEntry.displayName(): String {
    val pairs = paramPairList
    if (pairs.isEmpty()) return "…"

    val root = pairs.firstOrNull { it.keyText().equals("root", ignoreCase = true) }
    if (root != null) {
        val subkey = pairs.firstOrNull { it.keyText().equals("subkey", ignoreCase = true) }
        val rootText = root.valueUnquoted().trim()
        return if (subkey != null) "$rootText\\${subkey.valueUnquoted().trim()}" else rootText
    }

    for (key in listOf("name", "source", "filename")) {
        val value = pairs.firstOrNull { it.keyText().equals(key, ignoreCase = true) }
            ?.valueUnquoted()?.trim() ?: continue
        if (value.isNotEmpty()) return value.stripIssPrefix()
    }

    return pairs.first().valueUnquoted().trim().stripIssPrefix().ifEmpty { "…" }
}

private fun String.stripIssPrefix(): String =
    if (startsWith("{")) {
        val end = indexOf('}')
        if (end > 0 && getOrNull(end + 1) == '\\') substring(end + 2) else this
    } else this

// ── IsiParamPair ──────────────────────────────────────────────────────────────

fun IsiParamPair.valueText(): String = paramValue?.text?.trim().orEmpty()

fun IsiParamPair.valueUnquoted(): String = valueText().removeSurrounding("\"")

// ── IsiDirectiveEntry ─────────────────────────────────────────────────────────

fun IsiDirectiveEntry.keyText(): String = directiveKey.text.trim()

fun IsiDirectiveEntry.valueText(): String = paramValue?.text?.trim().orEmpty()

// ── IsiParamValue ─────────────────────────────────────────────────────────────

fun IsiParamValue.identifiers(): List<PsiElement> =
    node.getChildren(TokenSet.create(IsiTypes.IDENTIFIER)).map { it.psi }

fun IsiParamValue.singleText(): String = text.trim().removeSurrounding("\"")

fun IsiParamValue.valueTokens(): List<String> =
    text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }

// ── PsiElement (Sections) ─────────────────────────────────────────────────────

fun PsiElement.containingSection(): IsiSection? =
    PsiTreeUtil.getParentOfType(this, IsiSection::class.java)

fun PsiElement.containingParamPair(): IsiParamPair? =
    PsiTreeUtil.getParentOfType(this, IsiParamPair::class.java)

fun PsiElement.containingParameterEntry(): IsiParameterEntry? =
    PsiTreeUtil.getParentOfType(this, IsiParameterEntry::class.java)

fun PsiElement.containingDirectiveEntry(): IsiDirectiveEntry? =
    PsiTreeUtil.getParentOfType(this, IsiDirectiveEntry::class.java)

fun PsiElement.isInCodeSection(): Boolean =
    containingSection()?.nameText()?.equals("Code", ignoreCase = true) == true
