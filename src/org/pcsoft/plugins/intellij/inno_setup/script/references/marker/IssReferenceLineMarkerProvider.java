package org.pcsoft.plugins.intellij.inno_setup.script.references.marker;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.codeInsight.navigation.NavigationGutterIconBuilder;
import com.intellij.openapi.editor.markup.GutterIconRenderer;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyComponentReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyTaskReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyTypeReferenceElement;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssReferenceLineMarkerProvider implements LineMarkerProvider {
    @Nullable
    @Override
    public LineMarkerInfo getLineMarkerInfo(@NotNull PsiElement psiElement) {
        return null;
    }

    @Override
    public void collectSlowLineMarkers(@NotNull List<PsiElement> list, @NotNull Collection<LineMarkerInfo> collection) {
        for (final PsiElement element : list) {
            if (!(element instanceof IssPropertyNameElement))
                continue;

            final IssPropertyNameElement propertyNameElement = (IssPropertyNameElement) element;
            if (propertyNameElement.getDefinition() instanceof IssTaskDefinitionElement) {
                final IssTaskDefinitionElement taskDefinitionElement = (IssTaskDefinitionElement) propertyNameElement.getDefinition();

                final LineMarkerInfo lineMarkerInfo = handleTaskDefinition(taskDefinitionElement);
                collection.add(lineMarkerInfo);
            } else if (propertyNameElement.getDefinition() instanceof IssTypeDefinitionElement) {
                final IssTypeDefinitionElement typeDefinitionElement = (IssTypeDefinitionElement) propertyNameElement.getDefinition();

                final LineMarkerInfo lineMarkerInfo = handleTypeDefinition(typeDefinitionElement);
                collection.add(lineMarkerInfo);
            } else if (propertyNameElement.getDefinition() instanceof IssComponentDefinitionElement) {
                final IssComponentDefinitionElement componentDefinitionElement = (IssComponentDefinitionElement) propertyNameElement.getDefinition();

                final LineMarkerInfo lineMarkerInfo = handleComponentDefinition(componentDefinitionElement);
                collection.add(lineMarkerInfo);
            }
        }
    }

    private LineMarkerInfo handleComponentDefinition(IssComponentDefinitionElement componentDefinitionElement) {
        final Collection<IssPropertyComponentReferenceElement> componentReferenceElementList =
                IssFindUtils.findComponentReferenceElements(componentDefinitionElement.getProject());
        final List<IssDefinitionElement> definitionElementList = componentReferenceElementList.stream()
                .filter(item -> item.getDefinition() != null)
                .map(IssDefinablePropertyElement::getDefinition)
                .collect(Collectors.toList());

        return NavigationGutterIconBuilder.create(IssIcons.IC_REF_COMPONENT)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTargets(definitionElementList)
                .setTooltipText("Component References")
                .createLineMarkerInfo(componentDefinitionElement);
    }

    private LineMarkerInfo handleTypeDefinition(IssTypeDefinitionElement typeDefinitionElement) {
        final Collection<IssPropertyTypeReferenceElement> typeReferenceElementList =
                IssFindUtils.findTypeReferenceElements(typeDefinitionElement.getProject());
        final List<IssDefinitionElement> definitionElementList = typeReferenceElementList.stream()
                .filter(item -> item.getDefinition() != null)
                .map(IssDefinablePropertyElement::getDefinition)
                .collect(Collectors.toList());

        return NavigationGutterIconBuilder.create(IssIcons.IC_REF_TYPE)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTargets(definitionElementList)
                .setTooltipText("Type References")
                .createLineMarkerInfo(typeDefinitionElement);
    }

    private LineMarkerInfo handleTaskDefinition(IssTaskDefinitionElement taskDefinitionElement) {
        final Collection<IssPropertyTaskReferenceElement> taskReferenceElementList =
                IssFindUtils.findTaskReferenceElements(taskDefinitionElement.getProject());
        final List<IssDefinitionElement> definitionElementList = taskReferenceElementList.stream()
                .filter(item -> item.getDefinition() != null)
                .map(IssDefinablePropertyElement::getDefinition)
                .collect(Collectors.toList());

        return NavigationGutterIconBuilder.create(IssIcons.IC_REF_TASK)
                .setAlignment(GutterIconRenderer.Alignment.RIGHT)
                .setTargets(definitionElementList)
                .setTooltipText("Task References")
                .createLineMarkerInfo(taskDefinitionElement);
    }
}
