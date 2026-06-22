package org.pcsoft.intellij.plugin.inno_setup.language.parser.lang

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.allowedInLanguageFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.isInLanguageFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.IsSectionAnnotatorHighlighting
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionBlock
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionPreprocessorLine
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.IsSectionTitle
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.specSection
import org.pcsoft.intellij.plugin.inno_setup.services.IsSpecService

/**
 * ISL-specific validation, additive to [org.pcsoft.intellij.plugin.inno_setup.language.parser.section.IsSectionAnnotator].
 * Acts only inside `.isl` files and flags any section that is not permitted in an Inno Setup
 * language file. Unknown (non-spec) sections are left to the ISI annotator's "Unknown section" check.
 */
class IsLanguageAnnotator : Annotator {

    /**
     * Annotates the supplied PSI element when it matches this component's checks.
     */
    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!element.isInLanguageFile) return
        when (element) {
            is IsSectionPreprocessorLine -> annotatePreprocessorLine(element, holder)
            is IsSectionTitle -> annotateSectionName(element, holder)
        }
    }

    private fun annotatePreprocessorLine(line: IsSectionPreprocessorLine, holder: AnnotationHolder) {
        holder.newAnnotation(
            HighlightSeverity.ERROR,
            "Preprocessor directives are not allowed in Inno Setup language (.isl) files"
        )
            .range(line.textRange)
            .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
            .create()
    }

    private fun annotateSectionName(element: IsSectionTitle, holder: AnnotationHolder) {
        val section = element.parent?.parent as? IsSectionBlock ?: return
        val spec = service<IsSpecService>().spec
        val specSection = section.specSection(spec) ?: return // unknown section → handled by IsSectionAnnotator
        if (specSection.allowedInLanguageFile) return

        holder.newAnnotation(
            HighlightSeverity.ERROR,
            "Section '[${element.text}]' is not allowed in Inno Setup language (.isl) files"
        )
            .range(element.textRange)
            .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
            .create()
    }
}