package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.MultiResourceBundle;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssCompilerDirectiveDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_compiler_directive"), ResourceBundle.getBundle("/messages/documentation_common")
    );

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssCompilerDirectiveSectionElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_compilerdirective.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssCompilerDirectiveElement) {
            final IssCompilerDirectiveElement compilerDirectiveElement = (IssCompilerDirectiveElement) element;
            final IssCompilerDirectiveSectionElement sectionElement = compilerDirectiveElement.getCompilerDirectiveSection();
            if (sectionElement == null)
                return "Unknown section type";

            return RESOURCE_BUNDLE.getString(sectionElement.getSectionType().getDescriptionKey());
        }

        return null;
    }
}
