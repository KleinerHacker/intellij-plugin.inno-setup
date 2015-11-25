package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.IssLanguage;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionElement;

import java.util.Collection;

/**
 * Created by Christoph on 25.11.2015.
 */
public abstract class IssFile extends PsiFileBase {

    public IssFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, IssLanguage.INSTANCE);
    }

    @NotNull
    public Collection<IssSectionElement> getSectionList() {
        return PsiTreeUtil.findChildrenOfType(this, IssSectionElement.class);
    }}
