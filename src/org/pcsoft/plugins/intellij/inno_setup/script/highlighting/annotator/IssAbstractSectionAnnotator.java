package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.lexer.IssTokenFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssPropertyIdentifier;

import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by Christoph on 08.11.2015.
 */
public abstract class IssAbstractSectionAnnotator<E extends IssDefinitionElement> implements Annotator {
    protected static final String ERROR_REFERENCE_TASK = "Cannot find referenced task";
    protected static final String ERROR_REFERENCE_COMPONENT = "Cannot find referenced component";
    protected static final String WARN_REFERENCE_TASK = "Task '%s' already listed";
    protected static final String WARN_REFERENCE_COMPONENT = "Component '%s' already listed";

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
            final E definitionElement = (E) psiElement;

            checkRequiredProperties(definitionElement, annotationHolder);
            checkDeprecatedProperties(definitionElement, annotationHolder);
            findDoubleSectionProperties(definitionElement, annotationHolder);
            checkPropertyValues(definitionElement, annotationHolder);

            //Check for illegal reference
            ((Collection<IssPropertyElement>) definitionElement.getDefinitionPropertyList()).stream()
                    .filter(propertyElement -> propertyElement.getIdentifier() != null && propertyElement.getIdentifier().getIdentifierReference() != null)
                    .forEach(propertyElement ->
                            annotationHolder.createErrorAnnotation(propertyElement.getIdentifier().getIdentifierReference(),
                                    "Illegal reference: not allowed here!")
                    );

