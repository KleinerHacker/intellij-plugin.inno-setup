package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssIdentifierElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.*;
import org.pcsoft.plugins.intellij.inno_setup.script.types.*;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFileUtils;

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

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        return super.getQuickNavigateInfo(element, originalElement);
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        if (PsiTreeUtil.getParentOfType(element, IssFileDefinitionElement.class) != null) {
            return IssFileUtils.createFlagValue(element.getProject(), object.toString());
        }

        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssFilePropertyFlagsValueElement) {
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
        } else if (element instanceof IssFilePropertyFlagsValueElement) {
            final IssFilePropertyFlagsValueElement fileDefinitionFlagsValueElement = (IssFilePropertyFlagsValueElement) element;
            final IssFileFlag fileFlag = IssFileFlag.fromId(fileDefinitionFlagsValueElement.getName());
            if (fileFlag == null)
                return "Unknown flag";

            return RESOURCE_BUNDLE.getString(fileFlag.getDescriptionKey());
        } else if (element instanceof IssFilePropertyCopyModeValueElement) {
            final IssFilePropertyCopyModeValueElement filePropertyCopyModeValueElement = (IssFilePropertyCopyModeValueElement) element;
            final IssFileCopyMode fileCopyMode = IssFileCopyMode.fromId(filePropertyCopyModeValueElement.getName());
            if (fileCopyMode == null)
                return "Unknown copy mode";

            return RESOURCE_BUNDLE.getString(fileCopyMode.getDescriptionKey());
        } else if (element instanceof IssFilePropertyAttributeValueElement) {
            final IssFilePropertyAttributeValueElement filePropertyAttributeValueElement = (IssFilePropertyAttributeValueElement) element;
            final IssCommonIOAttribute issCommonIOAttribute = IssCommonIOAttribute.fromId(filePropertyAttributeValueElement.getName());
            if (issCommonIOAttribute == null)
                return "Unknown attribute";

            return RESOURCE_BUNDLE.getString(issCommonIOAttribute.getDescriptionKey());
        } else if (element instanceof IssFilePropertyPermissionsValueElement) {
            final IssFilePropertyPermissionsValueElement filePropertyPermissionsValueElement = (IssFilePropertyPermissionsValueElement) element;
            final IssCommonIOPermissions issCommonIOPermissions = IssCommonIOPermissions.fromId(filePropertyPermissionsValueElement.getPermission());
            if (issCommonIOPermissions == null)
                return "Unknown permission";
            final IssCommonUserOrGroupIdentifier issCommonUserOrGroupIdentifier = IssCommonUserOrGroupIdentifier.fromId(
                    filePropertyPermissionsValueElement.getUserOrGroupIdentifier());
            if (issCommonUserOrGroupIdentifier == null)
                return "Unknown user or group identifier";

            return RESOURCE_BUNDLE.getString(issCommonIOPermissions.getDescriptionKey()) + "<br/><i>As: " +
                    RESOURCE_BUNDLE.getString(issCommonUserOrGroupIdentifier.getDescriptionKey()) + "</i>";
        }

        return null;
    }
}
