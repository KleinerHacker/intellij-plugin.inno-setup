package org.pcsoft.plugins.intellij.inno_setup.ui;

import com.intellij.openapi.ui.ComboBox;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.Splitter;
import com.intellij.openapi.util.io.FileUtil;
import com.intellij.ui.JBColor;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.jdesktop.swingx.combobox.ListComboBoxModel;
import org.pcsoft.plugins.intellij.inno_setup.script.types.IssLanguageType;
import org.pcsoft.plugins.intellij.inno_setup.utils.IssLanguageUtils;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import java.awt.Graphics;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * Created by pfeifchr on 09.12.2015.
 */
public class IssLanguageContentPanel extends BorderLayoutPanel {
    private static final class LanguageItem {
        private static enum State {
            Message,
            CustomMessage
        }

        private final String name;
        private final Map<String, String> messageMap = new HashMap<>(), customMessageMap = new HashMap<>();

        private LanguageItem(File file) {
            this.name = FileUtil.getNameWithoutExtension(file);

            try (final Scanner scanner = new Scanner(new FileInputStream(file))) {
                State state = null;
                String line = null;
                try {
                    while ((line = scanner.nextLine()) != null) {
                        if (line.trim().isEmpty())
                            continue;
                        if (line.trim().equalsIgnoreCase("[messages]")) {
                            state = State.Message;
                        } else if (line.trim().equalsIgnoreCase("[customMessages]")) {
                            state = State.CustomMessage;
                        } else if (line.trim().startsWith("[") && line.trim().endsWith("]")) {
                            state = null;
                        } else if (state != null) {
                            final String[] parts = line.split("=");
                            if (parts.length != 2)
                                continue;

                            switch (state) {
                                case Message:
                                    messageMap.put(parts[0], parts[1]);
                                    break;
                                case CustomMessage:
                                    customMessageMap.put(parts[0], parts[1]);
                                    break;
                                default:
                                    throw new RuntimeException();
                            }
                        }
                    }
                } catch (NoSuchElementException e) {
                    //Do nothing, normal end, no more line
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public String toString() {
            return name;
        }
    }

    private final ComboBox cmbLanguage = new ComboBox();
    private final IssKeyValueTable tblMessages = new IssKeyValueTable();
    private final IssKeyValueTable tblCustomMessages = new IssKeyValueTable();

    public IssLanguageContentPanel() {
        cmbLanguage.setRenderer((list, value, index, isSelected, cellHasFocus) -> {
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
            if (cellHasFocus) {
                label.setBorder(BorderFactory.createLineBorder(JBColor.BLUE));
            }
            if (isSelected) {
                label.setForeground(JBColor.WHITE);
            }

            final IssLanguageType languageType = IssLanguageType.findByName(value.toString());
            if (languageType != null && languageType.getFlagIcon() != null) {
                label.setIcon(languageType.getFlagIcon());
            }

            return label;
        });
        cmbLanguage.setModel(new ListComboBoxModel<>(
                IssLanguageUtils.findLanguageFiles().stream().map(LanguageItem::new).collect(Collectors.toList())
        ));
        cmbLanguage.addItemListener(e -> {
            if (e.getItem() == null)
                return;

            tblMessages.updateData(((LanguageItem) e.getItem()).messageMap);
            tblCustomMessages.updateData(((LanguageItem) e.getItem()).customMessageMap);
        });
        if (cmbLanguage.getModel().getSize() > 0) {
            cmbLanguage.setSelectedIndex(-1);
            cmbLanguage.setSelectedIndex(0);
        }

        final LabeledComponent<ComboBox> pnlLanguages = new LabeledComponent<>();
        pnlLanguages.setText("Installed Languages:");
        pnlLanguages.setComponent(cmbLanguage);

        final LabeledComponent<JBScrollPane> pnlMessages = new LabeledComponent<>();
        pnlMessages.setText("Messages");
        pnlMessages.setComponent(new JBScrollPane(tblMessages));

        final LabeledComponent<JBScrollPane> pnlCustomMessages = new LabeledComponent<>();
        pnlCustomMessages.setText("Custom Messages");
        pnlCustomMessages.setComponent(new JBScrollPane(tblCustomMessages));

        final Splitter pnlSplitter = new Splitter(true, .7f);
        pnlSplitter.setFirstComponent(pnlMessages);
        pnlSplitter.setSecondComponent(pnlCustomMessages);

        addToTop(pnlLanguages);
        addToCenter(pnlSplitter);
    }
}
