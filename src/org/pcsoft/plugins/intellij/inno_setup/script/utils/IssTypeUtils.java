package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionNameValueElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Christoph on 23.12.2014.
 */
public final class IssTypeUtils {

    public static IssTypeDefinitionFlagsValueElement createFlagValue(final Project project, final String name) {
        final IssFile issFile = IssUtils.createFile(project, "[TYPES]\nFlags: " + name);
        return PsiTreeUtil.findChildOfType(issFile, IssTypeDefinitionFlagsValueElement.class);
    }

    public static IssTypeDefinitionNameValueElement createNameElement(final Project project, final String name) {
        final IssFile issFile = IssUtils.createFile(project, "[TYPES]\nName: " + name);
        return PsiTreeUtil.findChildOfType(issFile, IssTypeDefinitionNameValueElement.class);
    }

    public static Collection<IssTypeDefinitionElement> findTypeDefinitions(final Project project) {
        return findTypeDefinitions(project, null, false);
    }

    public static Collection<IssTypeDefinitionElement> findTypeDefinitions(final Project project, final String name, final boolean variant) {
        final List<IssTypeDefinitionElement> list = new ArrayList<>();
        IssUtils.findFiles(project, file -> {
            final Collection<IssTypeDefinitionElement> typeDefinitionElements = PsiTreeUtil.findChildrenOfType(file, IssTypeDefinitionElement.class);
            if (name == null) {
                list.addAll(typeDefinitionElements);
                return;
            }

            for (final IssTypeDefinitionElement typeDefinitionElement : typeDefinitionElements) {
                if (typeDefinitionElement.getName() == null)
                    continue;

                if (variant) {
                    if (StringUtils.startsWithIgnoreCase(typeDefinitionElement.getName(), name)) {
                        list.add(typeDefinitionElement);
                    }
                } else {
                    if (typeDefinitionElement.getName().equalsIgnoreCase(name)) {
                        list.add(typeDefinitionElement);
                    }
                }
            }
        });
        return list;
    }

    private IssTypeUtils() {
    }
}
