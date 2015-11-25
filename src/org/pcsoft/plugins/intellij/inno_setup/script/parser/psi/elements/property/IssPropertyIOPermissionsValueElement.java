package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyValueElement;

/**
 * Created by Christoph on 27.12.2014.
 */
public class IssPropertyIOPermissionsValueElement extends IssPropertyValueElement {

    public IssPropertyIOPermissionsValueElement(ASTNode node) {
        super(node);
    }

    @NotNull
    @Override
    public String getName() {
        return getText();
    }

    @NotNull
    public String getUserOrGroupIdentifier() {
        try {
            return getText().substring(0, getText().indexOf("-"));
        } catch (Exception e) {
            return "";
        }
    }

    @NotNull
    public String getPermission() {
        try {
            return getText().substring(getText().indexOf("-") + 1);
        } catch (Exception e) {
            return "";
        }
    }
}
