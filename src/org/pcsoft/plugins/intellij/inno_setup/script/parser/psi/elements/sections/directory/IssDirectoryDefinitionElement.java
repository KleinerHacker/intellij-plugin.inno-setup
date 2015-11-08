package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.directory;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionElement;

import javax.swing.*;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssDirectoryDefinitionElement extends IssDefinitionElement<IssDirectorySectionElement> {

    public IssDirectoryDefinitionElement(ASTNode node) {
        super(node, IssDirectorySectionElement.class);
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
                return "Dirs";
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
        return getDirectoryName() == null ? null : getDirectoryName().getValue() == null ? null : getDirectoryName().getValue().getText();
    }

    @Nullable
    public IssDirectoryPropertyNameElement getDirectoryName() {
        return PsiTreeUtil.findChildOfType(this, IssDirectoryPropertyNameElement.class);
    }

    @Nullable
    public IssDirectoryPropertyTasksElement getDirectoryTasks() {
        return PsiTreeUtil.findChildOfType(this, IssDirectoryPropertyTasksElement.class);
    }

    @Nullable
    public IssDirectoryPropertyComponentsElement getDirectoryComponents() {
        return PsiTreeUtil.findChildOfType(this, IssDirectoryPropertyComponentsElement.class);
    }

    @Nullable
    public IssDirectoryPropertyFlagsElement getDirectoryFlags() {
        return PsiTreeUtil.findChildOfType(this, IssDirectoryPropertyFlagsElement.class);
    }

    @Nullable
    public IssDirectoryPropertyAttributeElement getDirectoryAttribute() {
        return PsiTreeUtil.findChildOfType(this, IssDirectoryPropertyAttributeElement.class);
    }

    @Nullable
    public IssDirectoryPropertyPermissionsElement getDirectoryPermissions() {
        return PsiTreeUtil.findChildOfType(this, IssDirectoryPropertyPermissionsElement.class);
    }
}
