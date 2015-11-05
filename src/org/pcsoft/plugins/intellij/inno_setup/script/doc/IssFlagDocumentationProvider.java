package org.pcsoft.plugins.intellij.inno_setup.script.doc;

import com.intellij.lang.documentation.AbstractDocumentationProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiManager;
import com.intellij.psi.util.PsiTreeUtil;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.component.IssComponentDefinitionNameValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.file.IssFileDefinitionFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.task.IssTaskDefinitionFlagsValueElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionElement;
import org.pcsoft.plugins.intellij.inno_setup.script.parser.psi.elements.sections.type.IssTypeDefinitionFlagsValueElement;
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
        if (element instanceof IssComponentDefinitionNameValueElement) {

            final IssComponentDefinitionNameValueElement nameValueElement = (IssComponentDefinitionNameValueElement) element;
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
        if (element instanceof IssTypeDefinitionFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_typessection.htm");
        } else if (element instanceof IssTaskDefinitionFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_taskssection.htm");
        } else if (element instanceof IssComponentDefinitionFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_componentssection.htm");
        } else if (element instanceof IssFileDefinitionFlagsValueElement) {
            return Arrays.asList("http://www.jrsoftware.org/ishelp/topic_filessection.htm");
        }

        return null;
    }

    @Override
    public String generateDoc(PsiElement element, PsiElement originalElement) {
        try {
            Thread.sleep(1500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        if (element instanceof IssTypeDefinitionFlagsValueElement) {
            final IssTypeDefinitionFlagsValueElement typeDefinitionFlagsValueElement = (IssTypeDefinitionFlagsValueElement) element;
            final IssTypeFlag typeFlag = IssTypeFlag.fromId(typeDefinitionFlagsValueElement.getName());
            if (typeFlag == null)
                return UNKNOWN;

            return RESOURCE_BUNDLE.getString(typeFlag.getDescriptionKey());
        } else if (element instanceof IssTaskDefinitionFlagsValueElement) {
            final IssTaskDefinitionFlagsValueElement taskDefinitionFlagsValueElement = (IssTaskDefinitionFlagsValueElement) element;
            final IssTaskFlag taskFlag = IssTaskFlag.fromId(taskDefinitionFlagsValueElement.getName());
            if (taskFlag == null)
                return UNKNOWN;

            return RESOURCE_BUNDLE.getString(taskFlag.getDescriptionKey());
        } else if (element instanceof IssComponentDefinitionFlagsValueElement) {
            final IssComponentDefinitionFlagsValueElement componentDefinitionFlagsValueElement = (IssComponentDefinitionFlagsValueElement) element;
            final IssComponentFlag componentFlag = IssComponentFlag.fromId(componentDefinitionFlagsValueElement.getName());
            if (componentFlag == null)
                return UNKNOWN;

            return RESOURCE_BUNDLE.getString(componentFlag.getDescriptionKey());
        } else if (element instanceof IssFileDefinitionFlagsValueElement) {
            final IssFileDefinitionFlagsValueElement fileDefinitionFlagsValueElement = (IssFileDefinitionFlagsValueElement) element;
            final IssFileFlag fileFlag = IssFileFlag.fromId(fileDefinitionFlagsValueElement.getName());
            if (fileFlag == null)
                return UNKNOWN;

            return RESOURCE_BUNDLE.getString(fileFlag.getDescriptionKey());
        }

        return null;
    }
}
