package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionTypesValueElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Christoph on 28.12.2014.
 */
public final class IssComponentUtils {

    public static IssComponentDefinitionFlagsValueElement createFlagValue(final Project project, final String name) {
        final IssFile issFile = IssUtils.createFile(project, "[COMPONENTS]\nFlags: " + name);
        return PsiTreeUtil.findChildOfType(issFile, IssComponentDefinitionFlagsValueElement.class);
    }

    public static IssComponentDefinitionTypesValueElement createTypesValueElement(final Project project, final String value) {
        final IssFile issFile = IssUtils.createFile(project, "[COMPONENTS]\nTypes: " + value);
        return PsiTreeUtil.findChildOfType(issFile, IssComponentDefinitionTypesValueElement.class);
    }

    public static IssComponentDefinitionNameValueElement createNameElement(final Project project, final String name) {
        final IssFile issFile = IssUtils.createFile(project, "[COMPONENTS]\nName: " + name);
        return PsiTreeUtil.findChildOfType(issFile, IssComponentDefinitionNameValueElement.class);
    }

    public static Collection<IssComponentDefinitionElement> findComponentDefinitions(final Project project) {
        return findComponentDefinitions(project, null, false);
    }

    public static Collection<IssComponentDefinitionElement> findComponentDefinitions(final Project project, final String name, final boolean variant) {
        final List<IssComponentDefinitionElement> list = new ArrayList<>();
        IssUtils.findFiles(project, file -> {
            final Collection<IssComponentDefinitionElement> componentDefinitionElements = PsiTreeUtil.findChildrenOfType(file, IssComponentDefinitionElement.class);
            if (name == null) {
                list.addAll(componentDefinitionElements);
                return;
            }

            for (final IssComponentDefinitionElement componentDefinitionElement : componentDefinitionElements) {
                if (componentDefinitionElement.getName() == null)
                    continue;

                if (variant) {
                    if (StringUtils.startsWithIgnoreCase(componentDefinitionElement.getName(), name)) {
                        list.add(componentDefinitionElement);
                    }
                } else {
                    if (componentDefinitionElement.getName().equalsIgnoreCase(name)) {
                        list.add(componentDefinitionElement);
                    }
                }
            }
        });
        return list;
    }

    private IssComponentUtils() {
    }
}
