package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.annotator.section;

import com.intellij.lang.annotation.AnnotationHolder;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssLanguageDefinitionElement;

/**
 * Created by Christoph on 08.11.2015.
 */
public class IssSectionLanguageAnnotator extends IssAbstractSectionAnnotator<IssLanguageDefinitionElement> {

    public IssSectionLanguageAnnotator() {
        super(IssLanguageDefinitionElement.class, DoubletCheckType.Error);
    }

    @Override
    protected void detectErrors(@NotNull IssLanguageDefinitionElement languageDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForReferences(languageDefinitionElement, annotationHolder);
        checkForKnownValues(languageDefinitionElement, annotationHolder);
    }

    private void checkForKnownValues(@NotNull IssLanguageDefinitionElement languageDefinitionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    private void checkForReferences(@NotNull IssLanguageDefinitionElement languageDefinitionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    @Override
    protected void detectWarnings(@NotNull IssLanguageDefinitionElement languageDefinitionElement, @NotNull AnnotationHolder annotationHolder) {
        checkForDoubleReferences(languageDefinitionElement, annotationHolder);
        checkForDoubleValues(languageDefinitionElement, annotationHolder);
    }

    private void checkForDoubleValues(@NotNull IssLanguageDefinitionElement languageDefinitionElement, @NotNull AnnotationHolder annotationHolder) {

    }

    private void checkForDoubleReferences(@NotNull IssLanguageDefinitionElement languageDefinitionElement, @NotNull AnnotationHolder annotationHolder) {

    }
}
