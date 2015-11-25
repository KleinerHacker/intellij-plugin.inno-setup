package org.pcsoft.plugins.intellij.inno_setup.script.references.marker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyDefaultElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssCustomMessageSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssMessageSectionElement;

import javax.swing.*;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

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
                final IssIdentifierElement identifierElement = PsiTreeUtil.findChildOfType(psiElement, IssIdentifierElement.class);
                if (identifierElement != null && identifierElement.getName().contains(".")) {
                    final String lan = identifierElement.getName().substring(0, identifierElement.getName().indexOf("."));
                    final Locale locale = Locale.forLanguageTag(lan);
                    final Icon lanIcon = IssIcons.findIconByLocale(locale);

                    if (lanIcon != null) {
                        collection.add(
                                NavigationGutterIconBuilder.create(lanIcon)
                                        .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                                        .setTarget(psiElement)
                                        .setTooltipText(locale.getDisplayName())
                                        .createLineMarkerInfo(psiElement)
                        );
                    }
                }
            }
        }
    }
}
