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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.isppDirectives
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorTypes
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.quickfix.RemoveUnusedDefineQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.IsSectionAnnotatorHighlighting
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionConstant

/**
 * Annotates Inno Setup PSI elements with validation and highlighting information.
 */
class IsPreprocessorAnnotator : Annotator {

    /**
     * Annotates the supplied PSI element when it matches this component's checks.
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is IsPreprocessorDirective) annotateDirective(element, holder)
    }

    private fun annotateDirective(directive: IsPreprocessorDirective, holder: AnnotationHolder) {
        val hash = directive.node.findChildByType(IsPreprocessorTypes.HASH) ?: return
        val keyword = directive.node.findChildByType(IsPreprocessorTypes.IDENTIFIER) ?: return
        highlight(
            TextRange(hash.startOffset, keyword.textRange.endOffset),
            IsSectionAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder
        )

        val ex = directive as? IsPreprocessorDirectiveEx ?: return
        if (!ex.isDefine()) return

        ex.nameIdentifier?.let {
            highlight(it.textRange, IsSectionAnnotatorHighlighting.DEFINE_NAME, holder)
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
                .textAttributes(IsSectionAnnotatorHighlighting.UNUSED)
                .withFix(RemoveUnusedDefineQuickFix(directive))
                .create()
        }
    }

    private fun isDefineUsed(directive: IsPreprocessorDirective, name: String): Boolean {
        val injMgr = InjectedLanguageManager.getInstance(directive.project)
        val hostFile = injMgr.getTopLevelFile(directive.containingFile) as? IsScriptFile ?: return true

        // Check {#Name} references anywhere in the ISS host file.
        val usedAsConstant = PsiTreeUtil.findChildrenOfType(hostFile, IsSectionConstant::class.java).any { constant ->
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
