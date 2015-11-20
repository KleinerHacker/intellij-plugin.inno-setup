package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyTaskFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.MultiResourceBundle;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssSectionTaskDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_task"), ResourceBundle.getBundle("/messages/documentation_common")
    );

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssPropertyNameValueElement && PsiTreeUtil.getParentOfType(element, IssTaskDefinitionElement.class) != null) {
            final IssPropertyNameValueElement nameValueElement = (IssPropertyNameValueElement) element;
            final IssTaskDefinitionElement taskDefinitionElement = PsiTreeUtil.getParentOfType(element, IssTaskDefinitionElement.class);
            return "Reference to component: " + nameValueElement.getName() + "<br/>" +
                    "Component Name: " + taskDefinitionElement.getTaskDescription().getPropertyValue().getText();
        }

        return null;
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
//        if (PsiTreeUtil.getParentOfType(element, IssTaskDefinitionElement.class) != null) {
//            return IssTaskUtils.createFlagValue(element.getProject(), object.toString());
//        }

        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssPropertyTaskFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_taskssection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierElement) {
            final IssIdentifierElement identifierElement = (IssIdentifierElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssTaskDefinitionElement.class) != null) {
                final IssTaskProperty taskProperty = IssTaskProperty.fromId(identifierElement.getName());
                if (taskProperty == null)
                    return "Unknown task property";

                return RESOURCE_BUNDLE.getString(taskProperty.getDescriptionKey());
            }
        } else if (element instanceof IssPropertyTaskFlagsValueElement) {
            final IssPropertyTaskFlagsValueElement taskDefinitionFlagsValueElement = (IssPropertyTaskFlagsValueElement) element;
            final IssTaskFlag taskFlag = IssTaskFlag.fromId(taskDefinitionFlagsValueElement.getName());
            if (taskFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(taskFlag.getDescriptionKey());
        }

        return null;
    }
}
