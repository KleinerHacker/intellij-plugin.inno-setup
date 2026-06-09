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

package org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.IsiAnnotatorHighlighting
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiConstant
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.isppDirectives
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirective
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppTypes
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.quickfix.RemoveUnusedDefineQuickFix

class IsppAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is IsppDirective) annotateDirective(element, holder)
    }

    private fun annotateDirective(directive: IsppDirective, holder: AnnotationHolder) {
        val hash = directive.node.findChildByType(IsppTypes.HASH) ?: return
        val keyword = directive.node.findChildByType(IsppTypes.IDENTIFIER) ?: return
        highlight(
            TextRange(hash.startOffset, keyword.textRange.endOffset),
            IsiAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder
        )

        val ex = directive as? IsppDirectiveEx ?: return
        if (!ex.isDefine()) return

        ex.nameIdentifier?.let {
            highlight(it.textRange, IsiAnnotatorHighlighting.DEFINE_NAME, holder)
        }

        // A function-like macro (#define Name(a,b) …) must have an expression body.
        if (ex.isFunctionMacro() && ex.getMacroBody() == null) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Function-like macro '${ex.getDefineName().orEmpty()}' requires an expression"
            ).range(directive.textRange).create()
            return
        }

        val name = ex.getDefineName() ?: return
        if (!isDefineUsed(directive, name)) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "#define '$name' is never used")
                .range(directive.textRange)
                .textAttributes(IsiAnnotatorHighlighting.UNUSED)
                .withFix(RemoveUnusedDefineQuickFix(directive))
                .create()
        }
    }

    private fun isDefineUsed(directive: IsppDirective, name: String): Boolean {
        val injMgr = InjectedLanguageManager.getInstance(directive.project)
        val hostFile = injMgr.getTopLevelFile(directive.containingFile) as? IssFile ?: return true

        // Check {#Name} references anywhere in the ISS host file.
        val usedAsConstant = PsiTreeUtil.findChildrenOfType(hostFile, IsiConstant::class.java).any { constant ->
            val body = constant.constantBody.text.trim()
            body.startsWith("#") && body.trimStart('#').trim().equals(name, ignoreCase = true)
        }
        if (usedAsConstant) return true

        // Check cross-references inside other #define expressions.
        return hostFile.isppDirectives
            .filter { it !== directive }
            .any { other -> other.references.any { ref -> ref.canonicalText.equals(name, ignoreCase = true) } }
    }

    private fun highlight(range: TextRange, key: TextAttributesKey, holder: AnnotationHolder) =
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range).textAttributes(key).create()
}
