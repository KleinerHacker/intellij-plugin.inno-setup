package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.section;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssINIDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssINIFlag;

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
        checkForKnownValues(annotationHolder, iniDefinitionElement::getINIFlags, p -> IssINIFlag.fromId(p.getName()) == null, "Unknown flag");
    }

    private void checkForReferences(@NotNull IssINIDefinitionElement iniDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(annotationHolder, iniDefinitionElement::getINITaskReference, ERROR_REFERENCE_TASK);
        checkForReferences(annotationHolder, iniDefinitionElement::getINIComponentReference, ERROR_REFERENCE_COMPONENT);
    }

    @Override
    protected void detectWarnings(@NotNull IssINIDefinitionElement iniDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(iniDefinitionElement, annotationHolder);
        checkForDoubleValues(iniDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssINIDefinitionElement iniDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleValues(annotationHolder, iniDefinitionElement::getINIFlags, "Flag '%s' already listed");
    }

    private void checkForDoubleReferences(@NotNull IssINIDefinitionElement iniDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(annotationHolder, iniDefinitionElement::getINITaskReference, WARN_REFERENCE_TASK);
        checkForDoubleReferences(annotationHolder, iniDefinitionElement::getINIComponentReference, WARN_REFERENCE_COMPONENT);
    }
}
