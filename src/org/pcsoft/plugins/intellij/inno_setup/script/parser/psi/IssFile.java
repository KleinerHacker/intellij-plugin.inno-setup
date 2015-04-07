package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.IssFileType;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;

/**
 * Created by Christoph on 13.12.2014.
 */
public class IssFile extends PsiFileBase {

    public IssFile(FileViewProvider viewProvider) {
        super(viewProvider, IssLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return IssFileType.INSTANCE;
    }
}
