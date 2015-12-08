package org.pcsoft.plugins.intellij.inno_setup.configuration.app.ui;

import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.components.panels.VerticalBox;

import javax.swing.JTextField;
import javax.xml.bind.annotation.adapters.HexBinaryAdapter;
import java.awt.Component;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.beans.PropertyChangeListener;

/**
 * Created by pfeifchr on 24.11.2015.
 */
public final class IssAddLanguageDialog {

    public static final class Result {
        private final String languageName, languageNameLocale;
        private final byte[] languageId;
        private final int languageCodePage;

        private Result(String languageName, String languageNameLocale, byte[] languageId, int languageCodePage) {
            this.languageName = languageName;
            this.languageNameLocale = languageNameLocale;
            this.languageId = languageId;
            this.languageCodePage = languageCodePage;
        }

        public String getLanguageName() {
            return languageName;
        }

        public String getLanguageNameLocale() {
            return languageNameLocale;
        }

        public byte[] getLanguageId() {
            return languageId;
        }

        public int getLanguageCodePage() {
            return languageCodePage;
        }
    }

    public static Result show(Component owner) {
        final VerticalBox pnlDialog = new VerticalBox();

        final JTextField txtLanguageName = new JTextField();
        final LabeledComponent<JTextField> pnlLanguageName = new LabeledComponent<>();
        pnlLanguageName.setText("Language English Name (as filename):");
        pnlLanguageName.setComponent(txtLanguageName);
        pnlDialog.add(pnlLanguageName);

        final JTextField txtLanguageNameLocale = new JTextField();
        final LabeledComponent<JTextField> pnlLanguageNameLocale = new LabeledComponent<>();
        pnlLanguageNameLocale.setText("Language Locale Name:");
        pnlLanguageNameLocale.setComponent(txtLanguageNameLocale);
        pnlDialog.add(pnlLanguageNameLocale);

        final JTextField txtLanguageId = new JTextField();
        txtLanguageId.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!("" + e.getKeyChar()).matches("[A-Fa-f0-9]+")) {
                    e.consume();
                }
            }
        });
        final LabeledComponent<JTextField> pnlLanguageId = new LabeledComponent<>();
        pnlLanguageId.setText("Language ID (2 bytes as hex):");
        pnlLanguageId.setComponent(txtLanguageId);
        pnlDialog.add(pnlLanguageId);

        final JTextField txtLanguagePageCode = new JTextField();
        txtLanguagePageCode.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                if (!("" + e.getKeyChar()).matches("[0-9]+")) {
                    e.consume();
                }
            }
        });
        final LabeledComponent<JTextField> pnlLanguagePageCode = new LabeledComponent<>();
        pnlLanguagePageCode.setText("Language Page Code (4 digits):");
        pnlLanguagePageCode.setComponent(txtLanguagePageCode);
        pnlDialog.add(pnlLanguagePageCode);

        final DialogBuilder dialogBuilder = new DialogBuilder();
        dialogBuilder.setTitle("Add Language File");
        dialogBuilder.setCenterPanel(pnlDialog);
        dialogBuilder.addOkAction();
        dialogBuilder.addCancelAction();

        final PropertyChangeListener propertyChangeListener = e -> {
            dialogBuilder.okActionEnabled(txtLanguageName.getText() != null && !txtLanguageName.getText().trim().isEmpty() &&
                    txtLanguageNameLocale.getText() != null && !txtLanguageNameLocale.getText().trim().isEmpty() &&
                    txtLanguageId.getText() != null && !txtLanguageId.getText().trim().isEmpty() &&
                    txtLanguagePageCode.getText() != null && !txtLanguagePageCode.getText().trim().isEmpty());
        };
        txtLanguageName.addPropertyChangeListener(propertyChangeListener);
        txtLanguageNameLocale.addPropertyChangeListener(propertyChangeListener);
        txtLanguageId.addPropertyChangeListener(propertyChangeListener);
        txtLanguagePageCode.addPropertyChangeListener(propertyChangeListener);

        if (dialogBuilder.show() != DialogWrapper.OK_EXIT_CODE)
            return null;

        return new Result(txtLanguageName.getText(), txtLanguageNameLocale.getText(),
                new HexBinaryAdapter().unmarshal((txtLanguageId.getText().length() % 2 != 0 ? "0" : "") + txtLanguageId.getText()),
                Integer.parseInt(txtLanguagePageCode.getText()));
    }

    private IssAddLanguageDialog() {
    }
}
