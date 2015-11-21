package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssINIDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssINIFlag;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionINIAnnotator extends IssAbstractSectionAnnotator<IssINIDefinitionElement> {

    public IssSectionINIAnnotator() {
        super(IssINIDefinitionElement.class, DoubletCheckType.Warning);
    }

    @Override
    protected boolean areNeededDefinitionPropertiesExists(@NotNull IssINIDefinitionElement definitionElement) {
        return definitionElement.getINIFilename() != null && definitionElement.getINISection() != null;
    }

    @Override
    protected boolean areDefinitionSame(@NotNull IssINIDefinitionElement definitionElementLeft, @NotNull IssINIDefinitionElement definitionElementRight) {
        final boolean basicCheck =
                definitionElementLeft.getINIFilename().getPropertyValue().getString().equalsIgnoreCase(definitionElementRight.getINIFilename().getPropertyValue().getString()) &&
                        definitionElementLeft.getINISection().getPropertyValue().getString().equalsIgnoreCase(definitionElementRight.getINISection().getPropertyValue().getString());

        if (definitionElementLeft.getINIKey() == null && definitionElementRight.getINIKey() == null)
            return basicCheck;
        if ((definitionElementLeft.getINIKey() == null && definitionElementRight.getINIKey() != null) ||
                definitionElementLeft.getINIKey() != null && definitionElementRight.getINIKey() == null)
            return false;

        return basicCheck &&
                definitionElementLeft.getINIKey().getPropertyValue().getString().equalsIgnoreCase(definitionElementRight.getINIKey().getPropertyValue().getString());
    }

    @Override
    protected void detectErrors(@NotNull IssINIDefinitionElement iniDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(iniDefinitionElement, annotationHolder);
        checkForKnownValues(iniDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssINIDefinitionElement iniDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (iniDefinitionElement.getINIFlags() != null) {
            iniDefinitionElement.getINIFlags().getPropertyValueList().stream()
                    .filter(item -> IssINIFlag.fromId(item.getName()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown flag"));
        }
    }

    private void checkForReferences(@NotNull IssINIDefinitionElement iniDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (iniDefinitionElement.getINITaskReference() != null) {
            iniDefinitionElement.getINITaskReference().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced task");
                        errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
        if (iniDefinitionElement.getINIComponentReference() != null) {
            iniDefinitionElement.getINIComponentReference().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced component");
                        errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
    }

    @Override
    protected void detectWarnings(@NotNull IssINIDefinitionElement iniDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(iniDefinitionElement, annotationHolder);
        checkForDoubleValues(iniDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssINIDefinitionElement iniDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (iniDefinitionElement.getINIFlags() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    iniDefinitionElement.getINIFlags().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                    }
            );
        }
    }

    private void checkForDoubleReferences(@NotNull IssINIDefinitionElement iniDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (iniDefinitionElement.getINITaskReference() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    iniDefinitionElement.getINITaskReference().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Task '" + key + "' already listed");
                    }
            );
        }
        if (iniDefinitionElement.getINIComponentReference() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    iniDefinitionElement.getINIComponentReference().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Component '" + key + "' already listed");
                    }
            );
        }
    }
}
