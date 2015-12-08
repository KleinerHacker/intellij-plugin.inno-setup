package org.pcsoft.plugins.intellij.inno_setup.script.contributors.completion.cd;

import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.parameter.IssCompilerDirectivePreProcessorTypeParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.value.IssCompilerDirectivePreProcessorType;
import org.pcsoft.plugins.intellij.inno_setup.script.types.cd.value.IssCompilerDirectiveValueIdentifier;

/**
 * Created by pfeifchr on 07.12.2015.
 */
public class IssPreProcessorTypeCompilerDirectiveValueCompletionContributor extends IssAbstractCompilerDirectiveValueCompletionContributor<IssCompilerDirectivePreProcessorTypeParameterElement> {

    public IssPreProcessorTypeCompilerDirectiveValueCompletionContributor() {
        super(IssCompilerDirectivePreProcessorTypeParameterElement.class);
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
        return IssCompilerDirectivePreProcessorType.values();
    }
}
