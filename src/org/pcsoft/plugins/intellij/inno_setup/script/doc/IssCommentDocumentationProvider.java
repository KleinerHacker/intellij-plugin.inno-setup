package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiNamedElement;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentPropertyTypesValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyComponentsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyTasksValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskPropertyComponentsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyNameValueElement;

/**
 * Created by Christoph on 05.01.2015.
 */
public class IssCommentDocumentationProvider extends AbstractDocumentationProvider {

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (originalElement instanceof IssFilePropertyTasksValueElement) {
            final PsiNamedElement namedValueElement = (PsiNamedElement) originalElement;
            if (namedValueElement.getReference().resolve() == null)
                return "Task reference not found";
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append("Reference to task '<b>").append(namedValueElement.getReference().resolve().getText()).append("</b>'<br/>");
                sb.append("<hr/>");
                sb.append(generateDoc(element, originalElement));

                return sb.toString();
            }
        } else if (originalElement instanceof IssFilePropertyComponentsValueElement ||
                originalElement instanceof IssTaskPropertyComponentsValueElement) {
            final PsiNamedElement namedValueElement = (PsiNamedElement) originalElement;
            if (namedValueElement.getReference().resolve() == null)
                return "Component reference not found";
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append("Reference to component '<b>").append(namedValueElement.getReference().resolve().getText()).append("</b>'<br/>");
                sb.append("<hr/>");
                sb.append(generateDoc(element, originalElement));

                return sb.toString();
            }
        } else if (originalElement instanceof IssComponentPropertyTypesValueElement) {
            final PsiNamedElement namedValueElement = (PsiNamedElement) originalElement;
            if (namedValueElement.getReference().resolve() == null)
                return "Type reference not found";
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append("Reference to type '<b>").append(namedValueElement.getReference().resolve().getText()).append("</b>'<br/>");
                sb.append("<hr/>");
                sb.append(generateDoc(element, originalElement));

                return sb.toString();
            }
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssTaskPropertyNameValueElement) {
            final IssTaskPropertyNameValueElement nameValueElement = (IssTaskPropertyNameValueElement) element;
            if (nameValueElement.getValueParent() == null || nameValueElement.getValueParent().getDefinition() == null)
                return null;

            return buildDocFromComment(nameValueElement.getValueParent().getDefinition());
        } else if (element instanceof IssComponentPropertyNameValueElement) {
            final IssComponentPropertyNameValueElement nameValueElement = (IssComponentPropertyNameValueElement) element;
            if (nameValueElement.getValueParent() == null || nameValueElement.getValueParent().getDefinition() == null)
                return null;

            return buildDocFromComment(nameValueElement.getValueParent().getDefinition());
        } else if (element instanceof IssTypePropertyNameValueElement) {
            final IssTypePropertyNameValueElement nameValueElement = (IssTypePropertyNameValueElement) element;
            if (nameValueElement.getValueParent() == null || nameValueElement.getValueParent().getDefinition() == null)
                return null;

            return buildDocFromComment(nameValueElement.getValueParent().getDefinition());
        }

        return null;
    }

    private static String buildDocFromComment(PsiElement element) {
        if (element.getPrevSibling() instanceof PsiComment) {
            final StringBuilder sb = new StringBuilder();

            PsiElement current = element.getPrevSibling();
            while (current != null && current instanceof PsiComment) {
                sb.insert(0, current.getText().substring(1) + " ");
                current = current.getPrevSibling();
            }

            return sb.toString();
        }

        return null;
    }
}
