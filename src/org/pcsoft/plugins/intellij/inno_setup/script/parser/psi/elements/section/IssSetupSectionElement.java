package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.section;

import com.intellij.lang.ASTNode;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssStandardSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.standard.IssPropertyDefaultElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssSetupProperty;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFindUtils;

import javax.swing.*;

/**
 * Created by Christoph on 22.12.2014.
 */
public class IssSetupSectionElement extends IssStandardSectionElement {

    public IssSetupSectionElement(ASTNode node) {
        super(node);
    }

    @Nullable
    public IssPropertyDefaultElement getSetupAppName() {
        return IssFindUtils.findProperty(this, IssSetupProperty.AppName, IssPropertyDefaultElement.class);
    }

    @Nullable
    public IssPropertyDefaultElement getSetupAppVersion() {
        return IssFindUtils.findProperty(this, IssSetupProperty.AppVersion, IssPropertyDefaultElement.class);
    }

    @Override
    protected Icon getIcon(boolean b) {
        return IssIcons.IC_SECT_SETUP;
    }
}