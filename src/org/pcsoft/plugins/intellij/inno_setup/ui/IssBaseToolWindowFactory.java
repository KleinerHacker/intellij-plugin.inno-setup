package org.pcsoft.plugins.intellij.inno_setup.ui;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowContentUiType;
import com.intellij.openapi.wm.ToolWindowFactoryEx;
import com.intellij.ui.content.impl.ContentImpl;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.configuration.app.IssCompilerSettings;
import org.pcsoft.plugins.intellij.inno_setup.configuration.app.ui.IssLanguageTable;

/**
 * Created by Christoph on 24.11.2015.
 */
public class IssBaseToolWindowFactory implements ToolWindowFactoryEx {
    private static final IssCompilerSettings COMPILER_SETTINGS = ServiceManager.getService(IssCompilerSettings.class);

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        toolWindow.getContentManager().addContent(new ContentImpl(new IssLanguageTable(IssLanguageTable.ViewType.Simple, true), "Languages", true));
        toolWindow.getContentManager().addContent(new ContentImpl(new IssLanguageContentPanel(), "Messages", true));
    }

    @Override
    public void init(ToolWindow toolWindow) {
        toolWindow.setIcon(IssIcons.INNO_SETUP);
        toolWindow.setTitle("Inno Setup");
        toolWindow.setShowStripeButton(true);
        toolWindow.setContentUiType(ToolWindowContentUiType.COMBO, () -> {});
    }
}
