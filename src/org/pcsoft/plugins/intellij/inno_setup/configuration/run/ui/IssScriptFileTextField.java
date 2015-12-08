package org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui;

import com.intellij.ide.util.DirectoryChooser;
import com.intellij.openapi.project.ProjectCoreUtil;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import com.intellij.psi.PsiDirectory;
import com.intellij.psi.impl.file.PsiDirectoryFactory;
import com.intellij.psi.search.FileTypeIndex;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.indexing.FileBasedIndex;
import org.pcsoft.plugins.intellij.inno_setup.script.IssScriptFileType;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by pfeifchr on 08.12.2015.
 */
public class IssScriptFileTextField extends LabeledComponent<TextFieldWithBrowseButton> {
    private final TextFieldWithBrowseButton txtScriptFile;

    public IssScriptFileTextField() {
        txtScriptFile = new TextFieldWithBrowseButton();
        txtScriptFile.setEditable(false);
        txtScriptFile.addActionListener(e -> {
            final List<PsiDirectory> psiDirectories = FileBasedIndex.getInstance().getContainingFiles(
                    FileTypeIndex.NAME, IssScriptFileType.INSTANCE, GlobalSearchScope.allScope(ProjectCoreUtil.theProject))
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

        setText("Script File to compile:");
        setComponent(txtScriptFile);
    }

    public String getValue() {
        return txtScriptFile.getText();
    }

    public void setValue(String text) {
        txtScriptFile.setText(text);
    }
}
