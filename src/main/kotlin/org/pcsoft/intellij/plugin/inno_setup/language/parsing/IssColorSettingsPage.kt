package org.pcsoft.intellij.plugin.inno_setup.language.parsing

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import javax.swing.Icon

class IssColorSettingsPage : ColorSettingsPage {

    private val DESCRIPTORS = arrayOf(
        AttributesDescriptor("Comment",                            IssSyntaxHighlighting.COMMENT),
        AttributesDescriptor("String",                             IssSyntaxHighlighting.STRING),
        AttributesDescriptor("Number",                             IssSyntaxHighlighting.NUMBER),
        AttributesDescriptor("Brackets and braces//Brackets [ ]", IssSyntaxHighlighting.BRACKET),
        AttributesDescriptor("Brackets and braces//Braces { }",   IssSyntaxHighlighting.BRACE),
        AttributesDescriptor("Operation sign",                     IssSyntaxHighlighting.OPERATION_SIGN),
        AttributesDescriptor("Keyword",                            IssSyntaxHighlighting.KEYWORD),
        AttributesDescriptor("Section name",                       IssAnnotatorHighlighting.SECTION_NAME),
        AttributesDescriptor("Parameter key",                      IssAnnotatorHighlighting.PARAM_KEY),
        AttributesDescriptor("References//Constant reference",     IssAnnotatorHighlighting.REFERENCE),
        AttributesDescriptor("References//Flag",                   IssAnnotatorHighlighting.FLAG),
        AttributesDescriptor("References//ISPP define reference",  IssAnnotatorHighlighting.ISPP_REFERENCE_NAME),
        AttributesDescriptor("References//Unknown reference",      IssAnnotatorHighlighting.UNKNOWN_REFERENCE),
        AttributesDescriptor("Preprocessor//Directive",            IssAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE),
        AttributesDescriptor("Preprocessor//Define name",          IssAnnotatorHighlighting.DEFINE_NAME),
        AttributesDescriptor("Preprocessor//Define type",          IssAnnotatorHighlighting.DEFINE_TYPE),
        AttributesDescriptor("Deprecated",                         IssAnnotatorHighlighting.DEPRECATED),
    )

    override fun getDisplayName() = "Inno Setup"
    override fun getIcon(): Icon = IssIcons.ScriptFile
    override fun getHighlighter(): SyntaxHighlighter = IssTokenHighlighter()
    override fun getAttributeDescriptors() = DESCRIPTORS
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDemoText() = """
        ; Inno Setup Script - demo
        <pp>#define</pp> <dname>AppVersion</dname> "1.0"
        <pp>#define</pp> <dtype>int</dtype> <dname>BuildNumber</dname> 42
        <pp>#define</pp> <dtype>str</dtype> <dname>Company</dname> "ACME"
        <pp>#define</pp> <dtype>float</dtype> <dname>Ratio</dname> 1.5
        <pp>#define</pp> <dname>Max</dname>(a, b) a > b ? a : b
        <pp>#include</pp> "common.iss"

        [<sectionName>Setup</sectionName>]
        <paramKey>AppName</paramKey>=MyApplication
        <paramKey>AppVersion</paramKey>={<pp>#</pp><ppref>AppVersion</ppref>}
        <paramKey>VersionInfoVersion</paramKey>={<pp>#</pp><ppref>BuildNumber</ppref>}
        <paramKey>DefaultDirName</paramKey>=<ref>{autopf}</ref>\MyApp

        [<sectionName>Files</sectionName>]
        <paramKey>Source</paramKey>: "MyProg.exe"; <paramKey>DestDir</paramKey>: "<ref>{app}</ref>"; <paramKey>Flags</paramKey>: <flag>ignoreversion</flag>

        [<sectionName>Icons</sectionName>]
        <paramKey>Name</paramKey>: "<ref>{group}</ref>\My Program"; <paramKey>Filename</paramKey>: "<ref>{app}</ref>\MyProg.exe"
    """.trimIndent()

    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> = mapOf(
        "sectionName" to IssAnnotatorHighlighting.SECTION_NAME,
        "paramKey"    to IssAnnotatorHighlighting.PARAM_KEY,
        "ref"         to IssAnnotatorHighlighting.REFERENCE,
        "flag"        to IssAnnotatorHighlighting.FLAG,
        "pp"          to IssAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE,
        "dname"       to IssAnnotatorHighlighting.DEFINE_NAME,
        "dtype"       to IssAnnotatorHighlighting.DEFINE_TYPE,
        "ppref"       to IssAnnotatorHighlighting.ISPP_REFERENCE_NAME,
    )
}
