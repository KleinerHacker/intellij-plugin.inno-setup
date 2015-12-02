package org.pcsoft.plugins.intellij.inno_setup.configuration.ui;

import com.intellij.codeInspection.ui.ListTable;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.ActionToolbarPosition;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CustomShortcutSet;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.ProjectCoreUtil;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.JBColor;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.util.IconUtil;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.apache.commons.lang.StringUtils;
import org.pcsoft.plugins.intellij.inno_setup.configuration.IssCompilerSettings;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssLanguageType;
import org.pcsoft.plugins.intellij.inno_setup.utils.IssEditorUtils;
import org.pcsoft.plugins.intellij.inno_setup.vfs.IssVirtualFile;
import org.pcsoft.plugins.intellij.inno_setup.vfs.IssVirtualFileSystem;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.KeyStroke;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
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
    private static final int ICON_SIZE = 20;

    public static enum ViewType {
        Normal,
        Simple
    }

    private static ActionToolbarPosition convert(ViewType viewType) {
        switch (viewType) {
            case Normal:
                return ActionToolbarPosition.RIGHT;
            case Simple:
                return ActionToolbarPosition.TOP;
            default:
                throw new RuntimeException();
        }
    }

    private ListTable tbl;
    private ViewType viewType;

    public IssLanguageTable(ViewType viewType, final boolean supportOpenFile) {
        this.tbl = new ListTable(new IssLanguageTableModel(viewType));
        this.tbl.getColumnModel().getColumn(0).setMaxWidth(ICON_SIZE);
        this.tbl.setDefaultRenderer(String.class, (table, value, isSelected, hasFocus, row, column) -> {
            if (column > 0) {
                final JLabel label = new JLabel(value.toString()) {
                    @Override
                    public void paint(Graphics g) {
                        if (isSelected) {
                            g.setColor(JBColor.BLUE);
                            g.fillRect(0, 0, g.getClipBounds().width, g.getClipBounds().height);
                        }
                        super.paint(g);
                    }
                };
                label.setBorder(BorderFactory.createEmptyBorder(1, 1, 1, 1));
                if (hasFocus) {
                    label.setBorder(BorderFactory.createLineBorder(JBColor.BLUE));
                }
                if (isSelected) {
                    label.setForeground(JBColor.WHITE);
                }

                return label;
            }

            final IssLanguageType languageType = IssLanguageType.findByFile(value.toString());
            if (languageType == null || languageType.getFlagIcon() == null) {
                return new JLabel();
            }

            return new BorderLayoutPanel() {
                @Override
                public void paint(Graphics g) {
                    super.paint(g);
                    g.drawImage(IconUtil.toImage(languageType.getFlagIcon()), 0, 0, null);
                }
            };
        });
        this.tbl.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (!supportOpenFile)
                    return;

                if (e.getButton() == MouseEvent.BUTTON1 && e.getClickCount() == 2 && tbl.getSelectedRowCount() == 1) {
                    handleOpenAction();
                }
            }
        });
        this.viewType = viewType;

        final ToolbarDecorator toolbarDecorator = ToolbarDecorator.createDecorator(tbl)
                .setToolbarPosition(convert(viewType))
                .setAddAction(e -> handleAddAction())
                .setRemoveAction(e -> handleRemoveAction())
                .setEditAction(e -> handleEditAction())
                .addExtraAction(new AnActionButton("Refresh", AllIcons.Actions.Refresh) {
                    {
                        setShortcut(new CustomShortcutSet(KeyStroke.getKeyStroke(KeyEvent.VK_F5, 0)));
                    }

                    @Override
                    public void actionPerformed(AnActionEvent anActionEvent) {
                        refresh();
                    }
                });
        addToCenter(toolbarDecorator.createPanel());
    }

    public void refresh() {
        tbl.setModel(new IssLanguageTableModel(viewType));
        tbl.getColumnModel().getColumn(0).setMaxWidth(ICON_SIZE);
    }

    public ViewType getViewType() {
        return viewType;
    }

    public void setViewType(ViewType viewType) {
        this.viewType = viewType;
        refresh();
    }

    private void handleOpenAction() {
        final String fileName = tbl.getValueAt(tbl.getSelectedRow(), 1).toString();
        final File file = new File(SETTINGS.getLanguagesPath(), fileName);
        if (!file.exists()) {
            JOptionPane.showMessageDialog(tbl, "Cannot find file!", "Unable open", JOptionPane.ERROR_MESSAGE);
            return;
        }

        IssEditorUtils.openFile(ProjectCoreUtil.theProject, new IssVirtualFile(file, IssVirtualFileSystem.getInstance()));
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
                        line = line.replace("$LAN_NAME_EN$", result.getLanguageName())
                                .replace("$LAN_NAME_LOCALE$", result.getLanguageNameLocale())
                                .replace("$LAN_ID$", StringUtils.leftPad(new HexBinaryAdapter().marshal(result.getLanguageId()), 4, '0'))
                                .replace("$LAN_CODEPAGE$", String.valueOf(result.getLanguageCodePage()));
                        //TODO: <XXXX>
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
