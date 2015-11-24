package org.pcsoft.plugins.intellij.inno_setup.script;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;

import javax.swing.Icon;

/**
 * Created by Christoph on 12.12.2014.
 */
public class IssScriptFileType extends LanguageFileType {

    public static final IssScriptFileType INSTANCE = new IssScriptFileType();

    private IssScriptFileType() {
        super(IssLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Inno Setup Script";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Setup Script for Inno Setup 5.5";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "iss";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return IssIcons.IS_SCRIPT;
    }
}
