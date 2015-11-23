package org.pcsoft.plugins.intellij.inno_setup.configuration.ui;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.openapi.ui.TextFieldWithBrowseButton;
import org.pcsoft.plugins.intellij.inno_setup.configuration.IssCompilerSettings;

import java.awt.event.InputMethodEvent;
import java.awt.event.InputMethodListener;
import java.util.function.Consumer;

/**
 * Created by Christoph on 23.11.2015.
 */
public class IssLanguagesPathTextField extends LabeledComponent<TextFieldWithBrowseButton> {
    final TextFieldWithBrowseButton txtLanguagesPath = new TextFieldWithBrowseButton();

    public IssLanguagesPathTextField(Consumer<Boolean> modificationCallback) {
        txtLanguagesPath.getButton().setIcon(AllIcons.Actions.Reset_to_default);
        txtLanguagesPath.addActionListener(e -> {
            txtLanguagesPath.setText(IssCompilerSettings.DEFAULT_LANGUAGES_PATH);
            modificationCallback.accept(true);
        });
        txtLanguagesPath.getTextField().addInputMethodListener(new InputMethodListener() {
            @Override
            public void inputMethodTextChanged(InputMethodEvent event) {
                modificationCallback.accept(true);
            }

            @Override
            public void caretPositionChanged(InputMethodEvent event) {

            }
        });

        setText("Path of installed languages (relative to Inno Setup installation path):");
        setComponent(txtLanguagesPath);
    }

    public void setValue(String text) {
        txtLanguagesPath.setText(text);
    }

    public String getValue() {
        return txtLanguagesPath.getText();
    }
}
