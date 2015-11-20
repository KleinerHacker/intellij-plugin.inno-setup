package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssDefinablePropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyComponentReferenceValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyTaskReferenceValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyTypeReferenceValueElement;

/**
 * Created by Christoph on 05.01.2015.
 */
public class IssSectionReferenceDocumentationProvider extends AbstractDocumentationProvider {

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (originalElement instanceof IssPropertyTaskReferenceValueElement) {
            final IssPropertyTaskReferenceValueElement namedValueElement = (IssPropertyTaskReferenceValueElement) originalElement;
            if (namedValueElement.getReference().resolve() == null)
                return "Task reference not found";
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append("Reference to task '<b>").append(namedValueElement.getReference().resolve().getText()).append("</b>'<br/>");
                sb.append("<hr/>");
                sb.append(generateDoc(element, originalElement));

                return sb.toString();
            }
        } else if (originalElement instanceof IssPropertyComponentReferenceValueElement) {
            final IssPropertyComponentReferenceValueElement namedValueElement = (IssPropertyComponentReferenceValueElement) originalElement;
            if (namedValueElement.getReference().resolve() == null)
                return "Component reference not found";
            else {
                final StringBuilder sb = new StringBuilder();
                sb.append("Reference to component '<b>").append(namedValueElement.getReference().resolve().getText()).append("</b>'<br/>");
                sb.append("<hr/>");
                sb.append(generateDoc(element, originalElement));

                return sb.toString();
            }
        } else if (originalElement instanceof IssPropertyTypeReferenceValueElement) {
            final IssPropertyTypeReferenceValueElement namedValueElement = (IssPropertyTypeReferenceValueElement) originalElement;
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
        if (element instanceof IssPropertyNameValueElement) {
            final IssPropertyNameValueElement nameValueElement = (IssPropertyNameValueElement) element;
            if (nameValueElement.getParent() == null || !(nameValueElement.getParent() instanceof IssPropertyElement) ||
                    ((IssDefinablePropertyElement)nameValueElement.getParent()).getDefinition() == null)
                return null;

            return buildDocFromComment(((IssDefinablePropertyElement)nameValueElement.getParent()).getDefinition());
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