            detectErrors(definitionElement, annotationHolder);
            detectWarnings(definitionElement, annotationHolder);
        }
    }

    private void checkRequiredProperties(@NotNull E definitionElement, @NotNull final AnnotationHolder annotationHolder) {
        final List<IssPropertyIdentifier> notFoundList = new ArrayList<>();
        main:
        for (final IssPropertyIdentifier identifier : definitionElement.getPropertyTypeList()) {
            if (!identifier.isRequired())
                continue;

            for (final IssPropertyElement propertyElement : (Collection<IssPropertyElement>) definitionElement.getDefinitionPropertyList()) {
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
        for (final IssPropertyElement propertyElement : (Collection<IssPropertyElement>) definitionElement.getDefinitionPropertyList()) {
            if (propertyElement.getPropertyType().isDeprecated()) {
                final Annotation warningAnnotation = annotationHolder.createWarningAnnotation(propertyElement, "Property is deprecated!");
                warningAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_WARN_DEPRECATED);
            }
        }
    }

    private void findDoubleSectionProperties(@NotNull E definitionElement, @NotNull final AnnotationHolder annotationHolder) {
        if (doubletCheckType == DoubletCheckType.None)
            return;

        if (definitionElement.getParentSection() != null && areNeededDefinitionPropertiesExists(definitionElement)) {
            final long count = ((Collection<IssDefinitionElement>) definitionElement.getParentSection().getDefinitionList()).stream()
                    .filter(item -> item != definitionElement)
                    .filter(item -> areNeededDefinitionPropertiesExists((E) item))
                    .filter(item -> areDefinitionSame((E) item, definitionElement))
                    .count();
            if (count > 0) {
                if (doubletCheckType == DoubletCheckType.Error) {
                    annotationHolder.createErrorAnnotation(definitionElement, "Definition with name '" + definitionElement.getName() + "' already defined");
                } else if (doubletCheckType == DoubletCheckType.Warning) {
                    annotationHolder.createWarningAnnotation(definitionElement, "Definition with name '" + definitionElement.getName() + "' already defined");
                } else
                    throw new RuntimeException();
            }
        }
    }

    /**
     * Check that all needed property elements of definition are exists (!= null)
     *
     * @param definitionElement
     * @return
     */
    protected boolean areNeededDefinitionPropertiesExists(@NotNull E definitionElement) {
        return definitionElement.getName() != null;
    }

    /**
     * Check that all needed property of definition are the same (definitions are equal)
     *
     * @param definitionElementLeft
     * @param definitionElementRight
     * @return
     */
    protected boolean areDefinitionSame(@NotNull E definitionElementLeft, @NotNull E definitionElementRight) {
        return definitionElementLeft.getName().equalsIgnoreCase(definitionElementRight.getName());
    }

    private void checkPropertyValues(@NotNull E definitionElement, @NotNull final AnnotationHolder annotationHolder) {
        for (final IssPropertyElement<?> propertyElement : (Collection<IssPropertyElement>) definitionElement.getDefinitionPropertyList()) {
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
                case DirectSingleWithNumber:
                    checkForSingleValue(propertyElement, annotationHolder);
                    if (propertyElement.getPropertyValue().getFirstChild().getNode().getElementType() == IssTokenFactory.STRING) {
                        annotationHolder.createErrorAnnotation(propertyElement.getPropertyValue(), "Direct value expected");
                    }
                    break;
                case DirectMultiple:
                    for (final IssPropertyValueElement valueElement : propertyElement.getPropertyValueList()) {
                        if (valueElement.getFirstChild().getNode().getElementType() == IssTokenFactory.STRING ||
                                NumberUtils.isNumber(valueElement.getText())) {
                            annotationHolder.createErrorAnnotation(valueElement, "Direct value expected");
                        }
                    }
                    break;
                case DirectMultipleWithNumber:
                    for (final IssPropertyValueElement valueElement : propertyElement.getPropertyValueList()) {
                        if (valueElement.getFirstChild().getNode().getElementType() == IssTokenFactory.STRING) {
                            annotationHolder.createErrorAnnotation(valueElement, "Direct value expected");
                        }
                    }
                    break;
                case Boolean:
                    checkForSingleValue(propertyElement, annotationHolder);
                    try {
                        BooleanUtils.toBoolean(propertyElement.getPropertyValue().getText(), "yes", "no");
                    } catch (Exception e) {
                        annotationHolder.createErrorAnnotation(propertyElement.getPropertyValue(), "Must be 'yes' or 'no'");
                    }
                    break;
                case HexBinary:
                    checkForSingleValue(propertyElement, annotationHolder);
                    try {
                        new HexBinaryAdapter().unmarshal((propertyElement.getPropertyValue().getText().length() % 2 != 0 ? "0" : "")
                                + propertyElement.getPropertyValue().getText());
                    } catch (Exception e) {
                        annotationHolder.createErrorAnnotation(propertyElement.getPropertyValue(), "This is not a hex binary string!");
                    }
                    break;
                default:
                    throw new RuntimeException();
            }
        }
    }

    /**
     * Check that value list is exactly one
     *
     * @param propertyElement
     * @param annotationHolder
     */
    private void checkForSingleValue(@NotNull IssPropertyElement propertyElement, @NotNull final AnnotationHolder annotationHolder) {
        if (propertyElement.getPropertyValueList().size() > 1) {
            for (final IssPropertyValueElement valueElement : (Collection<IssPropertyValueElement>) propertyElement.getPropertyValueList()) {
                annotationHolder.createErrorAnnotation(valueElement, "Too many values were set for this property!");
            }
        }
    }

    protected abstract void detectErrors(@NotNull E element, @NotNull AnnotationHolder annotationHolder);

    protected abstract void detectWarnings(@NotNull E element, @NotNull AnnotationHolder annotationHolder);

    //region Special Utility Methods (Static)
    private static void checkForKnownValues(AnnotationHolder annotationHolder, Supplier<IssPropertyElement> propertySupplier,
                                            Function<IssPropertyValueElement, Boolean> valueUnknownCheckCallback,
                                            Consumer<IssPropertyValueElement> handleCallback) {
        final IssPropertyElement propertyElement = propertySupplier.get();
        if (propertyElement != null) {
            propertyElement.getPropertyValueList().stream()
                    .filter(item -> valueUnknownCheckCallback.apply((IssPropertyValueElement) item))
                    .forEach(item -> handleCallback.accept((IssPropertyValueElement) item));
        }
    }

    protected static void checkForKnownValues(AnnotationHolder annotationHolder, Supplier<IssPropertyElement> propertySupplier,
                                              Function<IssPropertyValueElement, Boolean> valueUnknownCheckCallback, String errorMsg) {
        checkForKnownValues(annotationHolder, propertySupplier, valueUnknownCheckCallback,
                p -> annotationHolder.createErrorAnnotation(p, errorMsg));
    }

    protected static void checkForDoubleValues(AnnotationHolder annotationHolder, Supplier<IssPropertyElement> propertySupplier,
                                               String warnMsg) {
        final IssPropertyElement propertyElement = propertySupplier.get();
        if (propertyElement != null) {
            IssAnnotatorUtils.findDoubleValues(
                    propertyElement.getPropertyValueList(),
                    element -> ((IssPropertyValueElement) element).getName().toLowerCase(),
                    (element, key) -> annotationHolder.createWarningAnnotation((IssPropertyValueElement) element, String.format(warnMsg, key))
            );
        }
    }

    protected static void checkForReferences(AnnotationHolder annotationHolder, Supplier<IssPropertyElement> propertySupplier, String errorMsg) {
        checkForKnownValues(annotationHolder, propertySupplier, p -> p.getReference().resolve() == null, p -> {
            final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(p, errorMsg);
            errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
        });
    }

    protected static void checkForDoubleReferences(AnnotationHolder annotationHolder, Supplier<IssPropertyElement> propertySupplier, String warnMsg) {
        checkForDoubleValues(annotationHolder, propertySupplier, warnMsg);
    }
    //endregion
}
