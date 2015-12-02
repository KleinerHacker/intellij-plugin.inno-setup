package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.section;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssDirectoryDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOPermissionsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssDirectoryFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssIOAttribute;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssIOPermissions;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.IssIOUserOrGroupIdentifier;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionDirectoryAnnotator extends IssAbstractSectionAnnotator<IssDirectoryDefinitionElement> {

    public IssSectionDirectoryAnnotator() {
        super(IssDirectoryDefinitionElement.class, DoubletCheckType.Warning);
    }

    @Override
    protected void detectErrors(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(directoryDefinitionElement, annotationHolder);
        checkForKnownValues(directoryDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForKnownValues(annotationHolder, directoryDefinitionElement::getDirectoryFlags, p -> IssDirectoryFlag.fromId(p.getName()) == null, "Unknown flag");
        checkForKnownValues(annotationHolder, directoryDefinitionElement::getDirectoryAttribute, p -> IssIOAttribute.fromId(p.getName()) == null, "Unknown attribute");
        checkForKnownValues(annotationHolder, directoryDefinitionElement::getDirectoryPermissions,
                p -> IssIOPermissions.fromId(((IssPropertyIOPermissionsValueElement)p).getPermission()) == null, "Unknown permission");
        checkForKnownValues(annotationHolder, directoryDefinitionElement::getDirectoryPermissions,
                p -> IssIOUserOrGroupIdentifier.fromId(((IssPropertyIOPermissionsValueElement)p).getUserOrGroupIdentifier()) == null, "Unknown user or group identifier");
    }

    private void checkForReferences(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(annotationHolder, directoryDefinitionElement::getDirectoryTaskReference, ERROR_REFERENCE_TASK);
        checkForReferences(annotationHolder, directoryDefinitionElement::getDirectoryComponentReference, ERROR_REFERENCE_COMPONENT);
    }

    @Override
    protected void detectWarnings(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(directoryDefinitionElement, annotationHolder);
        checkForDoubleValues(directoryDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(annotationHolder, directoryDefinitionElement::getDirectoryFlags, "Flag '%s' already listed");
        checkForDoubleValues(annotationHolder, directoryDefinitionElement::getDirectoryAttribute, "Attribute '%s' already listed");
        checkForDoubleValues(annotationHolder, directoryDefinitionElement::getDirectoryPermissions, "Permission '%s' already listed");
    }

    private void checkForDoubleReferences(@NotNull IssDirectoryDefinitionElement directoryDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(annotationHolder, directoryDefinitionElement::getDirectoryTaskReference, WARN_REFERENCE_TASK);
        checkForDoubleReferences(annotationHolder, directoryDefinitionElement::getDirectoryComponentReference, WARN_REFERENCE_COMPONENT);
    }
}
