package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeFlag;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionTypeAnnotator extends IssAbstractSectionAnnotator<IssTypeDefinitionElement> {

    public IssSectionTypeAnnotator() {
        super(IssTypeDefinitionElement.class);
    }

    @Override
    protected void detectErrors(@NotNull IssTypeDefinitionElement typeDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (typeDefinitionElement.getTypeName() == null) {
            annotationHolder.createErrorAnnotation(typeDefinitionElement, "Missing required section item 'Name'");
        }
        if (typeDefinitionElement.getTypeDescription() == null) {
            annotationHolder.createErrorAnnotation(typeDefinitionElement, "Missing required section item 'Description'");
        }
//        if (typeDefinitionElement.getParentSection() != null && typeDefinitionElement.getName() != null) {
//            final long count = typeDefinitionElement.getParentSection().getDefinitionList().stream()
//                    .filter(item -> item != typeDefinitionElement)
//                    .filter(item -> item.getName() != null)
//                    .filter(item -> item.getName().equals(typeDefinitionElement.getName()))
//                    .count();
//            if (count > 0) {
//                annotationHolder.createErrorAnnotation(typeDefinitionElement, "Task with name '" + typeDefinitionElement.getName() + "' already defined");
//            }
//        }
        if (typeDefinitionElement.getTypeFlags() != null) {
            typeDefinitionElement.getTypeFlags().getPropertyValueList().stream()
                    .filter(item -> IssTypeFlag.fromId(item.getName()) == null)
                    .forEach(item -> {
                        annotationHolder.createErrorAnnotation(item, "Unknown flag");
                    });
        }
    }

    @Override
    protected void detectWarnings(@NotNull IssTypeDefinitionElement typeDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (typeDefinitionElement.getTypeFlags() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    typeDefinitionElement.getTypeFlags().getPropertyValueList(),
                    element -> element.getName(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                    }
            );
        }
    }
}
