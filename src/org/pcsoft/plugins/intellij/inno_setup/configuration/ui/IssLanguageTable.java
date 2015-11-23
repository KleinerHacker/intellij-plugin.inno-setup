package org.pcsoft.plugins.intellij.inno_setup.configuration.ui;

import com.intellij.codeInspection.ui.ListTable;
import com.intellij.ui.ToolbarDecorator;

import javax.swing.*;

/**
 * Created by Christoph on 23.11.2015.
 */
public class IssLanguageTable extends ListTable {
    public static JPanel createTable() {
        final IssLanguageTable table = new IssLanguageTable();
        final ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(table)
                .setAddAction(e -> {

                })
                .setRemoveAction(e -> {

                })
                .setEditAction(e -> {
                    final String newName = JOptionPane.showInputDialog("Enter a new ISL file name:", table.getValueAt(table.getSelectedRow(), 0));
                    if (newName != null) {
                        //TODO
                    }
                });
        return toolbarDecorator.createPanel();
    }

    private IssLanguageTable() {
        super(new IssLanguageTableModel());
    }

    public void refresh() {
        setModel(new IssLanguageTableModel());
    }
}
