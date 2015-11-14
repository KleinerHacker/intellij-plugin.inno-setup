package org.pcsoft.plugins.intellij.inno_setup.script.references.marker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssComponentSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssDirectorySectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssFileSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssIconSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.setup.IssSetupSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssTaskSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssTypeSectionElement;

import java.util.Collection;
import java.util.List;

/**
 * Created by Christoph on 10.11.2015.
 */
public class IssOverviewLineMarkerProvider implements LineMarkerProvider {
    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> list, @NotNull Collection<LineMarkerInfo> collection) {
        for (final PsiElement psiElement : list) {
            if (psiElement instanceof IssSectionNameElement && psiElement.getParent() instanceof IssSetupSectionElement) {
                collection.add(
                        NavigationGutterIconBuilder.create(IssIcons.IC_SECT_SETUP)
                                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                                .setTarget(psiElement)
                                .setTooltipText("SETUP Section")
                                .createLineMarkerInfo(psiElement)
                );
            } else if (psiElement instanceof IssSectionNameElement && psiElement.getParent() instanceof IssFileSectionElement) {
                collection.add(
                        NavigationGutterIconBuilder.create(IssIcons.IC_SECT_FILE)
                                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                                .setTarget(psiElement)
                                .setTooltipText("FILES Section")
                                .createLineMarkerInfo(psiElement)
                );
            } else if (psiElement instanceof IssSectionNameElement && psiElement.getParent() instanceof IssDirectorySectionElement) {
                collection.add(
                        NavigationGutterIconBuilder.create(IssIcons.IC_SECT_DIRECTORY)
                                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                                .setTarget(psiElement)
                                .setTooltipText("DIRS Section")
                                .createLineMarkerInfo(psiElement)
                );
            } else if (psiElement instanceof IssSectionNameElement && psiElement.getParent() instanceof IssTypeSectionElement) {
                collection.add(
                        NavigationGutterIconBuilder.create(IssIcons.IC_SECT_TYPE)
                                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                                .setTarget(psiElement)
                                .setTooltipText("TYPES Section")
                                .createLineMarkerInfo(psiElement)
                );
            } else if (psiElement instanceof IssSectionNameElement && psiElement.getParent() instanceof IssTaskSectionElement) {
                collection.add(
                        NavigationGutterIconBuilder.create(IssIcons.IC_SECT_TASK)
                                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                                .setTarget(psiElement)
                                .setTooltipText("TASKS Section")
                                .createLineMarkerInfo(psiElement)
                );
            } else if (psiElement instanceof IssSectionNameElement && psiElement.getParent() instanceof IssComponentSectionElement) {
                collection.add(
                        NavigationGutterIconBuilder.create(IssIcons.IC_SECT_COMPONENT)
                                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                                .setTarget(psiElement)
                                .setTooltipText("COMPONENTS Section")
                                .createLineMarkerInfo(psiElement)
                );
            } else if (psiElement instanceof IssSectionNameElement && psiElement.getParent() instanceof IssIconSectionElement) {
                collection.add(
                        NavigationGutterIconBuilder.create(IssIcons.IC_SECT_ICON)
                                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                                .setTarget(psiElement)
                                .setTooltipText("ICONS Section")
                                .createLineMarkerInfo(psiElement)
                );
            }
        }
    }
}
