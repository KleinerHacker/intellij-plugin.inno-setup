package org.pcsoft.intellij.plugin.inno_setup.language

import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.*
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.InnoSetupSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IssSectionSpec

// ── IssFile ──────────────────────────────────────────────────────────────────

fun IssFile.sections(): List<IssSection> =
    PsiTreeUtil.getChildrenOfTypeAsList(this, IssSection::class.java)

fun IssFile.findSection(name: String): IssSection? =
    sections().firstOrNull { it.nameText().equals(name, ignoreCase = true) }

fun IssFile.findSections(name: String): List<IssSection> =
    sections().filter { it.nameText().equals(name, ignoreCase = true) }

fun IssFile.firstSection(): IssSection? = sections().firstOrNull()

fun IssFile.definedConstants(): List<Pair<String, String?>> {
    val fileText = text
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IssPreprocessorDirective::class.java)
        .filter { d -> d.identifier?.text?.equals("define", ignoreCase = true) == true }
        .mapNotNull { directive ->
            val ex = directive as? IssPreprocessorDirectiveEx ?: return@mapNotNull null
            val name = ex.getDefineName()?.ifEmpty { null } ?: return@mapNotNull null
            // The ISS lexer in YYINITIAL only produces IDENTIFIER tokens, not NUMBER or QUOTE.
            // So #define values that contain numbers or quoted strings are not captured by paramValue
            // tokens. We read the value from the raw source line instead.
            val rawVal = extractDefineValueFromLine(fileText, directive.textOffset, ex.getDefineTypeName(), name)
            name to rawVal
        }
}

// ── IssSection ───────────────────────────────────────────────────────────────

fun IssSection.nameText(): String = sectionHeader.sectionName?.text.orEmpty()

fun IssSection.allParamPairs(): List<IssParamPair> =
    parameterEntryList.flatMap { it.paramPairList }

fun IssSection.findParamPairs(key: String): List<IssParamPair> =
    allParamPairs().filter { it.keyText().equals(key, ignoreCase = true) }

fun IssSection.findParamPair(key: String): IssParamPair? =
    findParamPairs(key).firstOrNull()

fun IssSection.nameDeclarations(): List<IssParamPair> = findParamPairs("Name")

fun IssSection.firstParamPair(): IssParamPair? = allParamPairs().firstOrNull()

fun IssSection.specSection(spec: InnoSetupSpec): IssSectionSpec? =
    spec.sections.firstOrNull { it.name.equals(nameText(), ignoreCase = true) }

fun IssSection.specSection(): IssSectionSpec? =
    service<IssSpecService>().spec.sections.firstOrNull { it.name.equals(nameText(), ignoreCase = true) }

fun IssSection.isParameterSection(): Boolean = specSection()?.type == "parameter"

// ── IssParameterEntry ─────────────────────────────────────────────────────────

fun IssParameterEntry.firstParamPair(): IssParamPair? = paramPairList.firstOrNull()

fun IssParameterEntry.displayName(): String {
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

// ── IssParamPair ──────────────────────────────────────────────────────────────

fun IssParamPair.valueText(): String = paramValue?.text?.trim().orEmpty()

fun IssParamPair.valueUnquoted(): String = valueText().removeSurrounding("\"")

// ── IssDirectiveEntry ─────────────────────────────────────────────────────────

fun IssDirectiveEntry.keyText(): String = directiveKey.text.trim()

fun IssDirectiveEntry.valueText(): String = paramValue?.text?.trim().orEmpty()

// ── IssParamValue ─────────────────────────────────────────────────────────────

fun IssParamValue.identifiers(): List<PsiElement> =
    node.getChildren(TokenSet.create(IssTypes.IDENTIFIER)).map { it.psi }

fun IssParamValue.singleText(): String = text.trim().removeSurrounding("\"")

fun IssParamValue.valueTokens(): List<String> =
    text.trim().split(Regex("\\s+")).filter { it.isNotEmpty() }

// ── PsiElement (allgemein) ────────────────────────────────────────────────────

fun PsiElement.issFile(): IssFile? = containingFile as? IssFile

fun PsiElement.containingSection(): IssSection? =
    PsiTreeUtil.getParentOfType(this, IssSection::class.java)

fun PsiElement.containingParamPair(): IssParamPair? =
    PsiTreeUtil.getParentOfType(this, IssParamPair::class.java)

fun PsiElement.containingParameterEntry(): IssParameterEntry? =
    PsiTreeUtil.getParentOfType(this, IssParameterEntry::class.java)

fun PsiElement.containingDirectiveEntry(): IssDirectiveEntry? =
    PsiTreeUtil.getParentOfType(this, IssDirectiveEntry::class.java)

fun PsiElement.isInCodeSection(): Boolean =
    containingSection()?.nameText()?.equals("Code", ignoreCase = true) == true

/**
 * Extracts the value part of a `#define [type] name value` directive from the raw line text.
 *
 * The ISS lexer in YYINITIAL only produces IDENTIFIER tokens, so NUMBER and QUOTE tokens are
 * not emitted there — paramValue cannot capture numeric or quoted values directly.
 * Reading from the source line avoids this limitation.
 */
internal fun extractDefineValueFromLine(
    fileText: String,
    directiveOffset: Int,
    typeName: String?,
    name: String,
): String? {
    val lineStart = fileText.lastIndexOf('\n', directiveOffset).let { if (it < 0) 0 else it + 1 }
    val lineEnd   = fileText.indexOf('\n', directiveOffset).let { if (it < 0) fileText.length else it }
    val lineText  = fileText.substring(lineStart, lineEnd).trim()

    // Strip "#define"
    val afterDefine = lineText.removePrefix("#").trimStart().let { rest ->
        if (rest.startsWith("define", ignoreCase = true)) rest.substring("define".length).trimStart()
        else return null
    }
    // Skip optional type qualifier
    val afterType = if (typeName != null) afterDefine.removePrefix(typeName).trimStart() else afterDefine
    // Skip name
    val afterName = afterType.removePrefix(name).trimStart()

    return when {
        afterName.startsWith('(') -> null   // function-like macro body
        afterName.isEmpty()       -> null
        else -> afterName.removeSurrounding("\"").ifEmpty { null }
    }
}
