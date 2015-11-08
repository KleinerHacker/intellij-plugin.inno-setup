package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyValueElement;

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
    public final void annotate(@NotNull final PsiElement psiElement, @NotNull final AnnotationHolder annotationHolder) {
        if (elementClass.isAssignableFrom(psiElement.getClass())) {
            final E definitionElement = (E)psiElement;

            findDoubleSectionProperties(definitionElement, annotationHolder);
            checkPropertyValues(definitionElement, annotationHolder);

            detectErrors(definitionElement, annotationHolder);
            detectWarnings(definitionElement, annotationHolder);
        }
    }

    private void findDoubleSectionProperties(@NotNull E definitionElement, @NotNull final AnnotationHolder annotationHolder) {
        if (definitionElement.getParentSection() != null && definitionElement.getName() != null) {
            final long count = ((Collection<IssDefinitionElement>)definitionElement.getParentSection().getDefinitionList()).stream()
                    .filter(item -> item != definitionElement)
                    .filter(item -> item.getName() != null)
                    .filter(item -> item.getName().equals(definitionElement.getName()))
                    .count();
            if (count > 0) {
                annotationHolder.createErrorAnnotation(definitionElement, "Component with name '" + definitionElement.getName() + "' already defined");
            }
        }
    }

    private void checkPropertyValues(@NotNull E definitionElement, @NotNull final AnnotationHolder annotationHolder) {
        for (final IssDefinitionPropertyElement propertyElement : (Collection<IssDefinitionPropertyElement>)definitionElement.getDefinitionPropertyList()) {
            if (propertyElement.getPropertyValue() == null)
                continue;

            switch (propertyElement.getItemValueType()) {
                case Unknown:
                    break;
                case String:
                    checkForSingleValue(propertyElement, annotationHolder);
                    if (propertyElement.getPropertyValue().getFirstChild().getNode().getElementType() != IssTokenFactory.STRING) {
                        annotationHolder.createErrorAnnotation(propertyElement.getPropertyValue(), "String expected");
                    }
                    break;
                case Integer:
                    checkForSingleValue(propertyElement, annotationHolder);
                    try {
                        Integer.parseInt(propertyElement.getPropertyValue().getText());
                    } catch (NumberFormatException e) {
                        annotationHolder.createErrorAnnotation(propertyElement.getPropertyValue(), "Integer expected");
                    }
                    break;
                case Double:
                    checkForSingleValue(propertyElement, annotationHolder);
                    try {
                        Double.parseDouble(propertyElement.getPropertyValue().getText());
                    } catch (NumberFormatException e) {
                        annotationHolder.createErrorAnnotation(propertyElement.getPropertyValue(), "Double expected");
                    }
                    break;
                case DirectSingle:
                    checkForSingleValue(propertyElement, annotationHolder);
                    if (propertyElement.getPropertyValue().getFirstChild().getNode().getElementType() == IssTokenFactory.STRING ||
                            NumberUtils.isNumber(propertyElement.getPropertyValue().getText())) {
                        annotationHolder.createErrorAnnotation(propertyElement.getPropertyValue(), "Direct value expected");
                    }
                    break;
                case DirectMultiple:
                    for (final IssDefinitionPropertyValueElement valueElement : (Collection<IssDefinitionPropertyValueElement>)propertyElement.getPropertyValueList()) {
                        if (valueElement.getFirstChild().getNode().getElementType() == IssTokenFactory.STRING ||
                                NumberUtils.isNumber(valueElement.getText())) {
                            annotationHolder.createErrorAnnotation(valueElement, "Direct value expected");
                        }
                    }
                    break;
                default:
                    throw new RuntimeException();
            }
        }
    }

    /**
     * Check that value list is exactly one
     * @param propertyElement
     * @param annotationHolder
     */
    private void checkForSingleValue(@NotNull IssDefinitionPropertyElement propertyElement, @NotNull final AnnotationHolder annotationHolder) {
        if (propertyElement.getPropertyValueList().size() > 1) {
            for (final IssDefinitionPropertyValueElement valueElement : (Collection<IssDefinitionPropertyValueElement>)propertyElement.getPropertyValueList()) {
                annotationHolder.createErrorAnnotation(valueElement, "Too many values were set for this property!");
            }
        }
    }

    protected abstract void detectErrors(@NotNull E element, @NotNull AnnotationHolder annotationHolder);
    protected abstract void detectWarnings(@NotNull E element, @NotNull AnnotationHolder annotationHolder);
}
