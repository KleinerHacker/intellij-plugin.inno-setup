package org.pcsoft.plugins.intellij.inno_setup.script;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;

import javax.swing.*;

/**
 * Created by Christoph on 12.12.2014.
 */
public class IssLanguageFileType extends LanguageFileType {

    public static final IssLanguageFileType INSTANCE = new IssLanguageFileType();

    private IssLanguageFileType() {
        super(IssLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Inno Setup Language File";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Language File for Inno Setup 5.5";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "isl";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return IssIcons.IS_SCRIPT;
    }
}
