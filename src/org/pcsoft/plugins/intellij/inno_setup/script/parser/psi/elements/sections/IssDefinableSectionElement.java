package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Created by Christoph on 23.12.2014.
 */
public abstract class IssDefinableSectionElement<D extends IssDefinitionElement> extends IssSectionElement {

    private final Class<D> clazz;

    public IssDefinableSectionElement(ASTNode node, Class<D> clazz) {
        super(node);
        this.clazz = clazz;
    }

    @NotNull
    public Collection<D> getDefinitionList() {
        return PsiTreeUtil.findChildrenOfType(this, clazz);
    }
}
