package org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file;

import com.intellij.lang.ASTNode;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssDefinitionPropertyElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.IssItemValueType;

/**
 * Created by Christoph on 23.12.2014.
 */
public class IssFilePropertyDestDirElement extends IssDefinitionPropertyElement<IssFileDefinitionElement,IssFilePropertyDestDirValueElement> {
    public IssFilePropertyDestDirElement(ASTNode node) {
        super(node, IssFileDefinitionElement.class,IssFilePropertyDestDirValueElement.class);
    }

    @NotNull
    @Override
    public IssItemValueType getItemValueType() {
        return IssItemValueType.String;
    }
}
