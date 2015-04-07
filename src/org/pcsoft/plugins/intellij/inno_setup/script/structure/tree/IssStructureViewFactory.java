package org.pcsoft.plugins.intellij.inno_setup.script.structure.tree;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.TreeBasedStructureViewBuilder;
import com.intellij.lang.PsiStructureViewFactory;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * User: Christoph
 * Date: 14.07.13
 * Time: 11:44
 * TODO: Write Summary
 */
public final class IssStructureViewFactory implements PsiStructureViewFactory {

    @Nullable
    @Override
    public StructureViewBuilder getStructureViewBuilder(final PsiFile psiFile) {
        return new TreeBasedStructureViewBuilder() {
            @NotNull
            @Override
            public com.intellij.ide.structureView.StructureViewModel createStructureViewModel() {
                return new IssStructureViewModel(psiFile);
            }
        };
    }
}
