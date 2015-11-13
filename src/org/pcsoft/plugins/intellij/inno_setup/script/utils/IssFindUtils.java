package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssDefinableSectionIdentifier;

import java.util.Collection;

/**
 * Created by Christoph on 13.11.2015.
 */
public final class IssFindUtils {

    @Nullable
    public static <T extends IssPropertyElement> T findProperty(PsiElement element, IssDefinableSectionIdentifier propertyType, Class<T> propertyClass) {
        final Collection<T> collection = PsiTreeUtil.findChildrenOfType(element, propertyClass);
        return collection.stream()
                .filter(item -> item.getIdentifier() != null)
                .filter(item -> item.getIdentifier().getName().equals(propertyType.getId()))
                .findFirst().orElse(null);
    }

    @NotNull
    public static Collection<IssTaskDefinitionElement> findTaskDefinitionElements(final Project project) {
        return findTaskDefinitionElements(project, null, false);
    }

    @NotNull
    public static Collection<IssTaskDefinitionElement> findTaskDefinitionElements(final Project project, final String name, final boolean variant) {
        return IssUtils.findDefinitions(project, name, variant, IssTaskDefinitionElement.class,
                item -> item.getName() != null && !item.getName().trim().isEmpty(), IssTaskDefinitionElement::getName);
    }

    @NotNull
    public static Collection<IssTypeDefinitionElement> findTypeDefinitionElements(final Project project) {
        return findTypeDefinitionElements(project, null, false);
    }

    @NotNull
    public static Collection<IssTypeDefinitionElement> findTypeDefinitionElements(final Project project, final String name, final boolean variant) {
        return IssUtils.findDefinitions(project, name, variant, IssTypeDefinitionElement.class,
                item -> item.getName() != null && !item.getName().trim().isEmpty(), IssTypeDefinitionElement::getName);
    }

    @NotNull
    public static Collection<IssComponentDefinitionElement> findComponentDefinitionElements(final Project project) {
        return findComponentDefinitionElements(project, null, false);
    }

    @NotNull
    public static Collection<IssComponentDefinitionElement> findComponentDefinitionElements(final Project project, final String name, final boolean variant) {
        return IssUtils.findDefinitions(project, name, variant, IssComponentDefinitionElement.class,
                item -> item.getName() != null && !item.getName().trim().isEmpty(), IssComponentDefinitionElement::getName);
    }

    private IssFindUtils() {
    }
}
