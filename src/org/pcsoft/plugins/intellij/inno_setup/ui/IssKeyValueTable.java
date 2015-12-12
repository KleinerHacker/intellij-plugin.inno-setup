package org.pcsoft.plugins.intellij.inno_setup.ui;

import com.intellij.codeInspection.ui.ListTable;

import java.util.Map;

/**
 * Created by pfeifchr on 09.12.2015.
 */
public class IssKeyValueTable extends ListTable {

    public IssKeyValueTable() {
        super(new IssKeyValueTableModel(null));
    }

    public void updateData(Map<String, String> map) {
        setModel(new IssKeyValueTableModel(map));
    }
}
