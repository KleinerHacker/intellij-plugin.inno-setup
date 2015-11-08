package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory.IssDirectoryDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory.IssDirectoryPropertyAttributeValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory.IssDirectoryPropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory.IssDirectoryPropertyPermissionsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.*;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssDirectoryUtils;

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

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return super.getQuickNavigateInfo(element, originalElement);
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        if (PsiTreeUtil.getParentOfType(element, IssDirectoryDefinitionElement.class) != null) {
            return IssDirectoryUtils.createFlagValue(element.getProject(), object.toString());
        }

        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssDirectoryPropertyFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_dirssection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssIdentifierElement) {
            final IssIdentifierElement identifierElement = (IssIdentifierElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssDirectoryDefinitionElement.class) != null) {
                final IssDirectoryProperty directoryProperty = IssDirectoryProperty.fromId(identifierElement.getName());
                if (directoryProperty == null)
                    return "Unknown directory property";

                return RESOURCE_BUNDLE.getString(directoryProperty.getDescriptionKey());
            }
        } else if (element instanceof IssDirectoryPropertyFlagsValueElement) {
            final IssDirectoryPropertyFlagsValueElement directoryPropertyFlagsValueElement = (IssDirectoryPropertyFlagsValueElement) element;
            final IssFileFlag fileFlag = IssFileFlag.fromId(directoryPropertyFlagsValueElement.getName());
            if (fileFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(fileFlag.getDescriptionKey());
        } else if (element instanceof IssDirectoryPropertyAttributeValueElement) {
            final IssDirectoryPropertyAttributeValueElement directoryPropertyAttributeValueElement = (IssDirectoryPropertyAttributeValueElement) element;
            final IssCommonIOAttribute issCommonIOAttribute = IssCommonIOAttribute.fromId(directoryPropertyAttributeValueElement.getName());
            if (issCommonIOAttribute == null)
                return "Unknown attribute";

            return RESOURCE_BUNDLE.getString(issCommonIOAttribute.getDescriptionKey());
        } else if (element instanceof IssDirectoryPropertyPermissionsValueElement) {
            final IssDirectoryPropertyPermissionsValueElement directoryPropertyPermissionsValueElement = (IssDirectoryPropertyPermissionsValueElement) element;
            final IssCommonIOPermissions issCommonIOPermissions = IssCommonIOPermissions.fromId(directoryPropertyPermissionsValueElement.getPermission());
            if (issCommonIOPermissions == null)
                return "Unknown permission";
            final IssCommonUserOrGroupIdentifier issCommonUserOrGroupIdentifier = IssCommonUserOrGroupIdentifier.fromId(
                    directoryPropertyPermissionsValueElement.getUserOrGroupIdentifier());
            if (issCommonUserOrGroupIdentifier == null)
                return "Unknown user or group identifier";

            return RESOURCE_BUNDLE.getString(issCommonIOPermissions.getDescriptionKey()) + "<br/><i>As: " +
                    RESOURCE_BUNDLE.getString(issCommonUserOrGroupIdentifier.getDescriptionKey()) + "</i>";
        }

        return null;
    }
}
