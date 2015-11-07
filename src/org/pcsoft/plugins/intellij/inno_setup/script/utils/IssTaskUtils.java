package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskPropertyComponentsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskPropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskPropertyNameValueElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Christoph on 23.12.2014.
 */
public final class IssTaskUtils {

    public static IssTaskPropertyFlagsValueElement createFlagValue(final Project project, final String name) {
        final IssFile issFile = IssUtils.createFile(project, "[TASKS]\nFlags: " + name);
        return PsiTreeUtil.findChildOfType(issFile, IssTaskPropertyFlagsValueElement.class);
    }

    public static IssTaskPropertyComponentsValueElement createComponentReference(final Project project, final String refName) {
        final IssFile issFile = IssUtils.createFile(project, "[TASKS]\nComponents: " + refName);
        return PsiTreeUtil.findChildOfType(issFile, IssTaskPropertyComponentsValueElement.class);
    }

    public static IssTaskPropertyNameValueElement createNameElement(final Project project, final String name) {
        final IssFile issFile = IssUtils.createFile(project, "[TASKS]\nName: " + name);
        return PsiTreeUtil.findChildOfType(issFile, IssTaskPropertyNameValueElement.class);
    }

    public static Collection<IssTaskDefinitionElement> findTaskDefinitions(final Project project) {
        return findTaskDefinitions(project, null, false);
    }

    public static Collection<IssTaskDefinitionElement> findTaskDefinitions(final Project project, final String name, final boolean variant) {
        final List<IssTaskDefinitionElement> list = new ArrayList<>();
        IssUtils.findFiles(project, file -> {
            final Collection<IssTaskDefinitionElement> taskDefinitionElements = PsiTreeUtil.findChildrenOfType(file, IssTaskDefinitionElement.class);
            if (name == null) {
                list.addAll(taskDefinitionElements);
                return;
            }

            for (final IssTaskDefinitionElement taskDefinitionElement : taskDefinitionElements) {
                if (taskDefinitionElement.getName() == null)
                    continue;

                if (variant) {
                    if (StringUtils.startsWithIgnoreCase(taskDefinitionElement.getName(), name)) {
                        list.add(taskDefinitionElement);
                    }
                } else {
                    if (taskDefinitionElement.getName().equalsIgnoreCase(name)) {
                        list.add(taskDefinitionElement);
                    }
                }
            }
        });
        return list;
    }

    private IssTaskUtils() {
    }
}
