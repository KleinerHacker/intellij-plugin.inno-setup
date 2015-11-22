package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssRegistryDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIOPermissions;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIOUserOrGroupIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRegistryFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRegistryValueType;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionRegistryAnnotator extends IssAbstractSectionAnnotator<IssRegistryDefinitionElement> {

    public IssSectionRegistryAnnotator() {
        super(IssRegistryDefinitionElement.class, DoubletCheckType.Warning);
    }

    @Override
    protected boolean areNeededDefinitionPropertiesExists(@NotNull IssRegistryDefinitionElement definitionElement) {
        return definitionElement.getRegistryRoot() != null && definitionElement.getRegistrySubkey() != null;
    }

    @Override
    protected boolean areDefinitionSame(@NotNull IssRegistryDefinitionElement definitionElementLeft, @NotNull IssRegistryDefinitionElement definitionElementRight) {
        final boolean basicCheck =
                definitionElementLeft.getRegistryRoot().getPropertyValue().getRootType() == definitionElementRight.getRegistryRoot().getPropertyValue().getRootType() &&
                        definitionElementLeft.getRegistrySubkey().getPropertyValue().getString().equalsIgnoreCase(definitionElementRight.getRegistrySubkey().getPropertyValue().getString());

        if (definitionElementLeft.getRegistryValueName() == null && definitionElementRight.getRegistryValueName() == null)
            return basicCheck;
        if ((definitionElementLeft.getRegistryValueName() == null && definitionElementRight.getRegistryValueName() != null) ||
                definitionElementLeft.getRegistryValueName() != null && definitionElementRight.getRegistryValueName() == null)
            return false;

        return basicCheck &&
                definitionElementLeft.getRegistryValueName().getPropertyValue().getString().equalsIgnoreCase(definitionElementRight.getRegistryValueName().getPropertyValue().getString());
    }

    @Override
    protected void detectErrors(@NotNull IssRegistryDefinitionElement registryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(registryDefinitionElement, annotationHolder);
        checkForKnownValues(registryDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssRegistryDefinitionElement registryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (registryDefinitionElement.getRegistryFlags() != null) {
            registryDefinitionElement.getRegistryFlags().getPropertyValueList().stream()
                    .filter(item -> IssRegistryFlag.fromId(item.getName()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown flag"));
        }
        if (registryDefinitionElement.getRegistryRoot() != null) {
            registryDefinitionElement.getRegistryRoot().getPropertyValueList().stream()
                    .filter(item -> item.getRootType() == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown root"));
        }
        if (registryDefinitionElement.getRegistryValueType() != null) {
            registryDefinitionElement.getRegistryValueType().getPropertyValueList().stream()
                    .filter(item -> IssRegistryValueType.fromId(item.getName()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown value type"));
        }
        if (registryDefinitionElement.getRegistryPermissions() != null) {
            registryDefinitionElement.getRegistryPermissions().getPropertyValueList().stream()
                    .filter(item -> IssIOPermissions.fromId(item.getPermission()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown permission"));
            registryDefinitionElement.getRegistryPermissions().getPropertyValueList().stream()
                    .filter(item -> IssIOUserOrGroupIdentifier.fromId(item.getUserOrGroupIdentifier()) == null)
                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown user or group identifier"));
        }
    }

    private void checkForReferences(@NotNull IssRegistryDefinitionElement registryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (registryDefinitionElement.getRegistryTaskReference() != null) {
            registryDefinitionElement.getRegistryTaskReference().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced task");
                        errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
        if (registryDefinitionElement.getRegistryComponentReference() != null) {
            registryDefinitionElement.getRegistryComponentReference().getPropertyValueList().stream()
                    .filter(item -> item.getReference().resolve() == null)
                    .forEach(item -> {
                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced component");
                        errorAnnotation.setTextAttributes(IssLanguageHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
                    });
        }
    }

    @Override
    protected void detectWarnings(@NotNull IssRegistryDefinitionElement registryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(registryDefinitionElement, annotationHolder);
        checkForDoubleValues(registryDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssRegistryDefinitionElement registryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (registryDefinitionElement.getRegistryFlags() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    registryDefinitionElement.getRegistryFlags().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                    }
            );
        }
        if (registryDefinitionElement.getRegistryPermissions() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    registryDefinitionElement.getRegistryPermissions().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Permission '" + key + "' already listed");
                    }
            );
        }
    }

    private void checkForDoubleReferences(@NotNull IssRegistryDefinitionElement registryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (registryDefinitionElement.getRegistryTaskReference() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    registryDefinitionElement.getRegistryTaskReference().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Task '" + key + "' already listed");
                    }
            );
        }
        if (registryDefinitionElement.getRegistryComponentReference() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    registryDefinitionElement.getRegistryComponentReference().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Component '" + key + "' already listed");
                    }
            );
        }
    }
}
