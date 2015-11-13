package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.setup;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionElement;

import javax.swing.*;
import java.util.Collection;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssSetupSectionElement extends IssSectionElement {

    public IssSetupSectionElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssSetupSectionAppNameElement getSetupAppName() {
        return PsiTreeUtil.findChildOfType(this, IssSetupSectionAppNameElement.class);
    }

    @Nullable
    public IssSetupSectionAppVersionElement getSetupAppVersion() {
        return PsiTreeUtil.findChildOfType(this, IssSetupSectionAppVersionElement.class);
    }

    @Nullable
    public Collection<IssPropertyElement> getSectionItemList() {
        return PsiTreeUtil.findChildrenOfType(this, IssPropertyElement.class);
    }

    @Override
    protected Icon getIcon(boolean b) {
        return IssIcons.IC_SECT_SETUP;
    }
}
