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
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.IssAnnotatorHighlighting

class IsppAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (element is IsppDirective) annotateDirective(element, holder)
    }

    private fun annotateDirective(directive: IsppDirective, holder: AnnotationHolder) {
        val hash    = directive.node.findChildByType(IsppTypes.HASH) ?: return
        val keyword = directive.node.findChildByType(IsppTypes.IDENTIFIER) ?: return
        highlight(TextRange(hash.startOffset, keyword.textRange.endOffset),
            IssAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)

        val ex = directive as? IsppDirectiveEx ?: return
        if (!ex.isDefine()) return

        ex.getDefineTypeIdentifier()?.let {
            highlight(it.textRange, IssAnnotatorHighlighting.DEFINE_TYPE, holder)
        }
        ex.getNameIdentifier()?.let {
            highlight(it.textRange, IssAnnotatorHighlighting.DEFINE_NAME, holder)
        }

        // Validate typed define values
        val typeName = ex.getDefineTypeName() ?: return
        val rawVal   = ex.getDefineValue()    ?: return
        val valid = when (typeName.lowercase()) {
            "int", "integer"        -> rawVal.matches(Regex("-?[0-9]+"))
            "float", "double"       -> rawVal.matches(Regex("-?[0-9]*\\.?[0-9]+([eE][+-]?[0-9]+)?"))
            "str", "string", "any"  -> true
            else                    -> true
        }
        if (!valid) {
            val fileText   = directive.containingFile.text
            val valOffset  = fileText.indexOf(rawVal, directive.textOffset)
            val errorRange = if (valOffset >= directive.textOffset &&
                                 valOffset < directive.textRange.endOffset)
                TextRange(valOffset, valOffset + rawVal.length)
            else
                directive.textRange
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Type '$typeName' requires a ${typeDescription(typeName)} value, got: '$rawVal'"
            ).range(errorRange).create()
        }
    }

    private fun typeDescription(t: String) = when (t.lowercase()) {
        "int", "integer"  -> "integer"
        "float", "double" -> "numeric"
        else -> t
    }

    private fun highlight(range: TextRange, key: TextAttributesKey, holder: AnnotationHolder) =
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range).textAttributes(key).create()
}
