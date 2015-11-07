package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeProperty;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 06.01.2015.
 */
public class IssAttributeDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("/messages/documentation_attribute");

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierElement) {
            if (PsiTreeUtil.getParentOfType(element, IssTypeDefinitionElement.class) != null) {
                return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_typessection.htm");
            } else if (PsiTreeUtil.getParentOfType(element, IssComponentDefinitionElement.class) != null) {
                return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_componentssection.htm");
            } else if (PsiTreeUtil.getParentOfType(element, IssTaskDefinitionElement.class) != null) {
                return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_taskssection.htm");
            } else if (PsiTreeUtil.getParentOfType(element, IssFileDefinitionElement.class) != null) {
                return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_filessection.htm");
            }
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierElement) {
            final IssIdentifierElement identifierElement = (IssIdentifierElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssTypeDefinitionElement.class) != null) {
                final IssTypeProperty typeAttribute = IssTypeProperty.fromId(identifierElement.getName());
                if (typeAttribute == null)
                    return "Unknown type attribute";

                return RESOURCE_BUNDLE.getString(typeAttribute.getDescriptionKey());
            } else if (PsiTreeUtil.getParentOfType(element, IssComponentDefinitionElement.class) != null) {
                final IssComponentProperty componentAttribute = IssComponentProperty.fromId(identifierElement.getName());
                if (componentAttribute == null)
                    return "Unknown component attribute";

                return RESOURCE_BUNDLE.getString(componentAttribute.getDescriptionKey());
            } else if (PsiTreeUtil.getParentOfType(element, IssTaskDefinitionElement.class) != null) {
                final IssTaskProperty taskAttribute = IssTaskProperty.fromId(identifierElement.getName());
                if (taskAttribute == null)
                    return "Unknown task attribute";

                return RESOURCE_BUNDLE.getString(taskAttribute.getDescriptionKey());
            } else if (PsiTreeUtil.getParentOfType(element, IssFileDefinitionElement.class) != null) {
                final IssFileProperty fileAttribute = IssFileProperty.fromId(identifierElement.getName());
                if (fileAttribute == null)
                    return "Unknown file attribute";

                return RESOURCE_BUNDLE.getString(fileAttribute.getDescriptionKey());
            }
        }

        return null;
    }
}
