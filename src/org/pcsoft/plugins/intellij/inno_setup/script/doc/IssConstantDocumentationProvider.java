package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiComment;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import org.apache.commons.lang.ObjectUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.IssCompilerDirectiveSectionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.cd.parameter.IssCompilerDirectiveIdentifierParameterElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssBuiltinConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssConstantNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssEnvironmentConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.constant.IssMessageConstantElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.property.common.IssIdentifierNameElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.MultiResourceBundle;
import org.pcsoft.plugins.intellij.inno_setup.script.types.value.constant.IssBuiltinConstant;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssConstantDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = new MultiResourceBundle(
            ResourceBundle.getBundle("/messages/documentation_constant"), ResourceBundle.getBundle("/messages/documentation_common")
    );

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssCompilerDirectiveIdentifierParameterElement) {
            return getCompilerDirectiveConstantHeadline(originalElement);
        }

        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssBuiltinConstantElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_constant.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssConstantNameElement) {
            final IssConstantNameElement constantNameElement = (IssConstantNameElement) element;

            if (PsiTreeUtil.getParentOfType(element, IssBuiltinConstantElement.class) != null) {
                final IssBuiltinConstant builtinConstant = IssBuiltinConstant.fromId(constantNameElement.getName());
                if (builtinConstant == null)
                    return "Unknown builtin constant";

                return RESOURCE_BUNDLE.getString(builtinConstant.getDescriptionKey());
            } else if (PsiTreeUtil.getParentOfType(element, IssMessageConstantElement.class) != null) {
                if (originalElement instanceof IssIdentifierNameElement) {
                    final IssIdentifierNameElement identifierNameElement = (IssIdentifierNameElement) originalElement;
                    if (identifierNameElement.getParentProperty() != null && identifierNameElement.getParentProperty().getPropertyValue() != null) {
                        return identifierNameElement.getParentProperty().getPropertyValue().getText();
                    }
                }
            } else if (PsiTreeUtil.getParentOfType(element, IssEnvironmentConstantElement.class) != null) {
                return "<b>Environment Variable '" + constantNameElement.getName() + "'</b><br/>" +
                        ObjectUtils.defaultIfNull(System.getenv(constantNameElement.getName()), "NOT FOUND");
            }
        } else if (element instanceof IssCompilerDirectiveIdentifierParameterElement) {
            final IssCompilerDirectiveSectionElement sectionElement = ((IssCompilerDirectiveIdentifierParameterElement) element).getCompilerDirectiveSection();
            if (sectionElement == null)
                return getCompilerDirectiveConstantHeadline(originalElement);

            final StringBuilder sb = new StringBuilder();
            PsiElement currentElement = sectionElement.getPrevSibling();
            while (currentElement != null && currentElement instanceof PsiComment) {
                if (!currentElement.getText().trim().isEmpty()) {
                    sb.insert(0, currentElement.getText().substring(1).trim());
                }
                currentElement = currentElement.getPrevSibling();
            }
            sb.insert(0, getCompilerDirectiveConstantHeadline(originalElement));

            return sb.toString();
        }

        return null;
    }

    @NotNull
    private static String getCompilerDirectiveConstantHeadline(PsiElement originalElement) {
        return "<b>Compiler Directive Constant '<i>" + originalElement.getText() + "</i>'</b><br/>";
    }
}
