package org.pcsoft.plugins.intellij.iss.language;

import com.intellij.openapi.fileTypes.LanguageFileType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.IssIcons;

import javax.swing.*;

/**
 * Created by Christoph on 30.09.2016.
 */
public class IssFileType extends LanguageFileType {
    public static final IssFileType INSTANCE = new IssFileType();

    public IssFileType() {
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
        return "Inno Setup Script File";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "iss";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return IssIcons.FILE;
    }
}
