package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentPropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentPropertyNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFilePropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskPropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypePropertyFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssComponentFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssFileFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTaskFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssTypeFlag;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssComponentUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssFileUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssTaskUtils;
import org.pcsoft.plugins.intellij.inno_setup.script.utils.IssTypeUtils;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * Created by Christoph on 28.12.2014.
 * <a href="Bla" target="_self">Bla</link>
 */
public class IssFlagDocumentationProvider extends AbstractDocumentationProvider {

    private static final ResourceBundle RESOURCE_BUNDLE = ResourceBundle.getBundle("/messages/documentation_flag");
    private static final String UNKNOWN = "Unknown flag";

    @Nullable
    @Override
    public String getQuickNavigateInfo(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssComponentPropertyNameValueElement) {

            final IssComponentPropertyNameValueElement nameValueElement = (IssComponentPropertyNameValueElement) element;
            return "Reference to component: " + nameValueElement.getName() + "<br/>" +
                    "Component Name: " + nameValueElement.getValueParent().getDefinition().getComponentDescription().getDescriptionValue().getText();
        }

        return super.getQuickNavigateInfo(element, originalElement);
    }

    @Override
    public PsiElement getDocumentationElementForLookupItem(PsiManager psiManager, Object object, PsiElement element) {
        if (PsiTreeUtil.getParentOfType(element, IssTypeDefinitionElement.class) != null) {
            return IssTypeUtils.createFlagValue(element.getProject(), object.toString());
        } else if (PsiTreeUtil.getParentOfType(element, IssTaskDefinitionElement.class) != null) {
            return IssTaskUtils.createFlagValue(element.getProject(), object.toString());
        } else if (PsiTreeUtil.getParentOfType(element, IssComponentDefinitionElement.class) != null) {
            return IssComponentUtils.createFlagValue(element.getProject(), object.toString());
        } else if (PsiTreeUtil.getParentOfType(element, IssFileDefinitionElement.class) != null) {
            return IssFileUtils.createFlagValue(element.getProject(), object.toString());
        }

        return null;
    }

    @Override
    public List<String> getUrlFor(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssTypePropertyFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_typessection.htm");
        } else if (element instanceof IssTaskPropertyFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_taskssection.htm");
        } else if (element instanceof IssComponentPropertyFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_componentssection.htm");
        } else if (element instanceof IssFilePropertyFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_filessection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        if (element instanceof IssTypePropertyFlagsValueElement) {
            final IssTypePropertyFlagsValueElement typeDefinitionFlagsValueElement = (IssTypePropertyFlagsValueElement) element;
            final IssTypeFlag typeFlag = IssTypeFlag.fromId(typeDefinitionFlagsValueElement.getName());
            if (typeFlag == null)
                return UNKNOWN;

            return RESOURCE_BUNDLE.getString(typeFlag.getDescriptionKey());
        } else if (element instanceof IssTaskPropertyFlagsValueElement) {
            final IssTaskPropertyFlagsValueElement taskDefinitionFlagsValueElement = (IssTaskPropertyFlagsValueElement) element;
            final IssTaskFlag taskFlag = IssTaskFlag.fromId(taskDefinitionFlagsValueElement.getName());
            if (taskFlag == null)
                return UNKNOWN;

            return RESOURCE_BUNDLE.getString(taskFlag.getDescriptionKey());
        } else if (element instanceof IssComponentPropertyFlagsValueElement) {
            final IssComponentPropertyFlagsValueElement componentDefinitionFlagsValueElement = (IssComponentPropertyFlagsValueElement) element;
            final IssComponentFlag componentFlag = IssComponentFlag.fromId(componentDefinitionFlagsValueElement.getName());
            if (componentFlag == null)
                return UNKNOWN;

            return RESOURCE_BUNDLE.getString(componentFlag.getDescriptionKey());
        } else if (element instanceof IssFilePropertyFlagsValueElement) {
            final IssFilePropertyFlagsValueElement fileDefinitionFlagsValueElement = (IssFilePropertyFlagsValueElement) element;
            final IssFileFlag fileFlag = IssFileFlag.fromId(fileDefinitionFlagsValueElement.getName());
            if (fileFlag == null)
                return UNKNOWN;

            return RESOURCE_BUNDLE.getString(fileFlag.getDescriptionKey());
        }

        return null;
    }
}
