package org.pcsoft.plugins.intellij.inno_setup.script.structure.formatting;

import com.intellij.psi.codeStyle.CodeStyleSettings;
import com.intellij.psi.codeStyle.CustomCodeStyleSettings;

/**
 * Created by Christoph on 14.11.2015.
 */
public class IssCodeStyleSettings extends CustomCodeStyleSettings {
    public static final String KEY_SPACES_SETUP_ASSIGNER = "SPACES_SETUP_ASSIGNER";
    public static final String KEY_SPACES_PROPERTY_ASSIGNER="SPACES_PROPERTY_ASSIGNER";
    public static final String KEY_SPACES_PROPERTY_TERMINATOR="SPACES_PROPERTY_TERMINATOR";

    public boolean SPACES_SETUP_ASSIGNER = true;
    public boolean SPACES_PROPERTY_ASSIGNER = true;
    public boolean SPACES_PROPERTY_TERMINATOR = true;

    public IssCodeStyleSettings(CodeStyleSettings container) {
        super("InnoSetupScript", container);
    }
}
