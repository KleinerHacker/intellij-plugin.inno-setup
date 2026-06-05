package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.pcsoft.intellij.plugin.inno_setup.IssIcons
import javax.swing.Icon

class IsiColorSettingsPage : ColorSettingsPage {

    private val DESCRIPTORS = arrayOf(
        AttributesDescriptor("Comment",                            IsiSyntaxHighlighting.COMMENT),
        AttributesDescriptor("String",                             IsiSyntaxHighlighting.STRING),
        AttributesDescriptor("Number",                             IsiSyntaxHighlighting.NUMBER),
        AttributesDescriptor("Brackets and braces//Brackets [ ]", IsiSyntaxHighlighting.BRACKET),
        AttributesDescriptor("Brackets and braces//Braces { }",   IsiSyntaxHighlighting.BRACE),
        AttributesDescriptor("Operation sign",                     IsiSyntaxHighlighting.OPERATION_SIGN),
        AttributesDescriptor("Keyword",                            IsiSyntaxHighlighting.KEYWORD),
        AttributesDescriptor("Section name",                       IsiAnnotatorHighlighting.SECTION_NAME),
        AttributesDescriptor("Parameter key",                      IsiAnnotatorHighlighting.PARAM_KEY),
        AttributesDescriptor("References//Constant reference",     IsiAnnotatorHighlighting.REFERENCE),
        AttributesDescriptor("References//Flag",                   IsiAnnotatorHighlighting.FLAG),
        AttributesDescriptor("References//ISPP define reference",  IsiAnnotatorHighlighting.ISPP_REFERENCE_NAME),
        AttributesDescriptor("References//Unknown reference",      IsiAnnotatorHighlighting.UNKNOWN_REFERENCE),
        AttributesDescriptor("Preprocessor//Directive",            IsiAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE),
        AttributesDescriptor("Preprocessor//Define name",          IsiAnnotatorHighlighting.DEFINE_NAME),
        AttributesDescriptor("Deprecated",                         IsiAnnotatorHighlighting.DEPRECATED),
    )

    override fun getDisplayName() = "Inno Setup"
    override fun getIcon(): Icon = IssIcons.ScriptFile
    override fun getHighlighter(): SyntaxHighlighter = IsiTokenHighlighter()
    override fun getAttributeDescriptors() = DESCRIPTORS
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    override fun getDemoText() = """
        ; Inno Setup Script - demo
        <pp>#define</pp> <dname>AppVersion</dname> "1.0"
        <pp>#define</pp> <dname>BuildNumber</dname> 42
        <pp>#define</pp> <dname>NextBuild</dname> BuildNumber + 1
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
        "sectionName" to IsiAnnotatorHighlighting.SECTION_NAME,
        "paramKey"    to IsiAnnotatorHighlighting.PARAM_KEY,
        "ref"         to IsiAnnotatorHighlighting.REFERENCE,
        "flag"        to IsiAnnotatorHighlighting.FLAG,
        "pp"          to IsiAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE,
        "dname"       to IsiAnnotatorHighlighting.DEFINE_NAME,
        "ppref"       to IsiAnnotatorHighlighting.ISPP_REFERENCE_NAME,
    )
}
