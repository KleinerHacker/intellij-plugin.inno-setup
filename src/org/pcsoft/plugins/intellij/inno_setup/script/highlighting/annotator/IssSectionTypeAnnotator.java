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
        super(IssTypeDefinitionElement.class, DoubletCheckType.Error);
    }

    @Override
    protected void detectErrors(@NotNull IssTypeDefinitionElement typeDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForKnownValues(typeDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssTypeDefinitionElement typeDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
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
        checkForDoubleReferences(typeDefinitionElement, annotationHolder);
    }

    private void checkForDoubleReferences(@NotNull IssTypeDefinitionElement typeDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        if (typeDefinitionElement.getTypeFlags() != null) {
            IssAnnotatorUtils.findDoubleValues(
                    typeDefinitionElement.getTypeFlags().getPropertyValueList(),
                    element -> element.getName().toLowerCase(),
                    (element, key) -> {
                        annotationHolder.createWarningAnnotation(element, "Flag '" + key + "' already listed");
                    }
            );
        }
    }
}
