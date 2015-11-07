package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyComponentsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyTasksValueElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public final class IssFileUtils {

    public static IssFilePropertyFlagsValueElement createFlagValue(final Project project, final String name) {
        final IssFile issFile = IssUtils.createFile(project, "[FILES]\nFlags: " + name);
        return PsiTreeUtil.findChildOfType(issFile, IssFilePropertyFlagsValueElement.class);
    }

    public static IssFilePropertyTasksValueElement createTaskReference(final Project project, final String refName) {
        final IssFile issFile = IssUtils.createFile(project, "[FILES]\nTasks: " + refName);
        return PsiTreeUtil.findChildOfType(issFile, IssFilePropertyTasksValueElement.class);
    }

    public static IssFilePropertyComponentsValueElement createComponentReference(final Project project, final String refName) {
        final IssFile issFile = IssUtils.createFile(project, "[FILES]\nComponents: " + refName);
        return PsiTreeUtil.findChildOfType(issFile, IssFilePropertyComponentsValueElement.class);
    }

    private IssFileUtils() {
    }
}
