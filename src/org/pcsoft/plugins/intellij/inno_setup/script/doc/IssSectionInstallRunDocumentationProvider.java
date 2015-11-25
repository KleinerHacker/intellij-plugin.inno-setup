package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssInstallRunDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyFileFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyInstallRunFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssInstallRunFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssInstallRunProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.MultiResourceBundle;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssSectionInstallRunDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_install_run"), ResourceBundle.getBundle("/messages/documentation_common")
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
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_runsection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierElement) {
            final IssIdentifierElement identifierElement = (IssIdentifierElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssInstallRunDefinitionElement.class) != null) {
                final IssInstallRunProperty runProperty = IssInstallRunProperty.fromId(identifierElement.getName());
                if (runProperty == null)
                    return "Unknown run property";

                return RESOURCE_BUNDLE.getString(runProperty.getDescriptionKey());
            }
        } else if (element instanceof IssPropertyInstallRunFlagsValueElement) {
            final IssPropertyInstallRunFlagsValueElement installRunDefinitionFlagsValueElement = (IssPropertyInstallRunFlagsValueElement) element;
            final IssInstallRunFlag runFlag = IssInstallRunFlag.fromId(installRunDefinitionFlagsValueElement.getName());
            if (runFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(runFlag.getDescriptionKey());
        }

        return null;
    }
}
