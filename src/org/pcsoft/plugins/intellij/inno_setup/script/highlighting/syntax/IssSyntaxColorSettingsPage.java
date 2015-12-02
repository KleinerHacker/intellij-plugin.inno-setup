package org.pcsoft.plugins.intellij.inno_setup.script.highlighting.syntax;

import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.fileTypes.SyntaxHighlighter;
import com.intellij.openapi.options.colors.AttributesDescriptor;
import com.intellij.openapi.options.colors.ColorDescriptor;
import com.intellij.openapi.options.colors.ColorSettingsPage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.pcsoft.plugins.intellij.inno_setup.IssIcons;
import org.pcsoft.plugins.intellij.inno_setup.script.highlighting.IssLanguageHighlightingColorFactory;

import javax.swing.*;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Christoph
 * Date: 14.07.13
 * Time: 11:03
 * TODO: Write Summary
 */
public final class IssSyntaxColorSettingsPage implements ColorSettingsPage {

    private static final AttributesDescriptor[] DESCRIPTORS = new AttributesDescriptor[]{
            new AttributesDescriptor("Comments", IssLanguageHighlightingColorFactory.SYNTAX_COMMENT),
            new AttributesDescriptor("Strings", IssLanguageHighlightingColorFactory.ANNOTATOR_INFO_STRING),
            new AttributesDescriptor("Compiler Directives", IssLanguageHighlightingColorFactory.ANNOTATION_INFO_COMPILER_DIRECTIVE),
            new AttributesDescriptor("Section Title", IssLanguageHighlightingColorFactory.ANNOTATION_INFO_SECTION_TITLE),
            new AttributesDescriptor("Operators", IssLanguageHighlightingColorFactory.SYNTAX_OPERATORS)
    };

    static {
        Arrays.sort(DESCRIPTORS, new Comparator<AttributesDescriptor>() {
            @Override
            public int compare(AttributesDescriptor o1, AttributesDescriptor o2) {
                return o1.getDisplayName().compareTo(o2.getDisplayName());
            }
        });
    }

    @Nullable
    @Override
    public Map<String, TextAttributesKey> getAdditionalHighlightingTagToDescriptorMap() {
        Map<String, TextAttributesKey> map = new HashMap<String, TextAttributesKey>();
        return map;
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return IssIcons.IS_SCRIPT;
    }

    @NotNull
    @Override
    public SyntaxHighlighter getHighlighter() {
        return IssSyntaxHighlighter.getInstance();
    }

    @NotNull
    @Override
    public String getDemoText() {
        return "; Example File\n\n" +
                "#define example \"example\"\n\n" +
                "[Setup]\n" +
                "AppName=Inno Setup QuickStart Pack\n" +
                "AppId=Inno Setup 5\n" +
                "AppVersion=5.5.5\n\n" +
                "[Tasks]\n" +
                "Name: desktopicon; Description: \"{cm:CreateDesktopIcon}\"\n\n" +
                "[Files]\n" +
                ";first the files used by [Code] so these can be quickly decompressed despite solid compression\n" +
                "Source: \"otherfiles\\IDE.ico\"; Flags: dontcopy\n" +
                "Source: \"otherfiles\\ISPP.ico\"; Flags: dontcopy\n" +
                "Source: \"otherfiles\\ISCrypt.ico\"; Flags: dontcopy\n" +
                "Source: \"isxdlfiles\\isxdl.dll\"; Flags: dontcopy\n" +
                "Source: \"{#isfiles}\\WizModernSmallImage-IS.bmp\"; Flags: dontcopy\n\n" +
                "[Icons]\n" +
                "Name: \"{group}\\Inno Setup Compiler\"; Filename: \"{app}\\Compil32.exe\"; WorkingDir: \"{app}\"; AppUserModelID: \"JR.InnoSetup.IDE.5\"\n" +
                "Name: \"{group}\\Inno Setup Documentation\"; Filename: \"{app}\\ISetup.chm\";\n" +
                "Name: \"{group}\\Inno Setup Example Scripts\"; Filename: \"{app}\\Examples\\\";";
    }

    @NotNull
    @Override
    public AttributesDescriptor[] getAttributeDescriptors() {
        return DESCRIPTORS;
    }

    @NotNull
    @Override
    public ColorDescriptor[] getColorDescriptors() {
        return new ColorDescriptor[]{

        };
    }

    @NotNull
    @Override
    public String getDisplayName() {
        return "Inno Setup Script (ISS)";
    }
}
