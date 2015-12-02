package org.pcsoft.plugins.intellij.inno_setup.script.doc.section;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.IssSetupSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.property.IssSetupProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.types.MultiResourceBundle;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssCreationUtils;

import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssSectionSetupDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_setup"), ResourceBundle.getBundle("/messages/documentation_common")
    );

    @Override
    public PsiElement getDocumentationElementForLink(PsiManager psiManager, String link, PsiElement context) {
        //TODO: How work it?
        if (link.toLowerCase().equals("topic_setup_appversion.htm"))
            return IssCreationUtils.createPropertyFor(psiManager.getProject(), "SETUP", "AppVersion=none", IssIdentifierElement.class);

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierNameElement) {
            final IssIdentifierNameElement identifierNameElement = (IssIdentifierNameElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssSetupSectionElement.class) != null) {
                final IssSetupProperty setupProperty = IssSetupProperty.fromId(identifierNameElement.getName());
                if (setupProperty == null)
                    return "Unknown setup property";

                return RESOURCE_BUNDLE.getString(setupProperty.getDescriptionKey());
            }
        }

        return null;
    }
}
