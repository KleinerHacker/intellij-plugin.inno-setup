package org.pcsoft.plugins.intellij.inno_setup.configuration.ui;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowContentUiType;
import com.intellij.openapi.wm.ToolWindowFactoryEx;
import com.intellij.ui.content.impl.ContentImpl;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;

/**
 * Created by Christoph on 24.11.2015.
 */
public class IssToolWindowFactory implements ToolWindowFactoryEx {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.getContentManager().addContent(new ContentImpl(new IssLanguageTable(IssLanguageTable.ViewType.Simple, true), "Languages Overview", true));
    }

    @Override
    public void init(ToolWindow toolWindow) {
        toolWindow.setIcon(IssIcons.INNO_SETUP);
        toolWindow.setTitle("Inno Setup Languages");
        toolWindow.setShowStripeButton(true);
        toolWindow.setContentUiType(ToolWindowContentUiType.COMBO, () -> {});
    }
}
