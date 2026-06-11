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
        if (section != null && section.nameText.equals("Code", ignoreCase = true))
            return false
        if (PsiTreeUtil.getParentOfType(element, IsiIsppLine::class.java) != null)
            return false
        return true
    }
}
