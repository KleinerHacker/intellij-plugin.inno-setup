package org.pcsoft.plugins.intellij.inno_setup.configuration.ui;

import com.intellij.codeInspection.ui.ListTable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.pcsoft.plugins.intellij.inno_setup.configuration.IssCompilerSettings;

import javax.swing.JOptionPane;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

/**
 * Created by Christoph on 23.11.2015.
 */
public class IssLanguageTable extends BorderLayoutPanel {
    private static final IssCompilerSettings SETTINGS = ServiceManager.getService(IssCompilerSettings.class);

    private ListTable tbl;

    public IssLanguageTable() {

        this.tbl = new ListTable(new IssLanguageTableModel());

        final ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(tbl)
                .setAddAction(e -> handleAddAction())
                .setRemoveAction(e -> handleRemoveAction())
                .setEditAction(e -> handleEditAction())
                .addExtraAction(new AnActionButton("Refresh", AllIcons.Actions.Refresh) {
                    @Override
                    public void actionPerformed(AnActionEvent anActionEvent) {
                        refresh();
                    }
                });
        addToCenter(toolbarDecorator.createPanel());
    }

    public void refresh() {
        tbl.setModel(new IssLanguageTableModel());
    }

    private void handleAddAction() {
        final IssAddLanguageDialog.Result result = IssAddLanguageDialog.show(tbl);
        if (result != null) {
            final File file = new File(SETTINGS.getLanguagesPath(), result.getLanguageName() + ".isl");
            if (file.exists()) {
                JOptionPane.showMessageDialog(tbl, "Language file with name '" + file.getName() + "' already exists!", "Cannot add language file", JOptionPane.ERROR_MESSAGE);
                return;
            }

            try (final Scanner scanner = new Scanner(getClass().getResourceAsStream("/templates/template.isl"))) {
                try (final OutputStream out = new FileOutputStream(file, false)) {
                    String line = null;
                    while ((line = scanner.nextLine()) != null) {
                        //TODO: Replace place holder
                        out.write(line.getBytes("Unicode"));
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(tbl, "Unable to create language file: " + e.getMessage(), "Cannot add language file", JOptionPane.ERROR_MESSAGE);
                return;
            }

            refresh();
        }
    }

    private void handleRemoveAction() {
        final String originalFileName = tbl.getValueAt(tbl.getSelectedRow(), 0).toString();
        final String newFileName = originalFileName + ".orig";

        if (JOptionPane.showConfirmDialog(tbl, "Are you sure to remove selected language file(s)?", "Remove language file(s)",
                JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
            final File originalFile = new File(SETTINGS.getLanguagesPath(), originalFileName);
            if (!originalFile.exists()) {
                JOptionPane.showMessageDialog(tbl, "Cannot find file!", "Unable removing", JOptionPane.ERROR_MESSAGE);
                return;
            }
            File newFile = new File(SETTINGS.getLanguagesPath(), newFileName);
            int counter = 0;
            while (newFile.exists()) {
                counter++;
                newFile = new File(SETTINGS.getLanguagesPath(), newFileName + counter);
            }
            if (!originalFile.renameTo(newFile)) {
                JOptionPane.showMessageDialog(tbl, "Removing failed!", "Unable removing", JOptionPane.ERROR_MESSAGE);
                return;
            }

            refresh();
        }
    }

    private void handleEditAction() {
        final String originalFileName = tbl.getValueAt(tbl.getSelectedRow(), 0).toString();

        final String newFileName = JOptionPane.showInputDialog(tbl, "Enter a new ISL file name:", originalFileName);
        if (newFileName != null) {
            if (!newFileName.toLowerCase().endsWith(".isl")) {
                JOptionPane.showMessageDialog(tbl, "The filename must end on 'isl'.", "Unable to rename", JOptionPane.ERROR_MESSAGE);
                return;
            }

            final File originalFile = new File(SETTINGS.getLanguagesPath(), originalFileName);
            if (!originalFile.exists()) {
                JOptionPane.showMessageDialog(tbl, "Cannot find file!", "Unable to rename", JOptionPane.ERROR_MESSAGE);
                return;
            }
            final File newFile = new File(SETTINGS.getLanguagesPath(), newFileName);
            if (newFile.exists()) {
                JOptionPane.showMessageDialog(tbl, "File with new name already exists!", "Unable to rename", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (!originalFile.renameTo(newFile)) {
                JOptionPane.showMessageDialog(tbl, "Renaming failed!", "Unable to rename", JOptionPane.ERROR_MESSAGE);
                return;
            }

            refresh();
        }
    }
}
