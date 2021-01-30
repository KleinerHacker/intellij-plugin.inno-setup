package org.pcsoft.plugins.intellij.iss.core.util;

import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.components.panels.HorizontalBox;
import com.intellij.ui.components.panels.VerticalBox;

import javax.swing.*;

public final class IssUiUtils {
    public static <T extends JComponent>LabeledComponent<T> createLabeledComponent(String text, T comp) {
        final LabeledComponent<T> labeledComponent = new LabeledComponent<>();
        labeledComponent.setText(text);
        labeledComponent.setComponent(comp);

        return labeledComponent;
    }

    public static HorizontalBox createHorizontalBox(JComponent... comps) {
        final HorizontalBox pnl = new HorizontalBox();
        for (JComponent comp : comps) {
            pnl.add(comp);
        }

        return pnl;
    }

    public static VerticalBox createVerticalBox(JComponent... comps) {
        final VerticalBox pnl = new VerticalBox();
        for (JComponent comp : comps) {
            pnl.add(comp);
        }

        return pnl;
    }

    private IssUiUtils() {
    }
}
