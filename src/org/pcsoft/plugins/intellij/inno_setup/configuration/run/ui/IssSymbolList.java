package org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui;

import com.intellij.codeInspection.ui.ListTable;
import com.intellij.openapi.ui.LabeledComponent;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.util.ui.components.BorderLayoutPanel;
import org.apache.commons.lang.ArrayUtils;

import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Created by pfeifchr on 08.12.2015.
 */
public class IssSymbolList extends LabeledComponent<BorderLayoutPanel> {
    private final ListTable lstSymbol;

    public IssSymbolList() {
        lstSymbol = new ListTable(new IssSymbolListModel());
        final JPanel pnlSymbol = ToolbarDecorator.createDecorator(lstSymbol)
                .setAddAction(e -> putDummyData())
                .setRemoveAction(e -> {
                    if (JOptionPane.showConfirmDialog(lstSymbol, "You are sure to remove selected symbol(s)?", "Remove Symbols", JOptionPane.YES_NO_OPTION, JOptionPane.WARNING_MESSAGE) == JOptionPane.YES_OPTION) {
                        removeData(lstSymbol.getModel().getValueAt(lstSymbol.getSelectedRow(), 0).toString());
                    }
                })
                .createPanel();

        final BorderLayoutPanel pnl = new BorderLayoutPanel();
        pnl.addToCenter(pnlSymbol);

        setText("Symbol List:");
        setComponent(pnl);
    }

    public void setData(IssSymbolValue... values) {
        lstSymbol.setModel(new IssSymbolListModel(values));
    }

    public IssSymbolValue[] getData() {
        return ((IssSymbolListModel) lstSymbol.getModel()).getData();
    }

    private void putDummyData() {
        IssSymbolValue[] symbolValues = getData();
        symbolValues = (IssSymbolValue[]) ArrayUtils.add(symbolValues, new IssSymbolValue("DummyName", "DummyValue"));
        setData(symbolValues);
    }

    private void removeData(String name) {
        IssSymbolValue[] symbolValues = getData();
        final List<IssSymbolValue> newSymbolValueList = Stream.of(symbolValues)
                .filter(item -> !item.getName().equals(name))
                .collect(Collectors.toList());
        setData(newSymbolValueList.toArray(new IssSymbolValue[newSymbolValueList.size()]));
    }
}
