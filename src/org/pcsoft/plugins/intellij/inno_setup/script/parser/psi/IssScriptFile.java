package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi;

import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.IssScriptFileType;

/**
 * Created by Christoph on 13.12.2014.
 */
public class IssScriptFile extends IssFile {

    public IssScriptFile(FileViewProvider viewProvider) {
        super(viewProvider);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return IssScriptFileType.INSTANCE;
    }
}
