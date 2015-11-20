package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyIOAttributeValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyIOCopyModeValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.definable.IssPropertyIOPermissionsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIOAttribute;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIOPermissions;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssIOUserOrGroupIdentifier;
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
            final IssIOAttribute issIOAttribute = IssIOAttribute.fromId(directoryPropertyAttributeValueElement.getName());
            if (issIOAttribute == null)
                return "Unknown attribute";

            return RESOURCE_BUNDLE.getString(issIOAttribute.getDescriptionKey());
        } else if (element instanceof IssPropertyIOPermissionsValueElement) {
            final IssPropertyIOPermissionsValueElement directoryPropertyPermissionsValueElement = (IssPropertyIOPermissionsValueElement) element;
            final IssIOPermissions issIOPermissions = IssIOPermissions.fromId(directoryPropertyPermissionsValueElement.getPermission());
            if (issIOPermissions == null)
                return "Unknown permission";
            final IssIOUserOrGroupIdentifier issIOUserOrGroupIdentifier = IssIOUserOrGroupIdentifier.fromId(
                    directoryPropertyPermissionsValueElement.getUserOrGroupIdentifier());
            if (issIOUserOrGroupIdentifier == null)
                return "Unknown user or group identifier";

            return RESOURCE_BUNDLE.getString(issIOPermissions.getDescriptionKey()) + "<br/><i>As: " +
                    RESOURCE_BUNDLE.getString(issIOUserOrGroupIdentifier.getDescriptionKey()) + "</i>";
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
