package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssAbstractElement;

import java.util.Collection;

/**
 * Created by Christoph on 23.12.2014.
 */
public abstract class IssDefinitionElement<P extends IssDefinableSectionElement> extends IssAbstractElement {

    private final Class<P> clazz;

    public IssDefinitionElement(ASTNode node, Class<P> clazz) {
        super(node);
        this.clazz = clazz;
    }

    @Nullable
    public Collection<IssDefinitionPropertyElement> getSectionPropertyList() {
        return PsiTreeUtil.findChildrenOfType(this, IssDefinitionPropertyElement.class);
    }

    @Nullable
    public P getParentSection() {
        return PsiTreeUtil.getParentOfType(this, clazz);
    }
}
