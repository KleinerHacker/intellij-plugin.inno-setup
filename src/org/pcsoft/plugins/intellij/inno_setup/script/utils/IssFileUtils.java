package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionComponentsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionTasksValueElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public final class IssFileUtils {

    public static IssFileDefinitionFlagsValueElement createFlagValue(final Project project, final String name) {
        final IssFile issFile = IssUtils.createFile(project, "[FILES]\nFlags: " + name);
        return PsiTreeUtil.findChildOfType(issFile, IssFileDefinitionFlagsValueElement.class);
    }

    public static IssFileDefinitionTasksValueElement createTaskReference(final Project project, final String refName) {
        final IssFile issFile = IssUtils.createFile(project, "[FILES]\nTasks: " + refName);
        return PsiTreeUtil.findChildOfType(issFile, IssFileDefinitionTasksValueElement.class);
    }

    public static IssFileDefinitionComponentsValueElement createComponentReference(final Project project, final String refName) {
        final IssFile issFile = IssUtils.createFile(project, "[FILES]\nComponents: " + refName);
        return PsiTreeUtil.findChildOfType(issFile, IssFileDefinitionComponentsValueElement.class);
    }

    private IssFileUtils() {
    }
}
