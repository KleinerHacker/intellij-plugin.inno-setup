package org.pcsoft.plugins.intellij.inno_setup.script.others;

import com.intellij.codeInsight.editorActions.moveUpDown.LineMover;
import com.intellij.codeInsight.editorActions.moveUpDown.LineRange;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.IssScriptFile;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.IssSectionNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierElement;

/**
 * Created by pfeifchr on 23.11.2015.
 */
public class IssStatementUpDownMover extends LineMover {

    @Override
    public boolean checkAvailable(Editor editor, PsiFile psiFile, MoveInfo moveInfo, boolean down) {
        if (!(psiFile instanceof IssScriptFile))
            return false;
        if (!super.checkAvailable(editor, psiFile, moveInfo, down))
            return false;

        final Pair<PsiElement, PsiElement> pair = LineMover.getElementRange(editor, psiFile, moveInfo.toMove);
        if (pair == null)
            return false;

        if (PsiTreeUtil.getParentOfType(pair.first, IssIdentifierElement.class) != null) {
            return true;
        } else if (PsiTreeUtil.getParentOfType(pair.first, IssSectionNameElement.class) != null) {
            final IssSectionElement sectionElement = PsiTreeUtil.getParentOfType(pair.first, IssSectionElement.class);
            if (sectionElement == null)
                return false;

            final PsiElement sibling;
            if (down) {
                sibling = sectionElement.getNextSibling();
            } else {
                sibling = sectionElement.getPrevSibling();
            }
            if (sibling == null)
                return false;

            moveInfo.toMove2 = new LineRange(
                    editor.offsetToLogicalPosition(sibling.getTextRange().getStartOffset()).line,
                    editor.offsetToLogicalPosition(sibling.getTextRange().getEndOffset()).line
            );

            return true;
        }

        return false;
    }
}
