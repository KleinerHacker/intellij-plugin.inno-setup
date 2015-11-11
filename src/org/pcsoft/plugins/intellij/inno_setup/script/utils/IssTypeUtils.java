package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.StringUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyNameValueElement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 23.12.2014.
 */
public final class IssTypeUtils {

    public static IssTypePropertyFlagsValueElement createFlagValue(final Project project, final String name) {
        final IssFile issFile = IssUtils.createFile(project, "[TYPES]\nFlags: " + name);
        return PsiTreeUtil.findChildOfType(issFile, IssTypePropertyFlagsValueElement.class);
    }

    public static IssTypePropertyNameValueElement createNameElement(final Project project, final String name) {
        final IssFile issFile = IssUtils.createFile(project, "[TYPES]\nName: " + name);
        return PsiTreeUtil.findChildOfType(issFile, IssTypePropertyNameValueElement.class);
    }

    public static Collection<IssTypeDefinitionElement> findTypeDefinitions(final Project project) {
        return findTypeDefinitions(project, null, false);
    }

    public static Collection<IssTypeDefinitionElement> findTypeDefinitions(final Project project, final String name, final boolean variant) {
        final List<IssTypeDefinitionElement> list = new ArrayList<>();
        IssUtils.findFiles(project, file -> {
            final Collection<IssTypeDefinitionElement> typeDefinitionElements = PsiTreeUtil.findChildrenOfType(file, IssTypeDefinitionElement.class);
            if (name == null) {
                list.addAll(
                        typeDefinitionElements.stream()
                                .filter(item -> item.getName() != null)
                                .filter(item -> !item.getName().trim().isEmpty())
                                .collect(Collectors.toList())
                );
                return;
            }

            for (final IssTypeDefinitionElement typeDefinitionElement : typeDefinitionElements) {
                if (typeDefinitionElement.getName() == null || typeDefinitionElement.getName().trim().isEmpty())
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
