package org.pcsoft.plugins.intellij.inno_setup.script.utils;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssScriptFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyComponentReferenceValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskReferenceValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTypeReferenceValueElement;

/**
 * Created by Christoph on 13.11.2015.
 */
public final class IssCreationUtils {

    public static IssPropertyTypeReferenceValueElement createTypeReferenceValueElement(final Project project, final String value) {
        final IssScriptFile issScriptFile = IssUtils.createFile(project, "[COMPONENTS]\nTypes: " + value);
        return PsiTreeUtil.findChildOfType(issScriptFile, IssPropertyTypeReferenceValueElement.class);
    }

    public static IssPropertyTaskReferenceValueElement createTaskReferenceValueElement(final Project project, final String refName) {
        final IssScriptFile issScriptFile = IssUtils.createFile(project, "[DIRS]\nTasks: " + refName);
        return PsiTreeUtil.findChildOfType(issScriptFile, IssPropertyTaskReferenceValueElement.class);
    }

    public static IssPropertyComponentReferenceValueElement createComponentReferenceValueElement(final Project project, final String refName) {
        final IssScriptFile issScriptFile = IssUtils.createFile(project, "[DIRS]\nComponents: " + refName);
        return PsiTreeUtil.findChildOfType(issScriptFile, IssPropertyComponentReferenceValueElement.class);
    }

    public static IssPropertyNameValueElement createNameValueElement(final Project project, final String name) {
        final IssScriptFile issScriptFile = IssUtils.createFile(project, "[COMPONENTS]\nName: " + name);
        return PsiTreeUtil.findChildOfType(issScriptFile, IssPropertyNameValueElement.class);
    }

    public static <T extends PsiElement>T createPropertyFor(final Project project, final String section, final String property, final Class<T> clazz) {
        final IssScriptFile issScriptFile = IssUtils.createFile(project, "[" + section + "]\n" + property);
        return PsiTreeUtil.findChildOfType(issScriptFile, clazz);
    }

    private IssCreationUtils() {
    }
}
