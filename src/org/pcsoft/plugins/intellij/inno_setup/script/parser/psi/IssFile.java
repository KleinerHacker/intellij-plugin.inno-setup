package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.IssScriptFileType;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionElement;

import java.util.Collection;

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
        return IssScriptFileType.INSTANCE;
    }

    @NotNull
    public Collection<IssSectionElement> getSectionList() {
        return PsiTreeUtil.findChildrenOfType(this, IssSectionElement.class);
    }
}
