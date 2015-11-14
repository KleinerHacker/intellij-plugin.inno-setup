package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyFileFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssFileDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.*;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssSectionFileDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_file"), ResourceBundle.getBundle("/messages/documentation_common")
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
        if (element instanceof IssPropertyFileFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_filessection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierElement) {
            final IssIdentifierElement identifierElement = (IssIdentifierElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssFileDefinitionElement.class) != null) {
                final IssFileProperty fileProperty = IssFileProperty.fromId(identifierElement.getName());
                if (fileProperty == null)
                    return "Unknown file property";

                return RESOURCE_BUNDLE.getString(fileProperty.getDescriptionKey());
            }
        } else if (element instanceof IssPropertyFileFlagsValueElement) {
            final IssPropertyFileFlagsValueElement fileDefinitionFlagsValueElement = (IssPropertyFileFlagsValueElement) element;
            final IssFileFlag fileFlag = IssFileFlag.fromId(fileDefinitionFlagsValueElement.getName());
            if (fileFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(fileFlag.getDescriptionKey());
        }

        return null;
    }
}
