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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.template.psi.impl

import com.intellij.extapi.psi.ASTWrapperPsiElement
import com.intellij.lang.ASTNode
import com.intellij.psi.LiteralTextEscaper
import com.intellij.psi.PsiFileFactory
import com.intellij.psi.PsiLanguageInjectionHost
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.template.IsTemplateLanguage
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorHostLine
import org.pcsoft.intellij.plugin.inno_setup.language.parser.template.psi.IsTemplatePreprocessorLine

abstract class IsTemplatePreprocessorLineMixinImpl(node: ASTNode) : ASTWrapperPsiElement(node),
    PsiLanguageInjectionHost, IsPreprocessorHostLine {

    override fun isValidHost(): Boolean = true

    override fun updateText(text: String): PsiLanguageInjectionHost {
        val factory = PsiFileFactory.getInstance(project)
        val newFile = factory.createFileFromText("d.ist", IsTemplateLanguage, "$text\n")
        val newLine = PsiTreeUtil.findChildOfType(newFile, IsTemplatePreprocessorLine::class.java) ?: return this
        return replace(newLine) as? PsiLanguageInjectionHost ?: this
    }

    override fun createLiteralTextEscaper(): LiteralTextEscaper<out PsiLanguageInjectionHost> =
        LiteralTextEscaper.createSimple(this as IsTemplatePreprocessorLine)
}
