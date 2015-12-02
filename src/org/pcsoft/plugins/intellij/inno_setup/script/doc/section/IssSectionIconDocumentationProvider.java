package org.pcsoft.plugins.intellij.inno_setup.script.doc.section;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssIconDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIconFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssIconFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssIconProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.MultiResourceBundle;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssSectionIconDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_icon"), ResourceBundle.getBundle("/messages/documentation_common")
    );

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
//        if (PsiTreeUtil.getParentOfType(element, IssFileDefinitionElement.class) != null) {
//            return IssFileUtils.createFlagValue(element.getProject(), object.toString());
//        }

        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssPropertyIconFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_iconssection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierNameElement) {
            final IssIdentifierNameElement identifierNameElement = (IssIdentifierNameElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssIconDefinitionElement.class) != null) {
                final IssIconProperty iconProperty = IssIconProperty.fromId(identifierNameElement.getName());
                if (iconProperty == null)
                    return "Unknown icon property";

                return RESOURCE_BUNDLE.getString(iconProperty.getDescriptionKey());
            }
        } else if (element instanceof IssPropertyIconFlagsValueElement) {
            final IssPropertyIconFlagsValueElement iconDefinitionFlagsValueElement = (IssPropertyIconFlagsValueElement) element;
            final IssIconFlag iconFlag = IssIconFlag.fromId(iconDefinitionFlagsValueElement.getName());
            if (iconFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(iconFlag.getDescriptionKey());
        }

        return null;
    }
}
