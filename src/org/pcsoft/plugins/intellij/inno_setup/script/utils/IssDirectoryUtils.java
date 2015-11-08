package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory.IssDirectoryPropertyComponentsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory.IssDirectoryPropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory.IssDirectoryPropertyTasksValueElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public final class IssDirectoryUtils {

    public static IssDirectoryPropertyFlagsValueElement createFlagValue(final Project project, final String name) {
        final IssFile issFile = IssUtils.createFile(project, "[DIRS]\nFlags: " + name);
        return PsiTreeUtil.findChildOfType(issFile, IssDirectoryPropertyFlagsValueElement.class);
    }

    public static IssDirectoryPropertyTasksValueElement createTaskReference(final Project project, final String refName) {
        final IssFile issFile = IssUtils.createFile(project, "[DIRS]\nTasks: " + refName);
        return PsiTreeUtil.findChildOfType(issFile, IssDirectoryPropertyTasksValueElement.class);
    }

    public static IssDirectoryPropertyComponentsValueElement createComponentReference(final Project project, final String refName) {
        final IssFile issFile = IssUtils.createFile(project, "[DIRS]\nComponents: " + refName);
        return PsiTreeUtil.findChildOfType(issFile, IssDirectoryPropertyComponentsValueElement.class);
    }

    private IssDirectoryUtils() {
    }
}
