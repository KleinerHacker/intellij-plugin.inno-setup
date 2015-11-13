package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOAttributeValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOCopyModeValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.IssPropertyIOPermissionsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssCommonIOAttribute;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssCommonIOPermissions;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssCommonUserOrGroupIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileCopyMode;

import java.util.ResourceBundle;

/**
 * Created by Christoph on 13.11.2015.
 */
public class IssSectionCommonDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("/messages/documentation_common");

    @Override
    public String generateDoc(PsiElement element, @Nullable PsiElement originalElement) {
        if (element instanceof IssPropertyIOAttributeValueElement) {
            final IssPropertyIOAttributeValueElement directoryPropertyAttributeValueElement = (IssPropertyIOAttributeValueElement) element;
            final IssCommonIOAttribute issCommonIOAttribute = IssCommonIOAttribute.fromId(directoryPropertyAttributeValueElement.getName());
            if (issCommonIOAttribute == null)
                return "Unknown attribute";

            return RESOURCE_BUNDLE.getString(issCommonIOAttribute.getDescriptionKey());
        } else if (element instanceof IssPropertyIOPermissionsValueElement) {
            final IssPropertyIOPermissionsValueElement directoryPropertyPermissionsValueElement = (IssPropertyIOPermissionsValueElement) element;
            final IssCommonIOPermissions issCommonIOPermissions = IssCommonIOPermissions.fromId(directoryPropertyPermissionsValueElement.getPermission());
            if (issCommonIOPermissions == null)
                return "Unknown permission";
            final IssCommonUserOrGroupIdentifier issCommonUserOrGroupIdentifier = IssCommonUserOrGroupIdentifier.fromId(
                    directoryPropertyPermissionsValueElement.getUserOrGroupIdentifier());
            if (issCommonUserOrGroupIdentifier == null)
                return "Unknown user or group identifier";

            return RESOURCE_BUNDLE.getString(issCommonIOPermissions.getDescriptionKey()) + "<br/><i>As: " +
                    RESOURCE_BUNDLE.getString(issCommonUserOrGroupIdentifier.getDescriptionKey()) + "</i>";
        } else if (element instanceof IssPropertyIOCopyModeValueElement) {
            final IssPropertyIOCopyModeValueElement filePropertyCopyModeValueElement = (IssPropertyIOCopyModeValueElement) element;
            final IssFileCopyMode fileCopyMode = IssFileCopyMode.fromId(filePropertyCopyModeValueElement.getName());
            if (fileCopyMode == null)
                return "Unknown copy mode";

            return RESOURCE_BUNDLE.getString(fileCopyMode.getDescriptionKey());
        }

        return null;
    }
}
