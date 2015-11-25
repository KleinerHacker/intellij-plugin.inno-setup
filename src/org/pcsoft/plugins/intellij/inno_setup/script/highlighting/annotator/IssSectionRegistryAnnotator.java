package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssRegistryDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOPermissionsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.*;

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
        checkForKnownValues(annotationHolder, registryDefinitionElement::getRegistryFlags, p -> IssRegistryFlag.fromId(p.getName()) == null, "Unknown flag");
        checkForKnownValues(annotationHolder, registryDefinitionElement::getRegistryRoot, p -> IssRegistryRoot.fromId(p.getName()) == null, "Unknown root");
        checkForKnownValues(annotationHolder, registryDefinitionElement::getRegistryValueType, p -> IssRegistryValueType.fromId(p.getName()) == null, "Unknown value type");
        checkForKnownValues(annotationHolder, registryDefinitionElement::getRegistryPermissions,
                p -> IssIOPermissions.fromId(((IssPropertyIOPermissionsValueElement)p).getPermission()) == null, "Unknown permission");
        checkForKnownValues(annotationHolder, registryDefinitionElement::getRegistryPermissions,
                p -> IssIOUserOrGroupIdentifier.fromId(((IssPropertyIOPermissionsValueElement)p).getUserOrGroupIdentifier()) == null, "Unknown user or group identifier");
    }

    private void checkForReferences(@NotNull IssRegistryDefinitionElement registryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(annotationHolder, registryDefinitionElement::getRegistryTaskReference, ERROR_REFERENCE_TASK);
        checkForReferences(annotationHolder, registryDefinitionElement::getRegistryComponentReference, ERROR_REFERENCE_COMPONENT);
    }

    @Override
    protected void detectWarnings(@NotNull IssRegistryDefinitionElement registryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(registryDefinitionElement, annotationHolder);
        checkForDoubleValues(registryDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssRegistryDefinitionElement registryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(annotationHolder, registryDefinitionElement::getRegistryFlags, "Flag '%s' already listed");
        checkForDoubleValues(annotationHolder, registryDefinitionElement::getRegistryPermissions, "Permission '%s' already listed");
    }

    private void checkForDoubleReferences(@NotNull IssRegistryDefinitionElement registryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(annotationHolder, registryDefinitionElement::getRegistryTaskReference, WARN_REFERENCE_TASK);
        checkForDoubleReferences(annotationHolder, registryDefinitionElement::getRegistryComponentReference, WARN_REFERENCE_COMPONENT);
    }
}
