package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
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

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("/messages/documentation_file");
    private static final ResourceBundle COMMON_RESOURCE_BUNDLE = ResourceBundle.getBundle("/messages/documentation_common");
    private static final String UNKNOWN = "Unknown flag";

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
        if (element instanceof IssFilePropertyFlagsValueElement) {
            final IssFilePropertyFlagsValueElement fileDefinitionFlagsValueElement = (IssFilePropertyFlagsValueElement) element;
            final IssFileFlag fileFlag = IssFileFlag.fromId(fileDefinitionFlagsValueElement.getName());
            if (fileFlag == null)
                return UNKNOWN;

            return RESOURCE_BUNDLE.getString(fileFlag.getDescriptionKey());
        } else if (element instanceof IssFilePropertyCopyModeValueElement) {
            final IssFilePropertyCopyModeValueElement filePropertyCopyModeValueElement = (IssFilePropertyCopyModeValueElement) element;
            final IssFileCopyMode fileCopyMode = IssFileCopyMode.fromId(filePropertyCopyModeValueElement.getName());
            if (fileCopyMode == null)
                return UNKNOWN;

            return RESOURCE_BUNDLE.getString(fileCopyMode.getDescriptionKey());
        } else if (element instanceof IssFilePropertyAttributeValueElement) {
            final IssFilePropertyAttributeValueElement filePropertyAttributeValueElement = (IssFilePropertyAttributeValueElement) element;
            final IssFileAttribute issFileAttribute = IssFileAttribute.fromId(filePropertyAttributeValueElement.getName());
            if (issFileAttribute == null)
                return UNKNOWN;

            return RESOURCE_BUNDLE.getString(issFileAttribute.getDescriptionKey());
        } else if (element instanceof IssFilePropertyPermissionsValueElement) {
            final IssFilePropertyPermissionsValueElement filePropertyPermissionsValueElement = (IssFilePropertyPermissionsValueElement) element;
            final IssFilePermissions issFilePermissions = IssFilePermissions.fromId(filePropertyPermissionsValueElement.getPermission());
            if (issFilePermissions == null)
                return UNKNOWN;
            final IssCommonUserOrGroupIdentifier issCommonUserOrGroupIdentifier = IssCommonUserOrGroupIdentifier.fromId(
                    filePropertyPermissionsValueElement.getUserOrGroupIdentifier());
            if (issCommonUserOrGroupIdentifier == null)
                return UNKNOWN;

            return RESOURCE_BUNDLE.getString(issFilePermissions.getDescriptionKey()) + "<br/><i>As: " +
                    COMMON_RESOURCE_BUNDLE.getString(issCommonUserOrGroupIdentifier.getDescriptionKey()) + "</i>";
        }

        return null;
    }
}
