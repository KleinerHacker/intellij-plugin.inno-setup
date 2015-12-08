package org.pcsoft.plugins.intellij.inno_setup.configuration.run.ui;

import com.intellij.codeInspection.ui.ListWrappingTableModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by pfeifchr on 08.12.2015.
 */
public class IssSymbolListModel extends ListWrappingTableModel {
    private static List<List<String>> convert(IssSymbolValue... values) {
        final List<String> nameList = new ArrayList<>(), valueList = new ArrayList<>();
        if (values == null)
            return Arrays.asList(nameList, valueList);

        for (final IssSymbolValue value : values) {
            nameList.add(value.getName());
            valueList.add(value.getValue());
        }

        return Arrays.asList(nameList, valueList);
    }

    private static IssSymbolValue[] convert(List<String> nameList, List<String> valueList) {
        if (nameList == null)
            throw new IllegalArgumentException("nameList = null");
        if (valueList == null)
            throw new IllegalArgumentException("valueList = null");
        if (nameList.size() != valueList.size())
            throw new IllegalArgumentException("nameList (size) != valueList (size)");

        final List<IssSymbolValue> list = new ArrayList<>();
        for (int i=0; i<nameList.size(); i++) {
            list.add(new IssSymbolValue(nameList.get(i), valueList.get(i)));
        }

        return list.toArray(new IssSymbolValue[list.size()]);
    }

    public IssSymbolListModel(IssSymbolValue... values) {
        super(convert(values), "Name", "Value");
    }

    public IssSymbolValue[] getData() {
        final List<String> nameList = new ArrayList<>();
        for (int i=0; i<getRowCount(); i++) {
            nameList.add(getValueAt(i, 0).toString());
        }

        final List<String> valueList = new ArrayList<>();
        for (int i=0; i<getRowCount(); i++) {
            valueList.add(getValueAt(i, 1).toString());
        }

        return convert(nameList, valueList);
    }
}
