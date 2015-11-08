package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentPropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.MultiResourceBundle;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssComponentUtils;

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
        if (element instanceof IssComponentPropertyNameValueElement) {

            final IssComponentPropertyNameValueElement nameValueElement = (IssComponentPropertyNameValueElement) element;
            return "Reference to component: " + nameValueElement.getName() + "<br/>" +
                    "Component Name: " + nameValueElement.getValueParent().getDefinition().getComponentDescription().getPropertyValue().getText();
        }

        return super.getQuickNavigateInfo(element, originalElement);
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        if (PsiTreeUtil.getParentOfType(element, IssComponentDefinitionElement.class) != null) {
            return IssComponentUtils.createFlagValue(element.getProject(), object.toString());
        }

        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssComponentPropertyFlagsValueElement) {
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
        } else if (element instanceof IssComponentPropertyFlagsValueElement) {
            final IssComponentPropertyFlagsValueElement componentDefinitionFlagsValueElement = (IssComponentPropertyFlagsValueElement) element;
            final IssComponentFlag componentFlag = IssComponentFlag.fromId(componentDefinitionFlagsValueElement.getName());
            if (componentFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(componentFlag.getDescriptionKey());
        }

        return null;
    }
}
