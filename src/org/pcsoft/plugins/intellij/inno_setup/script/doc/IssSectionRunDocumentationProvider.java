package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssRunDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyFileFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyRunFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRunFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssRunProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.MultiResourceBundle;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssSectionRunDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_run"), ResourceBundle.getBundle("/messages/documentation_common")
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

            if (PsiTreeUtil.getParentOfType(element, IssRunDefinitionElement.class) != null) {
                final IssRunProperty runProperty = IssRunProperty.fromId(identifierElement.getName());
                if (runProperty == null)
                    return "Unknown run property";

                return RESOURCE_BUNDLE.getString(runProperty.getDescriptionKey());
            }
        } else if (element instanceof IssPropertyRunFlagsValueElement) {
            final IssPropertyRunFlagsValueElement runDefinitionFlagsValueElement = (IssPropertyRunFlagsValueElement) element;
            final IssRunFlag runFlag = IssRunFlag.fromId(runDefinitionFlagsValueElement.getName());
            if (runFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(runFlag.getDescriptionKey());
        }

        return null;
    }
}
