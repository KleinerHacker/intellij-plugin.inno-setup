package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Christoph on 08.11.2015.
 */
public abstract class IssAbstractSectionAnnotator<E extends IssDefinitionElement> implements Annotator {
    public enum DoubletCheckType {
        Error,
        Warning,
        None
    }

    private final Class<E> elementClass;
    private final DoubletCheckType doubletCheckType;

    protected IssAbstractSectionAnnotator(Class<E> elementClass, DoubletCheckType doubletCheckType) {
        this.elementClass = elementClass;
        this.doubletCheckType = doubletCheckType;
    }

    @Override
    public final void annotate(@NotNull final PsiElement psiElement, @NotNull final AnnotationHolder annotationHolder) {
        if (elementClass.isAssignableFrom(psiElement.getClass())) {
            final E definitionElement = (E)psiElement;

            checkRequiredProperties(definitionElement, annotationHolder);
            checkDeprecatedProperties(definitionElement, annotationHolder);
            findDoubleSectionProperties(definitionElement, annotationHolder);
            checkPropertyValues(definitionElement, annotationHolder);

            detectErrors(definitionElement, annotationHolder);
            detectWarnings(definitionElement, annotationHolder);
        }
    }

    private void checkRequiredProperties(@NotNull E definitionElement, @NotNull final AnnotationHolder annotationHolder) {
        final List<IssDefinableSectionIdentifier> notFoundList = new ArrayList<>();
        main: for (final IssDefinableSectionIdentifier identifier : definitionElement.getPropertyTypeList()) {
            if (!identifier.isRequired())
                continue;

            for (final IssPropertyElement propertyElement : (Collection<IssPropertyElement>)definitionElement.getDefinitionPropertyList()) {
                if (propertyElement.getPropertyType().equals(identifier))
                    continue main;
            }

            notFoundList.add(identifier);
        }

        if (notFoundList.size() > 0) {
            annotationHolder.createErrorAnnotation(definitionElement, "Unable to find required properties: " + notFoundList.toString());
        }
    }

    private void checkDeprecatedProperties(@NotNull E definitionElement, @NotNull final AnnotationHolder annotationHolder) {
        for (final IssPropertyElement propertyElement : (Collection<IssPropertyElement>)definitionElement.getDefinitionPropertyList()) {
            if (propertyElement.getPropertyType().isDeprecated()) {
                final Annotation warningAnnotation = annotationHolder.createWarningAnnotation(propertyElement, "Property is deprecated!");
                warningAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_WARN_PROPERTY_DEPRECATED);
            }
        }
    }

    private void findDoubleSectionProperties(@NotNull E definitionElement, @NotNull final AnnotationHolder annotationHolder) {
        if (doubletCheckType == DoubletCheckType.None)
            return;

        if (definitionElement.getParentSection() != null && areNeededDefinitionPropertiesExists(definitionElement)) {
            final long count = ((Collection<IssDefinitionElement>)definitionElement.getParentSection().getDefinitionList()).stream()
                    .filter(item -> item != definitionElement)
                    .filter(item -> areNeededDefinitionPropertiesExists((E)item))
                    .filter(item -> areDefinitionSame((E)item, definitionElement))
                    .count();
            if (count > 0) {
                if (doubletCheckType == DoubletCheckType.Error) {
                    annotationHolder.createErrorAnnotation(definitionElement, "Component with name '" + definitionElement.getName() + "' already defined");
                } else if (doubletCheckType == DoubletCheckType.Warning) {
                    annotationHolder.createWarningAnnotation(definitionElement, "Component with name '" + definitionElement.getName() + "' already defined");
                } else
                    throw new RuntimeException();
            }
        }
    }

    /**
     * Check that all needed property elements of definition are exists (!= null)
     * @param definitionElement
     * @return
     */
    protected boolean areNeededDefinitionPropertiesExists(@NotNull E definitionElement) {
        return definitionElement.getName() != null;
    }

    /**
     * Check that all needed property of definition are the same (definitions are equal)
     * @param definitionElementLeft
     * @param definitionElementRight
     * @return
     */
    protected boolean areDefinitionSame(@NotNull E definitionElementLeft, @NotNull E definitionElementRight) {
        return definitionElementLeft.getName().equalsIgnoreCase(definitionElementRight.getName());
    }

    private void checkPropertyValues(@NotNull E definitionElement, @NotNull final AnnotationHolder annotationHolder) {
        for (final IssPropertyElement propertyElement : (Collection<IssPropertyElement>)definitionElement.getDefinitionPropertyList()) {
            if (propertyElement.getPropertyValue() == null)
                continue;

            switch (propertyElement.getPropertyType().getValueType()) {
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
                    for (final IssPropertyValueElement valueElement : (Collection<IssPropertyValueElement>)propertyElement.getPropertyValueList()) {
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
    private void checkForSingleValue(@NotNull IssPropertyElement propertyElement, @NotNull final AnnotationHolder annotationHolder) {
        if (propertyElement.getPropertyValueList().size() > 1) {
            for (final IssPropertyValueElement valueElement : (Collection<IssPropertyValueElement>)propertyElement.getPropertyValueList()) {
                annotationHolder.createErrorAnnotation(valueElement, "Too many values were set for this property!");
            }
        }
    }

    protected abstract void detectErrors(@NotNull E element, @NotNull AnnotationHolder annotationHolder);
    protected abstract void detectWarnings(@NotNull E element, @NotNull AnnotationHolder annotationHolder);
}
