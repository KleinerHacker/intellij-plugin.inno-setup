package org.pcsoft.intellij.plugin.inno_setup.language.parsing

import com.intellij.codeInsight.highlighting.HighlightErrorFilter
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.IssSection

class IssErrorFilter : HighlightErrorFilter() {
    override fun shouldHighlightErrorElement(element: PsiErrorElement): Boolean {
        val section = PsiTreeUtil.getParentOfType(element, IssSection::class.java)
        if (section != null && section.nameText().equals("Code", ignoreCase = true))
            return false
        if (PsiTreeUtil.getParentOfType(element, IssPreprocessorDirective::class.java) != null)
            return false
        return true
    }
}
