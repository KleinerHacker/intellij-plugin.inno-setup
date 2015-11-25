package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssRegistryDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyRegistryFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyRegistryRootValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyRegistryValueTypeValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.*;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssSectionRegistryDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_registry"), ResourceBundle.getBundle("/messages/documentation_common")
    );

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
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_registrysection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierElement) {
            final IssIdentifierElement identifierElement = (IssIdentifierElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssRegistryDefinitionElement.class) != null) {
                final IssRegistryProperty registryProperty = IssRegistryProperty.fromId(identifierElement.getName());
                if (registryProperty == null)
                    return "Unknown Registry property";

                return RESOURCE_BUNDLE.getString(registryProperty.getDescriptionKey());
            }
        } else if (element instanceof IssPropertyRegistryFlagsValueElement) {
            final IssPropertyRegistryFlagsValueElement registryDefinitionFlagsValueElement = (IssPropertyRegistryFlagsValueElement) element;
            final IssRegistryFlag registryFlag = IssRegistryFlag.fromId(registryDefinitionFlagsValueElement.getName());
            if (registryFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(registryFlag.getDescriptionKey());
        } else if (element instanceof IssPropertyRegistryRootValueElement) {
            final IssPropertyRegistryRootValueElement registryDefinitionRootValueElement = (IssPropertyRegistryRootValueElement) element;
            final IssRegistryRoot registryRoot = IssRegistryRoot.fromId(registryDefinitionRootValueElement.getName());
            if (registryRoot == null)
                return "Unknown root";

            return RESOURCE_BUNDLE.getString(registryRoot.getDescriptionKey());
        } else if (element instanceof IssPropertyRegistryValueTypeValueElement) {
            final IssPropertyRegistryValueTypeValueElement registryDefinitionValueTypeValueElement = (IssPropertyRegistryValueTypeValueElement) element;
            final IssRegistryValueType registryValueType = IssRegistryValueType.fromId(registryDefinitionValueTypeValueElement.getName());
            if (registryValueType == null)
                return "Unknown value type";

            return RESOURCE_BUNDLE.getString(registryValueType.getDescriptionKey());
        }

        return null;
    }
}
