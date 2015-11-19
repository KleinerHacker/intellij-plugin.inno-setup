package org.pcsoft.plugins.intellij.inno_setup.run;

import com.intellij.ide.util.DirectoryChooser;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.options.SettingsEditor;
import com.intellij.openapi.project.ProjectCoreUtil;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.ui.components.panels.VerticalBox;
import com.intellij.util.indexing.FileBasedIndex;
import org.jetbrains.annotations.NotNull;
import org.pcsoft.plugins.intellij.inno_setup.script.IssFileType;

import javax.swing.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssRunSettingsEditor extends SettingsEditor<IssRunConfiguration> {
    private final VerticalBox pnlSettings;
    private final TextFieldWithBrowseButton txtScriptFile;

    public IssRunSettingsEditor() {
        pnlSettings = new VerticalBox();

        txtScriptFile = new TextFieldWithBrowseButton();
        txtScriptFile.setEditable(false);
        txtScriptFile.addActionListener(e -> {
            final List<PsiDirectory> psiDirectories = FileBasedIndex.getInstance().getContainingFiles(
                    FileTypeIndex.NAME, IssFileType.INSTANCE, GlobalSearchScope.allScope(ProjectCoreUtil.theProject))
                    .stream()
                    .filter(item -> item.getExtension() != null)
                    .filter(item -> item.getExtension().toLowerCase().equals("iss"))
                    .map(item -> PsiDirectoryFactory.getInstance(ProjectCoreUtil.theProject).createDirectory(item))
                    .collect(Collectors.toList());

            final DirectoryChooser directoryChooser = new DirectoryChooser(ProjectCoreUtil.theProject);
            directoryChooser.setTitle("Choose Script File");
            directoryChooser.fillList(psiDirectories.toArray(new PsiDirectory[psiDirectories.size()]), null,
                    ProjectCoreUtil.theProject, (String) null);
            if (directoryChooser.showAndGet()) {
                txtScriptFile.setText(directoryChooser.getSelectedDirectory().getVirtualFile().getPath());
            }
        });

        final LabeledComponent<TextFieldWithBrowseButton> lblScriptFile = new LabeledComponent<>();
        lblScriptFile.setText("Script File to compile:");
        lblScriptFile.setComponent(txtScriptFile);

        pnlSettings.add(lblScriptFile);
    }

    @Override
    protected void resetEditorFrom(IssRunConfiguration issRunConfiguration) {
        txtScriptFile.setText(issRunConfiguration.getScriptFile());
    }

    @Override
    protected void applyEditorTo(IssRunConfiguration issRunConfiguration) throws ConfigurationException {
        issRunConfiguration.setScriptFile(txtScriptFile.getText());
    }

    @NotNull
    @Override
    protected JComponent createEditor() {
        return pnlSettings;
    }
}
