package org.pcsoft.plugins.intellij.inno_setup.script.doc.section;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section.common.IssSectionNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.section.IssSectionType;

import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 */
public class IssSectionDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("/messages/documentation_section");

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssSectionNameElement) {
            final IssSectionNameElement sectionNameElement = (IssSectionNameElement) element;
            final IssSectionType sectionType = IssSectionType.fromId(sectionNameElement.getName());
            if (sectionType == null)
                return null;
            if (sectionType.getDescriptionKey() == null)
                return null;

            return RESOURCE_BUNDLE.getString(sectionType.getDescriptionKey());
        }

        return null;
    }
}
