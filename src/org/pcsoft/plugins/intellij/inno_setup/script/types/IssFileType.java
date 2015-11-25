package org.pcsoft.plugins.intellij.inno_setup.script.types;

import com.intellij.extapi.psi.PsiFileBase;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssLanguageFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssScriptFile;

/**
 * Created by Christoph on 25.11.2015.
 */
public enum IssFileType {
    Script("iss", IssScriptFile.class),
    Language("isl", IssLanguageFile.class);

    private final String extension;
    private final Class<? extends PsiFileBase> psiFileClass;

    private IssFileType(String extension, Class<? extends PsiFileBase> psiFileClass) {
        this.extension = extension;
        this.psiFileClass = psiFileClass;
    }

    public String getExtension() {
        return extension;
    }

    public Class<? extends PsiFileBase> getPsiFileClass() {
        return psiFileClass;
    }
}
