package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionElement;

import javax.swing.Icon;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssTaskDefinitionElement extends IssDefinitionElement<IssTaskSectionElement> {

    public IssTaskDefinitionElement(ASTNode node) {
        super(node, IssTaskSectionElement.class);
    }

    @Override
    public ItemPresentation getPresentation() {
        return new ItemPresentation() {
            @Nullable
            @Override
            public String getPresentableText() {
                return getName();
            }

            @Nullable
            @Override
            public String getLocationString() {
                return "Tasks";
            }

            @Nullable
            @Override
            public Icon getIcon(boolean b) {
                return null;
            }
        };
    }

    @Override
    public String getName() {
        return getTaskName() == null ? null : getTaskName().getNameValue() == null ? null : getTaskName().getNameValue().getName();
    }

    @Nullable
    public IssTaskDefinitionNameElement getTaskName() {
        return PsiTreeUtil.findChildOfType(this, IssTaskDefinitionNameElement.class);
    }

    @Nullable
    public IssTaskDefinitionDescriptionElement getTaskDescription() {
        return PsiTreeUtil.findChildOfType(this, IssTaskDefinitionDescriptionElement.class);
    }

    @Nullable
    public IssTaskDefinitionComponentsElement getTaskComponents() {
        return PsiTreeUtil.findChildOfType(this, IssTaskDefinitionComponentsElement.class);
    }

    @Nullable
    public IssTaskDefinitionFlagsElement getTaskFlags() {
        return PsiTreeUtil.findChildOfType(this, IssTaskDefinitionFlagsElement.class);
    }
}
