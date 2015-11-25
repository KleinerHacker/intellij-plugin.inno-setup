package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguageFileType;

/**
 * Created by Christoph on 13.12.2014.
 */
public class IssLanguageFile extends IssFile {

    public IssLanguageFile(FileViewProvider viewProvider) {
        super(viewProvider);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return IssLanguageFileType.INSTANCE;
    }
}
