package org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirective
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.parsing.psi.IsppTypes
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.IsiAnnotatorHighlighting

class IsppAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is IsppDirective) annotateDirective(element, holder)
    }

    private fun annotateDirective(directive: IsppDirective, holder: AnnotationHolder) {
        val hash    = directive.node.findChildByType(IsppTypes.HASH) ?: return
        val keyword = directive.node.findChildByType(IsppTypes.IDENTIFIER) ?: return
        highlight(TextRange(hash.startOffset, keyword.textRange.endOffset),
            IsiAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)

        val ex = directive as? IsppDirectiveEx ?: return
        if (!ex.isDefine()) return

        ex.getNameIdentifier()?.let {
            highlight(it.textRange, IsiAnnotatorHighlighting.DEFINE_NAME, holder)
        }

        // A function-like macro (#define Name(a,b) …) must have an expression body.
        if (ex.isFunctionMacro() && ex.getMacroBody() == null) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Function-like macro '${ex.getDefineName().orEmpty()}' requires an expression"
            ).range(directive.textRange).create()
        }
    }

    private fun highlight(range: TextRange, key: TextAttributesKey, holder: AnnotationHolder) =
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range).textAttributes(key).create()
}
