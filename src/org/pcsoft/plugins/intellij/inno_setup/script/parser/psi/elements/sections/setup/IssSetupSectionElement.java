package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.setup;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionItemElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssSectionElement;

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
    public Collection<IssDefinitionItemElement> getSectionItemList() {
        return PsiTreeUtil.findChildrenOfType(this, IssDefinitionItemElement.class);
    }
}
