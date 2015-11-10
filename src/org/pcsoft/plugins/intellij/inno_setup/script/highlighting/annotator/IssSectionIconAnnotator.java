package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.icon.IssIconDefinitionElement;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionIconAnnotator extends IssAbstractSectionAnnotator<IssIconDefinitionElement> {

    public IssSectionIconAnnotator() {
        super(IssIconDefinitionElement.class, DoubletCheckType.Warning);
    }

    @Override
    protected void detectErrors(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
//        checkForReferences(iconDefinitionElement, annotationHolder);
//        checkForKnownValues(iconDefinitionElement, annotationHolder);
    }

//    private void checkForKnownValues(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
//        if (iconDefinitionElement.getFileFlags() != null) {
//            iconDefinitionElement.getFileFlags().getPropertyValueList().stream()
//                    .filter(item -> IssFileFlag.fromId(item.getName()) == null)
//                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown flag"));
//        }
//        if (iconDefinitionElement.getFileCopyMode() != null) {
//            iconDefinitionElement.getFileCopyMode().getPropertyValueList().stream()
//                    .filter(item -> IssFileCopyMode.fromId(item.getName()) == null)
//                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown copy mode"));
//        }
//        if (iconDefinitionElement.getFileAttribute() != null) {
//            iconDefinitionElement.getFileAttribute().getPropertyValueList().stream()
//                    .filter(item -> IssCommonIOAttribute.fromId(item.getName()) == null)
//                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown attribute"));
//        }
//        if (iconDefinitionElement.getFilePermissions() != null) {
//            iconDefinitionElement.getFilePermissions().getPropertyValueList().stream()
//                    .filter(item -> IssCommonIOPermissions.fromId(item.getPermission()) == null)
//                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown permission"));
//            iconDefinitionElement.getFilePermissions().getPropertyValueList().stream()
//                    .filter(item -> IssCommonUserOrGroupIdentifier.fromId(item.getUserOrGroupIdentifier()) == null)
//                    .forEach(item -> annotationHolder.createErrorAnnotation(item, "Unknown user or group identifier"));
//        }
//        if (iconDefinitionElement.getFileExternalSize() != null && iconDefinitionElement.getFileExternalSize().getPropertyValue() != null) {
//            if (iconDefinitionElement.getFileExternalSize().getPropertyValue().getSize() < 0) {
//                annotationHolder.createErrorAnnotation(iconDefinitionElement.getFileExternalSize().getPropertyValue(), "External size must be positive!");
//            }
//        }
//    }

//    private void checkForReferences(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
//        if (iconDefinitionElement.getFileTasks() != null) {
//            iconDefinitionElement.getFileTasks().getPropertyValueList().stream()
//                    .filter(item -> item.getReference().resolve() == null)
//                    .forEach(item -> {
//                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced task");
//                        errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
//                    });
//        }
//        if (iconDefinitionElement.getFileComponents() != null) {
//            iconDefinitionElement.getFileComponents().getPropertyValueList().stream()
//                    .filter(item -> item.getReference().resolve() == null)
//                    .forEach(item -> {
//                        final Annotation errorAnnotation = annotationHolder.createErrorAnnotation(item, "Cannot find referenced component");
//                        errorAnnotation.setTextAttributes(IssHighlightingColorFactory.ANNOTATOR_ERROR_REFERENCE);
//                    });
//        }
//    }

    @Override
    protected void detectWarnings(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
//        checkForDoubleReferences(iconDefinitionElement, annotationHolder);
        if (iconDefinitionElement.getParentSection() != null && iconDefinitionElement.getName() != null) {
            final long count = iconDefinitionElement.getParentSection().getDefinitionList().stream()
                    .filter(item -> item != iconDefinitionElement)
                    .filter(item -> item.getName() != null)
                    .filter(item -> item.getName().equalsIgnoreCase(iconDefinitionElement.getName()))
                    .count();
            if (count > 0) {
                annotationHolder.createWarningAnnotation(iconDefinitionElement, "File with source '" + iconDefinitionElement.getName() + "' already defined");
            }
        }
//        checkForDoubleValues(iconDefinitionElement, annotationHolder);
    }

//    private void checkForDoubleValues(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
//        if (iconDefinitionElement.getFileFlags() != null) {
//            IssAnnotatorUtils.findDoubleValues(
//                    iconDefinitionElement.getFileFlags().getPropertyValueList(),
//                    element -> element.getName().toLowerCase(),
//                    (element, key) -> {
//                        annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
//                    }
//            );
//        }
//        if (iconDefinitionElement.getFileAttribute() != null) {
//            IssAnnotatorUtils.findDoubleValues(
//                    iconDefinitionElement.getFileAttribute().getPropertyValueList(),
//                    element -> element.getName().toLowerCase(),
//                    (element, key) -> {
//                        annotationHolder.createWarningAnnotation(element, "Attribute '" + key + "' already listed");
//                    }
//            );
//        }
//        if (iconDefinitionElement.getFilePermissions() != null) {
//            IssAnnotatorUtils.findDoubleValues(
//                    iconDefinitionElement.getFilePermissions().getPropertyValueList(),
//                    element -> element.getName().toLowerCase(),
//                    (element, key) -> {
//                        annotationHolder.createWarningAnnotation(element, "Permission '" + key + "' already listed");
//                    }
//            );
//        }
//    }
//
//    private void checkForDoubleReferences(@NotNull IssIconDefinitionElement iconDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
//        if (iconDefinitionElement.getFileTasks() != null) {
//            IssAnnotatorUtils.findDoubleValues(
//                    iconDefinitionElement.getFileTasks().getPropertyValueList(),
//                    element -> element.getName().toLowerCase(),
//                    (element, key) -> {
//                        annotationHolder.createWarningAnnotation(element, "Task '" + key + "' already listed");
//                    }
//            );
//        }
//        if (iconDefinitionElement.getFileComponents() != null) {
//            IssAnnotatorUtils.findDoubleValues(
//                    iconDefinitionElement.getFileComponents().getPropertyValueList(),
//                    element -> element.getName().toLowerCase(),
//                    (element, key) -> {
//                        annotationHolder.createWarningAnnotation(element, "Component '" + key + "' already listed");
//                    }
//            );
//        }
//    }
}
