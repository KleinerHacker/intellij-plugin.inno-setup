package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

import com.intellij.codeInsight.highlighting.HighlightErrorFilter
import com.intellij.psi.PsiErrorElement
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.isi.nameText
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiIsppLine
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiSection

class IsiErrorFilter : HighlightErrorFilter() {
    override fun shouldHighlightErrorElement(element: PsiErrorElement): Boolean {
        val section = PsiTreeUtil.getParentOfType(element, IsiSection::class.java)
        if (section != null && section.nameText().equals("Code", ignoreCase = true))
            return false
        if (PsiTreeUtil.getParentOfType(element, IsiIsppLine::class.java) != null)
            return false
        return true
    }
}
