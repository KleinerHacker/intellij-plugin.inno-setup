package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyComponentFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.MultiResourceBundle;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssSectionComponentDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_component"), ResourceBundle.getBundle("/messages/documentation_common")
    );

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssPropertyNameValueElement && PsiTreeUtil.getParentOfType(element, IssComponentDefinitionElement.class) != null) {
            final IssPropertyNameValueElement nameValueElement = (IssPropertyNameValueElement) element;
            final IssComponentDefinitionElement componentDefinitionElement = PsiTreeUtil.getParentOfType(element, IssComponentDefinitionElement.class);
            return "Reference to component: " + nameValueElement.getName() + "<br/>" +
                    "Component Name: " + componentDefinitionElement.getComponentDescription().getPropertyValue().getText();
        }

        return null;
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
//        if (PsiTreeUtil.getParentOfType(element, IssComponentDefinitionElement.class) != null) {
//            return IssComponentUtils.createFlagValue(element.getProject(), object.toString());
//        }

        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssPropertyComponentFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_componentssection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierElement) {
            final IssIdentifierElement identifierElement = (IssIdentifierElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssComponentDefinitionElement.class) != null) {
                final IssComponentProperty componentProperty = IssComponentProperty.fromId(identifierElement.getName());
                if (componentProperty == null)
                    return "Unknown component property";

                return RESOURCE_BUNDLE.getString(componentProperty.getDescriptionKey());
            }
        } else if (element instanceof IssPropertyComponentFlagsValueElement) {
            final IssPropertyComponentFlagsValueElement componentDefinitionFlagsValueElement = (IssPropertyComponentFlagsValueElement) element;
            final IssComponentFlag componentFlag = IssComponentFlag.fromId(componentDefinitionFlagsValueElement.getName());
            if (componentFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(componentFlag.getDescriptionKey());
        }

        return null;
    }
}
