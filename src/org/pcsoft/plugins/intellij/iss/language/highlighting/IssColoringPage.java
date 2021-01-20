package org.pcsoft.plugins.intellij.iss.language.highlighting;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.iss.IssIcons;

import javax.swing.*;
import java.util.HashMap;
import java.util.Map;

public class IssColoringPage implements ColorSettingsPage {
    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("String", IssHighlighting.STRING),
            new AttributesDescriptor("Operator", IssHighlighting.OPERATOR),
            new AttributesDescriptor("Splitter", IssHighlighting.SPLITTER),
            new AttributesDescriptor("Comment", IssHighlighting.COMMENT),
            new AttributesDescriptor("Keyword", IssHighlighting.KEYWORD),
            new AttributesDescriptor("Braces", IssHighlighting.BRACES),
            new AttributesDescriptor("Label", IssHighlighting.LABEL),
            new AttributesDescriptor("Constant", IssHighlighting.CONST),
    };

    @Nullable
    @Override
    public Icon getIcon() {
        return IssIcons.FILE;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return new IssSyntaxHighlighter();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "; -- Example1.iss --\n" +
                "; Demonstrates copying 3 files and creating an icon.\n" +
                "\n" +
                "; SEE THE DOCUMENTATION FOR DETAILS ON CREATING .ISS SCRIPT FILES!\n" +
                "\n" +
                "[<LABEL>Setup</LABEL>]\n" +
                "<KEY>AppName</KEY>=My Program\n" +
                "<KEY>AppVersion</KEY>=1.5\n" +
                "<KEY>DefaultDirName</KEY>=<CONST>{pf}</CONST>\\My Program\n" +
                "<KEY>DefaultGroupName</KEY>=My Program\n" +
                "<KEY>UninstallDisplayIcon</KEY>=<CONST>{app}</CONST>\\MyProg.exe\n" +
                "<KEY>Compression</KEY>=lzma2\n" +
                "<KEY>SolidCompression</KEY>=yes\n" +
                "<KEY>OutputDir</KEY>=userdocs:Inno Setup Examples Output\n" +
                "\n" +
                "[<LABEL>Files</LABEL>]\n" +
                "<KEY>Source</KEY>: \"<STRING>MyProg.exe</STRING>\"; <KEY>DestDir</KEY>: \"<CONST>{app}</CONST>\"\n" +
                "<KEY>Source</KEY>: \"<STRING>MyProg.chm</STRING>\"; <KEY>DestDir</KEY>: \"<CONST>{app}</CONST>\"\n" +
                "<KEY>Source</KEY>: \"<STRING>Readme.txt</STRING>\"; <KEY>DestDir</KEY>: \"<CONST>{app}</CONST>\"; <KEY>Flags</KEY>: isreadme\n" +
                "\n" +
                "[<LABEL>Icons</LABEL>]\n" +
                "<KEY>Name</KEY>: \"<CONST>{group}</CONST><STRING>\\My Program</STRING>\"; <KEY>Filename</KEY>: \"<CONST>{app}</CONST><STRING>\\MyProg.exe</STRING>\"";
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        final Map<String, TextAttributesKey> map = new HashMap<>();
        map.put("KEY", IssHighlighting.KEYWORD);
        map.put("LABEL", IssHighlighting.LABEL);
        map.put("CONST", IssHighlighting.CONST);
        map.put("STRING", IssHighlighting.STRING);

        return map;
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return ColorDescriptor.EMPTY_ARRAY;
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Inno Setup";
    }
}
