package org.pcsoft.plugins.intellij.inno_setup.script.structure.tree;

import com.intellij.ide.structureView.StructureViewModel;
import com.intellij.ide.structureView.StructureViewModelBase;
import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

/**
 * User: Christoph
 * Date: 14.07.13
 * Time: 11:42
 * TODO: Write Summary
 */
public final class IssStructureViewModel extends StructureViewModelBase implements
        StructureViewModel.ElementInfoProvider {

    public IssStructureViewModel(@NotNull PsiFile psiFile) {
        super(psiFile, new IssPsiStructureViewElement(psiFile));
    }

    @NotNull
    public Sorter[] getSorters() {
        return new Sorter[] {Sorter.ALPHA_SORTER};
    }


    @Override
    public boolean isAlwaysShowsPlus(StructureViewTreeElement element) {
        return false;
    }

    @Override
    public boolean isAlwaysLeaf(StructureViewTreeElement element) {
        return false;
    }
}
