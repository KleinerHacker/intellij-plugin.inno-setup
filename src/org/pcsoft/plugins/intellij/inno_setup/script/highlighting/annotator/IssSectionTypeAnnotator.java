package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssTypeDefinitionElement;
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
        checkForKnownValues(annotationHolder, typeDefinitionElement::getTypeFlags, p -> IssTypeFlag.fromId(p.getName()) == null, "Unknown flag");
    }

    @Override
    protected void detectWarnings(@NotNull IssTypeDefinitionElement typeDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(typeDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssTypeDefinitionElement typeDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(annotationHolder, typeDefinitionElement::getTypeFlags, "Flag '%s' already listed");
    }
}
