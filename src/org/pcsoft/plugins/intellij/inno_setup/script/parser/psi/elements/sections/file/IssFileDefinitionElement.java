package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionElement;

import javax.swing.Icon;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFileDefinitionElement extends IssDefinitionElement<IssFileSectionElement> {

    public IssFileDefinitionElement(ASTNode node) {
        super(node, IssFileSectionElement.class);
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
                return "Files";
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
        return getFileSource() == null ? null : getFileSource().getValue() == null ? null : getFileSource().getValue().getText();
    }

    @Nullable
    public IssFileDefinitionSourceElement getFileSource() {
        return PsiTreeUtil.findChildOfType(this, IssFileDefinitionSourceElement.class);
    }

    @Nullable
    public IssFileDefinitionTasksElement getFileTasks() {
        return PsiTreeUtil.findChildOfType(this, IssFileDefinitionTasksElement.class);
    }

    @Nullable
    public IssFileDefinitionDestDirElement getFileDestDir() {
        return PsiTreeUtil.findChildOfType(this, IssFileDefinitionDestDirElement.class);
    }

    @Nullable
    public IssFileDefinitionComponentsElement getFileComponents() {
        return PsiTreeUtil.findChildOfType(this, IssFileDefinitionComponentsElement.class);
    }

    @Nullable
    public IssFileDefinitionFlagsElement getFileFlags() {
        return PsiTreeUtil.findChildOfType(this, IssFileDefinitionFlagsElement.class);
    }
}
