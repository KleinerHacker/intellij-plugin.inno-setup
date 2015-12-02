package org.pcsoft.plugins.intellij.inno_setup.script.doc.section;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.definition.IssUninstallRunDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyFileFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyUninstallRunFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.flag.IssUninstallRunFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssUninstallRunProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.MultiResourceBundle;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssSectionUninstallRunDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_uninstall_run"), ResourceBundle.getBundle("/messages/documentation_common")
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
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_uninstallrunsection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierNameElement) {
            final IssIdentifierNameElement identifierNameElement = (IssIdentifierNameElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssUninstallRunDefinitionElement.class) != null) {
                final IssUninstallRunProperty runProperty = IssUninstallRunProperty.fromId(identifierNameElement.getName());
                if (runProperty == null)
                    return "Unknown uninstall run property";

                return RESOURCE_BUNDLE.getString(runProperty.getDescriptionKey());
            }
        } else if (element instanceof IssPropertyUninstallRunFlagsValueElement) {
            final IssPropertyUninstallRunFlagsValueElement uninstallRunDefinitionFlagsValueElement = (IssPropertyUninstallRunFlagsValueElement) element;
            final IssUninstallRunFlag runFlag = IssUninstallRunFlag.fromId(uninstallRunDefinitionFlagsValueElement.getName());
            if (runFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(runFlag.getDescriptionKey());
        }

        return null;
    }
}
