package org.pcsoft.intellij.plugin.inno_setup.language

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.IsppFile
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirective
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiIsppLine
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiSection

// ── IssFile (Sektionen) ────────────────────────────────────────────────────────

fun IssFile.sections(): List<IsiSection> =
    PsiTreeUtil.getChildrenOfTypeAsList(this, IsiSection::class.java)

fun IssFile.findSection(name: String): IsiSection? =
    sections().firstOrNull { it.nameText().equals(name, ignoreCase = true) }

fun IssFile.findSections(name: String): List<IsiSection> =
    sections().filter { it.nameText().equals(name, ignoreCase = true) }

fun IssFile.firstSection(): IsiSection? = sections().firstOrNull()

// ── IssFile (ISPP-Brücke) ──────────────────────────────────────────────────────

fun IssFile.isppDirectives(): List<IsppDirective> {
    val mgr = InjectedLanguageManager.getInstance(project)
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IsiIsppLine::class.java)
        .flatMap { line ->
            val result = mutableListOf<IsppDirective>()
            mgr.enumerate(line) { injectedPsi, _ ->
                if (injectedPsi is IsppFile) {
                    result.addAll(PsiTreeUtil.getChildrenOfTypeAsList(injectedPsi, IsppDirective::class.java))
                }
            }
            result
        }
}

/**
 * All ISPP directives paired with the host-file offset of the line they live on.
 * Because each `#define` line is injected as its own fragment, the host offset (the start of the
 * containing [IsiIsppLine], a direct child of this file) is the authority for declaration order.
 */
fun IssFile.isppDirectivesWithHostOffset(): List<Pair<IsppDirective, Int>> {
    val mgr = InjectedLanguageManager.getInstance(project)
    return PsiTreeUtil.getChildrenOfTypeAsList(this, IsiIsppLine::class.java)
        .flatMap { line ->
            val result = mutableListOf<Pair<IsppDirective, Int>>()
            mgr.enumerate(line) { injectedPsi, _ ->
                if (injectedPsi is IsppFile) {
                    PsiTreeUtil.getChildrenOfTypeAsList(injectedPsi, IsppDirective::class.java)
                        .forEach { result.add(it to line.textRange.startOffset) }
                }
            }
            result
        }
}

fun IssFile.definedConstants(): List<Pair<String, String?>> =
    isppDirectives()
        .filter { (it as? IsppDirectiveEx)?.isDefine() == true }
        .mapNotNull { directive ->
            val ex = directive as? IsppDirectiveEx ?: return@mapNotNull null
            val name = ex.getDefineName()?.ifEmpty { null } ?: return@mapNotNull null
            name to ex.getDefineValue()
        }

// ── PsiElement (Host) ──────────────────────────────────────────────────────────

fun PsiElement.issFile(): IssFile? = containingFile as? IssFile
