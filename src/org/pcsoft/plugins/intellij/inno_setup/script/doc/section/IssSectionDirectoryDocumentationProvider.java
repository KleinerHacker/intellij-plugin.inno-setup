package org.pcsoft.plugins.intellij.inno_setup.script.doc.section;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssDirectoryDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyDirectoryFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssDirectoryFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssDirectoryProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.MultiResourceBundle;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssSectionDirectoryDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_directory"), ResourceBundle.getBundle("/messages/documentation_common")
    );

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
//        if (PsiTreeUtil.getParentOfType(element, IssDirectoryDefinitionElement.class) != null) {
//            return IssDirectoryUtils.createFlagValue(element.getProject(), object.toString());
//        }

        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssPropertyDirectoryFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_dirssection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierNameElement) {
            final IssIdentifierNameElement identifierNameElement = (IssIdentifierNameElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssDirectoryDefinitionElement.class) != null) {
                final IssDirectoryProperty directoryProperty = IssDirectoryProperty.fromId(identifierNameElement.getName());
                if (directoryProperty == null)
                    return "Unknown directory property";

                return RESOURCE_BUNDLE.getString(directoryProperty.getDescriptionKey());
            }
        } else if (element instanceof IssPropertyDirectoryFlagsValueElement) {
            final IssPropertyDirectoryFlagsValueElement directoryPropertyFlagsValueElement = (IssPropertyDirectoryFlagsValueElement) element;
            final IssDirectoryFlag directoryFlag = IssDirectoryFlag.fromId(directoryPropertyFlagsValueElement.getName());
            if (directoryFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(directoryFlag.getDescriptionKey());
        }

        return null;
    }
}
