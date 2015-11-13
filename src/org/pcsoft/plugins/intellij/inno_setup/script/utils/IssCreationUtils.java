package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyComponentReferenceValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskReferenceValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTypeReferenceValueElement;

/**
 * Created by Christoph on 13.11.2015.
 */
public final class IssCreationUtils {

    public static IssPropertyTypeReferenceValueElement createTypeReferenceValueElement(final Project project, final String value) {
        final IssFile issFile = IssUtils.createFile(project, "[COMPONENTS]\nTypes: " + value);
        return PsiTreeUtil.findChildOfType(issFile, IssPropertyTypeReferenceValueElement.class);
    }

    public static IssPropertyTaskReferenceValueElement createTaskReferenceValueElement(final Project project, final String refName) {
        final IssFile issFile = IssUtils.createFile(project, "[DIRS]\nTasks: " + refName);
        return PsiTreeUtil.findChildOfType(issFile, IssPropertyTaskReferenceValueElement.class);
    }

    public static IssPropertyComponentReferenceValueElement createComponentReferenceValueElement(final Project project, final String refName) {
        final IssFile issFile = IssUtils.createFile(project, "[DIRS]\nComponents: " + refName);
        return PsiTreeUtil.findChildOfType(issFile, IssPropertyComponentReferenceValueElement.class);
    }

    public static IssPropertyNameValueElement createNameValueElement(final Project project, final String name) {
        final IssFile issFile = IssUtils.createFile(project, "[COMPONENTS]\nName: " + name);
        return PsiTreeUtil.findChildOfType(issFile, IssPropertyNameValueElement.class);
    }

    private IssCreationUtils() {
    }
}
