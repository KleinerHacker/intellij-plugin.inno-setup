package org.pcsoft.plugins.intellij.inno_setup.script.others;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionTasksValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionNameValueElement;

/**
 * Created with IntelliJ IDEA.
 * User: pfeifchr
 * Date: 17.07.13
 * Time: 14:44
 * To change this template use File | Settings | File Templates.
 */
public final class IssRefactoringSupportProvider extends RefactoringSupportProvider {

    @Override
    public boolean isMemberInplaceRenameAvailable(PsiElement element, PsiElement context) {
        return element instanceof IssFileDefinitionTasksValueElement || element instanceof IssTaskDefinitionNameValueElement ||
                element instanceof IssComponentDefinitionNameValueElement;
    }
}
