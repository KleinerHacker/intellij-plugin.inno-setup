package org.pcsoft.plugins.intellij.inno_setup.script.doc.section;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssINIDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyINIFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyTaskFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssINIFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssINIProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.MultiResourceBundle;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssSectionINIDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_ini"), ResourceBundle.getBundle("/messages/documentation_common")
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
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_inisection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierNameElement) {
            final IssIdentifierNameElement identifierNameElement = (IssIdentifierNameElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssINIDefinitionElement.class) != null) {
                final IssINIProperty iniProperty = IssINIProperty.fromId(identifierNameElement.getName());
                if (iniProperty == null)
                    return "Unknown INI property";

                return RESOURCE_BUNDLE.getString(iniProperty.getDescriptionKey());
            }
        } else if (element instanceof IssPropertyINIFlagsValueElement) {
            final IssPropertyINIFlagsValueElement iniDefinitionFlagsValueElement = (IssPropertyINIFlagsValueElement) element;
            final IssINIFlag iniFlag = IssINIFlag.fromId(iniDefinitionFlagsValueElement.getName());
            if (iniFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(iniFlag.getDescriptionKey());
        }

        return null;
    }
}
