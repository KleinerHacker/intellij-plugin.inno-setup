package org.pcsoft.plugins.intellij.inno_setup.ui;

import com.intellij.openapi.util.Condition;
import org.apache.commons.lang.SystemUtils;

/**
 * Created by Christoph on 24.11.2015.
 */
public class IssHelpToolWindowCondition implements Condition {
    @Override
    public boolean value(Object o) {
        return SystemUtils.IS_OS_WINDOWS;
    }
}
