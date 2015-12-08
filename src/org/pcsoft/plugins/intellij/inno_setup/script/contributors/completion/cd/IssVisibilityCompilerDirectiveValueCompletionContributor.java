package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.cd;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.parameter.IssCompilerDirectiveVisibilityParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.value.IssCompilerDirectiveValueIdentifier;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.value.IssCompilerDirectiveVisibility;

/**
 * Created by pfeifchr on 07.12.2015.
 */
public class IssVisibilityCompilerDirectiveValueCompletionContributor extends IssAbstractCompilerDirectiveValueCompletionContributor<IssCompilerDirectiveVisibilityParameterElement> {

    public IssVisibilityCompilerDirectiveValueCompletionContributor() {
        super(IssCompilerDirectiveVisibilityParameterElement.class);
    }

//    @NotNull
//    @Override
//    protected ElementPattern<? extends PsiElement> getElementPattern(Class<IssCompilerDirectiveVisibilityParameterElement> elementClass) {
//        return PlatformPatterns.psiElement().withLanguage(IssLanguage.INSTANCE).with(new PatternCondition<PsiElement>(IssVisibilityCompilerDirectiveValueCompletionContributor.class.getSimpleName()) {
//            @Override
//            public boolean accepts(@NotNull PsiElement element, ProcessingContext processingContext) {
//                return element.getPrevSibling() instanceof IssCompilerDirectiveIdentifierElement ||
//                        element instanceof IssCompilerDirectiveIdentifierParameterElement ||
//                        element.getNextSibling() instanceof IssCompilerDirectiveIdentifierParameterElement;
//            }
//        });
//    }

    @NotNull
    @Override
    protected IssCompilerDirectiveValueIdentifier[] getValueList() {
        return IssCompilerDirectiveVisibility.values();
    }
}
