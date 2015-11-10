package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import com.intellij.navigation.ItemPresentation;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileProperty;

import javax.swing.Icon;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFileDefinitionElement extends IssDefinitionElement<IssFileSectionElement, IssFileProperty> {

    public IssFileDefinitionElement(ASTNode node) {
        super(node, IssFileSectionElement.class);
    }

    @NotNull
    @Override
    public IssFileProperty[] getPropertyTypeList() {
        return IssFileProperty.values();
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
                return IssIcons.IC_SECT_FILE;
            }
        };
    }

    @Override
    public String getName() {
        return getFileSource() == null ? null : getFileSource().getValue() == null ? null : getFileSource().getValue().getText();
    }

    @Nullable
    public IssFilePropertySourceElement getFileSource() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertySourceElement.class);
    }

    @Nullable
    public IssFilePropertyTasksElement getFileTasks() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertyTasksElement.class);
    }

    @Nullable
    public IssFilePropertyDestDirElement getFileDestDir() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertyDestDirElement.class);
    }

    @Nullable
    public IssFilePropertyDestNameElement getFileDestName() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertyDestNameElement.class);
    }

    @Nullable
    public IssFilePropertyExcludesElement getFileExcludes() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertyExcludesElement.class);
    }

    @Nullable
    public IssFilePropertyExternalSizeElement getFileExternalSize() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertyExternalSizeElement.class);
    }

    @Nullable
    public IssFilePropertyFontInstallElement getFileFontInstall() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertyFontInstallElement.class);
    }

    @Nullable
    public IssFilePropertyStrongAssemblyNameElement getFileStrongAssemblyName() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertyStrongAssemblyNameElement.class);
    }

    @Nullable
    public IssFilePropertyComponentsElement getFileComponents() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertyComponentsElement.class);
    }

    @Nullable
    public IssFilePropertyFlagsElement getFileFlags() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertyFlagsElement.class);
    }

    @Nullable
    public IssFilePropertyCopyModeElement getFileCopyMode() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertyCopyModeElement.class);
    }

    @Nullable
    public IssFilePropertyAttributeElement getFileAttribute() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertyAttributeElement.class);
    }

    @Nullable
    public IssFilePropertyPermissionsElement getFilePermissions() {
        return PsiTreeUtil.findChildOfType(this, IssFilePropertyPermissionsElement.class);
    }
}
