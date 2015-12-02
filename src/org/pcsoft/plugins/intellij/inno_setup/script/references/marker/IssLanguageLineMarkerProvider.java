package org.pcsoft.plugins.intellij.inno_setup.script.references.marker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssLanguageDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyDefaultElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssCustomMessageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssMessageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssLanguageType;

import java.util.Collection;
import java.util.List;

/**
 * Created by Christoph on 22.11.2015.
 */
public class IssLanguageLineMarkerProvider implements LineMarkerProvider {

    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> list, @NotNull Collection<LineMarkerInfo> collection) {
        for (final PsiElement psiElement : list) {
            if (psiElement instanceof IssPropertyDefaultElement && (psiElement.getParent() instanceof IssCustomMessageSectionElement ||
                    psiElement.getParent() instanceof IssMessageSectionElement)) {
                final IssIdentifierReferenceElement identifierReferenceElement = PsiTreeUtil.findChildOfType(psiElement, IssIdentifierReferenceElement.class);
                if (identifierReferenceElement != null && identifierReferenceElement.getReference() != null && identifierReferenceElement.getReference().resolve() != null) {
                    final PsiElement resolvedElement = identifierReferenceElement.getReference().resolve();
                    final IssLanguageDefinitionElement languageDefinitionElement = PsiTreeUtil.getParentOfType(resolvedElement, IssLanguageDefinitionElement.class);
                    if (languageDefinitionElement == null)
                        continue;
                    final IssLanguageType languageType = languageDefinitionElement.getLanguageType();

                    if (languageType != null) {
                        collection.add(
                                NavigationGutterIconBuilder.create(languageType.getFlagIcon())
                                        .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                                        .setTarget(languageDefinitionElement)
                                        .setTooltipText(languageType.getName())
                                        .createLineMarkerInfo(psiElement)
                        );
                    }
                }
            } else if (psiElement instanceof IssLanguageDefinitionElement) {
                final IssLanguageDefinitionElement languageDefinitionElement = (IssLanguageDefinitionElement) psiElement;
                final IssLanguageType languageType = languageDefinitionElement.getLanguageType();

                if (languageType != null) {
                    collection.add(
                            NavigationGutterIconBuilder.create(languageType.getFlagIcon())
                                    .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                                    .setTarget(psiElement)
                                    .setTooltipText(languageType.getName())
                                    .createLineMarkerInfo(psiElement)
                    );
                }
            }
        }
    }
}
