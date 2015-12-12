package org.pcsoft.plugins.intellij.inno_setup.ui;

import com.intellij.codeInspection.ui.ListWrappingTableModel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by pfeifchr on 09.12.2015.
 */
public class IssKeyValueTableModel extends ListWrappingTableModel {
    private static List<List<String>> convert(Map<String, String> map) {
        final List<String> keyList = new ArrayList<>(), valueList = new ArrayList<>();
        if (map == null || map.isEmpty())
            return Arrays.asList(keyList, valueList);

        for (final Map.Entry<String, String> entry : map.entrySet()) {
            keyList.add(entry.getKey());
            valueList.add(entry.getValue());
        }

        return Arrays.asList(keyList, valueList);
    }

    public IssKeyValueTableModel(Map<String, String> map) {
        super(convert(map), "Key", "Value");
    }

    @Override
    public boolean isCellEditable(int rowIndex, int columnIndex) {
        return false;
    }
}
