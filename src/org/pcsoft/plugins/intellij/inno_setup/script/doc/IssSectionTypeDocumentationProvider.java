package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentPropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskPropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.*;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssComponentUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFileUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssTaskUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssTypeUtils;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssSectionTypeDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_type"), ResourceBundle.getBundle("/messages/documentation_common")
    );

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return super.getQuickNavigateInfo(element, originalElement);
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        if (PsiTreeUtil.getParentOfType(element, IssTypeDefinitionElement.class) != null) {
            return IssFileUtils.createFlagValue(element.getProject(), object.toString());
        }

        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssTypePropertyFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_typessection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierElement) {
            final IssIdentifierElement identifierElement = (IssIdentifierElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssTypeDefinitionElement.class) != null) {
                final IssTypeProperty typeProperty = IssTypeProperty.fromId(identifierElement.getName());
                if (typeProperty == null)
                    return "Unknown type property";

                return RESOURCE_BUNDLE.getString(typeProperty.getDescriptionKey());
            }
        } else if (element instanceof IssTypePropertyFlagsValueElement) {
            final IssTypePropertyFlagsValueElement typeDefinitionFlagsValueElement = (IssTypePropertyFlagsValueElement) element;
            final IssTypeFlag typeFlag = IssTypeFlag.fromId(typeDefinitionFlagsValueElement.getName());
            if (typeFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(typeFlag.getDescriptionKey());
        }

        return null;
    }
}
