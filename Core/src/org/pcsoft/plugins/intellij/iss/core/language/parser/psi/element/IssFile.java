package org.pcsoft.plugins.intellij.iss.core.language.parser.psi.element;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.core.language.IssFileType;
import org.pcsoft.plugins.intellij.iss.core.language.IssLanguage;

import javax.swing.*;

/**
 * Created by Christoph on 30.09.2016.
 */
public class IssFile extends PsiFileBase {
    public IssFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, IssLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return IssFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Inno Setup Script File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }

    @Nullable
    public IssSection[] getSections() {
        return PsiTreeUtil.getChildrenOfType(this, IssSection.class);
    }
}
