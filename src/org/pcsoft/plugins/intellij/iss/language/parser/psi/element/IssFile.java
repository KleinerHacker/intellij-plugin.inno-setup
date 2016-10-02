package org.pcsoft.plugins.intellij.iss.language.parser.psi.element;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.iss.language.IssFileType;
import org.pcsoft.plugins.intellij.iss.language.IssLanguage;

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
}
