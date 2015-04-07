package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.error;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.IssAnnotatorUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionItemElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.setup.IssSetupSectionElement;

import java.util.Collection;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssSectionErrorAnnotator implements Annotator {

    @Override
    public void annotate(PsiElement psiElement, AnnotationHolder annotationHolder) {
        findDoubleSectionItems(psiElement, annotationHolder);
    }

    private void findDoubleSectionItems(PsiElement psiElement, AnnotationHolder annotationHolder) {
        final Collection<IssDefinitionItemElement> collection;
        if (psiElement instanceof IssSetupSectionElement) {
            final IssSetupSectionElement setupSectionElement = (IssSetupSectionElement) psiElement;
            if (setupSectionElement.getSectionItemList() == null || setupSectionElement.getSectionItemList().isEmpty())
                return;

            collection = setupSectionElement.getSectionItemList();
        } else if (psiElement instanceof IssDefinitionElement) {
            final IssDefinitionElement definitionElement = (IssDefinitionElement) psiElement;
            if (definitionElement.getSectionItemList() == null || definitionElement.getSectionItemList().isEmpty())
                return;

            collection = definitionElement.getSectionItemList();
        } else
            return;

        IssAnnotatorUtils.findDoubleValues(
                collection,
                element -> element.getIdentifier() != null,
                element -> element.getIdentifier().getText().toLowerCase(),
                (element, key) -> {
                    annotationHolder.createErrorAnnotation(element, "Section item '" + key + "' already defined!");
                }
        );
    }
}
