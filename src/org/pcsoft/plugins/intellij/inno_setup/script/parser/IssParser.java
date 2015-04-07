package org.pcsoft.plugins.intellij.inno_setup.script.parser;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiParser;
import com.intellij.psi.tree.IElementType;
import org.jetbrains.annotations.NotNull;

/**
 * Created by Christoph on 14.12.2014.
 */
public class IssParser implements PsiParser {

    @NotNull
    @Override
    public ASTNode parse(IElementType iElementType, PsiBuilder psiBuilder) {
        psiBuilder.setDebugMode(true);

        final PsiBuilder.Marker rootMark = psiBuilder.mark();

        IssParserSectionUtility.parseSections(psiBuilder);

        rootMark.done(IssMarkerFactory.ISS_FILE);

        return psiBuilder.getTreeBuilt();
    }


}
