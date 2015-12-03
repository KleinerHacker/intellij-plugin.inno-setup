package org.pcsoft.plugins.intellij.inno_setup.script.references.marker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.daemon.RelatedItemLineMarkerInfo;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.common.IssSectionHeaderElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;

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
        list.stream()
                .filter(psiElement -> psiElement instanceof IssSectionHeaderElement && psiElement.getParent() instanceof IssSectionElement)
                .forEach(psiElement -> {
                    final RelatedItemLineMarkerInfo<PsiElement> markerInfo = buildMarker((IssSectionElement) psiElement.getParent(), psiElement);
                    if (markerInfo != null) {
                        collection.add(markerInfo);
                    }
                });
    }

    @Nullable
    private RelatedItemLineMarkerInfo<PsiElement> buildMarker(IssSectionElement sectionElement, PsiElement baseElement) {
        final IssSectionType sectionType = sectionElement.getSectionType();
        if (sectionType == null || sectionType.getIcon() == null)
            return null;

        return NavigationGutterIconBuilder.create(sectionType.getIcon())
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTarget(baseElement)
                .setTooltipText(sectionType.getId() + " Section")
                .createLineMarkerInfo(baseElement);
    }
}
