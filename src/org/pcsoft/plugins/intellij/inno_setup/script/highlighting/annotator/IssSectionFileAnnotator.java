package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssFileDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyIOPermissionsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.*;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionFileAnnotator extends IssAbstractSectionAnnotator<IssFileDefinitionElement> {

    public IssSectionFileAnnotator() {
        super(IssFileDefinitionElement.class, DoubletCheckType.Warning);
    }

    @Override
    protected boolean areNeededDefinitionPropertiesExists(@NotNull IssFileDefinitionElement definitionElement) {
        return definitionElement.getFileSource() != null && definitionElement.getFileDestDir() != null;
    }

    @Override
    protected boolean areDefinitionSame(@NotNull IssFileDefinitionElement definitionElementLeft, @NotNull IssFileDefinitionElement definitionElementRight) {
        final boolean basicCheck =
                definitionElementLeft.getFileSource().getPropertyValue().getString().equalsIgnoreCase(definitionElementRight.getFileSource().getPropertyValue().getString()) &&
                definitionElementLeft.getFileDestDir().getPropertyValue().getString().equalsIgnoreCase(definitionElementRight.getFileDestDir().getPropertyValue().getString());

        if (definitionElementLeft.getFileDestName() == null && definitionElementRight.getFileDestName() == null)
            return basicCheck;
        if ((definitionElementLeft.getFileDestName() == null && definitionElementRight.getFileDestName() != null) ||
                definitionElementLeft.getFileDestName() != null && definitionElementRight.getFileDestName() == null)
            return false;

        return basicCheck &&
                definitionElementLeft.getFileDestName().getPropertyValue().getString().equalsIgnoreCase(definitionElementRight.getFileDestName().getPropertyValue().getString());
    }

    @Override
    protected void detectErrors(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(fileDefinitionElement, annotationHolder);
        checkForKnownValues(fileDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForKnownValues(annotationHolder, fileDefinitionElement::getFileFlags, p -> IssFileFlag.fromId(p.getName()) == null, "Unknown flag");
        checkForKnownValues(annotationHolder, fileDefinitionElement::getFileAttribute, p -> IssIOAttribute.fromId(p.getName()) == null, "Unknown attribute");
        checkForKnownValues(annotationHolder, fileDefinitionElement::getFileCopyMode, p -> IssFileCopyMode.fromId(p.getName()) == null, "Unknown copy mode");
        checkForKnownValues(annotationHolder, fileDefinitionElement::getFilePermissions,
                p -> IssIOPermissions.fromId(((IssPropertyIOPermissionsValueElement)p).getPermission()) == null, "Unknown permission");
        checkForKnownValues(annotationHolder, fileDefinitionElement::getFilePermissions,
                p -> IssIOUserOrGroupIdentifier.fromId(((IssPropertyIOPermissionsValueElement)p).getUserOrGroupIdentifier()) == null, "Unknown user or group identifier");
        if (fileDefinitionElement.getFileExternalSize() != null && fileDefinitionElement.getFileExternalSize().getPropertyValue() != null &&
                fileDefinitionElement.getFileExternalSize().getPropertyValue().getNumber() != null) {
            if (fileDefinitionElement.getFileExternalSize().getPropertyValue().getNumber() < 0) {
                annotationHolder.createErrorAnnotation(fileDefinitionElement.getFileExternalSize().getPropertyValue(), "External size must be positive!");
            }
        }
    }

    private void checkForReferences(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(annotationHolder, fileDefinitionElement::getFileTaskReference, ERROR_REFERENCE_TASK);
        checkForReferences(annotationHolder, fileDefinitionElement::getFileComponentReference, ERROR_REFERENCE_COMPONENT);
    }

    @Override
    protected void detectWarnings(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(fileDefinitionElement, annotationHolder);
        checkForDoubleValues(fileDefinitionElement, annotationHolder);
        if (fileDefinitionElement.getFileExternalSize() != null && fileDefinitionElement.getFileExternalSize().getPropertyValue() != null &&
                fileDefinitionElement.getFileExternalSize().getPropertyValue().getNumber() != null) {
            if (fileDefinitionElement.getFileExternalSize().getPropertyValue().getNumber() == 0) {
                annotationHolder.createWarningAnnotation(fileDefinitionElement.getFileExternalSize().getPropertyValue(), "External size should be greater than 0!");
            }
        }
    }

    private void checkForDoubleValues(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(annotationHolder, fileDefinitionElement::getFileFlags, "Flag '%s' already listed");
        checkForDoubleValues(annotationHolder, fileDefinitionElement::getFileAttribute, "Attribute '%s' already listed");
        checkForDoubleValues(annotationHolder, fileDefinitionElement::getFilePermissions, "Permission '%s' already listed");
    }

    private void checkForDoubleReferences(@NotNull IssFileDefinitionElement fileDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(annotationHolder, fileDefinitionElement::getFileTaskReference, WARN_REFERENCE_TASK);
        checkForDoubleReferences(annotationHolder, fileDefinitionElement::getFileComponentReference, WARN_REFERENCE_COMPONENT);
    }
}
