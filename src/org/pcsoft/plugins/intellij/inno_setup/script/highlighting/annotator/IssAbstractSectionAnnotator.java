package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;

import java.util.Collection;

/**
 * Created by Christoph on 08.11.2015.
 */
public abstract class IssAbstractSectionAnnotator<E extends IssDefinitionElement> implements Annotator {
    private final Class<E> elementClass;

    protected IssAbstractSectionAnnotator(Class<E> elementClass) {
        this.elementClass = elementClass;
    }

    @Override
    public final void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (elementClass.isAssignableFrom(psiElement.getClass())) {
            final E definitionElement = (E)psiElement;

            if (definitionElement.getSectionPropertyList() == null || definitionElement.getSectionPropertyList().isEmpty())
                return;

            IssAnnotatorUtils.findDoubleValues(
                    (Collection<IssDefinitionPropertyElement>)definitionElement.getSectionPropertyList(),
                    element -> element.getIdentifier() != null,
                    element -> element.getIdentifier().getText().toLowerCase(),
                    (element, key) -> annotationHolder.createErrorAnnotation(element, "Section property '" + key + "' already defined!")
            );

            detectErrors(definitionElement, annotationHolder);
            detectWarnings(definitionElement, annotationHolder);
        }
    }

    protected abstract void detectErrors(@NotNull E element, @NotNull AnnotationHolder annotationHolder);
    protected abstract void detectWarnings(@NotNull E element, @NotNull AnnotationHolder annotationHolder);
}
