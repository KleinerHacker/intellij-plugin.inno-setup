/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section

import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.fileTypes.SyntaxHighlighter
import com.intellij.openapi.options.colors.AttributesDescriptor
import com.intellij.openapi.options.colors.ColorDescriptor
import com.intellij.openapi.options.colors.ColorSettingsPage
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsIcons
import javax.swing.Icon

/**
 * Exposes syntax-highlighting colors in the IntelliJ color settings UI.
 */
class IsSectionColorSettingsPage : ColorSettingsPage {

    private val DESCRIPTORS = arrayOf(
        AttributesDescriptor("Comment", IsSectionSyntaxHighlighting.COMMENT),
        AttributesDescriptor("String", IsSectionSyntaxHighlighting.STRING),
        AttributesDescriptor("Number", IsSectionSyntaxHighlighting.NUMBER),
        AttributesDescriptor("Brackets and braces//Brackets [ ]", IsSectionSyntaxHighlighting.BRACKET),
        AttributesDescriptor("Brackets and braces//Braces { }", IsSectionSyntaxHighlighting.BRACE),
        AttributesDescriptor("Operation sign", IsSectionSyntaxHighlighting.OPERATION_SIGN),
        AttributesDescriptor("Keyword", IsSectionSyntaxHighlighting.KEYWORD),
        AttributesDescriptor("Section name", IsSectionAnnotatorHighlighting.SECTION_NAME),
        AttributesDescriptor("Parameter key", IsSectionAnnotatorHighlighting.PARAM_KEY),
        AttributesDescriptor("References//Constant reference", IsSectionAnnotatorHighlighting.REFERENCE),
        AttributesDescriptor("References//Flag", IsSectionAnnotatorHighlighting.FLAG),
        AttributesDescriptor("References//ISPP define reference", IsSectionAnnotatorHighlighting.ISPP_REFERENCE_NAME),
        AttributesDescriptor("References//Unknown reference", IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE),
        AttributesDescriptor("Preprocessor//Directive", IsSectionAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE),
        AttributesDescriptor("Preprocessor//Define name", IsSectionAnnotatorHighlighting.DEFINE_NAME),
        AttributesDescriptor("Deprecated", IsSectionAnnotatorHighlighting.DEPRECATED),
        AttributesDescriptor("Unused", IsSectionAnnotatorHighlighting.UNUSED),
    )

    /**
     * Returns user-visible presentation text for this IntelliJ extension.
     */
    override fun getDisplayName() = "Inno Setup"

    /**
     * Returns the icon shown for this element or file type.
     */
    override fun getIcon(): Icon = IsIcons.ScriptFile

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getHighlighter(): SyntaxHighlighter = IsSectionTokenHighlighter()

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getAttributeDescriptors() = DESCRIPTORS

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getColorDescriptors(): Array<ColorDescriptor> = ColorDescriptor.EMPTY_ARRAY

    /**
     * Returns or performs the public behavior represented by this member.
     */
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
        <paramKey>Source</paramKey>: "MyProg.exe"; <paramKey>DestDir</paramKey>: "<ref>{app}</ref>"; <paramKey>Flags</paramKey>: <flag>ignoreversion</flag><unused>;</unused>

        [<sectionName>Icons</sectionName>]
        <paramKey>Name</paramKey>: "<ref>{group}</ref>\My Program"; <paramKey>Filename</paramKey>: "<ref>{app}</ref>\MyProg.exe"
    """.trimIndent()

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getAdditionalHighlightingTagToDescriptorMap(): Map<String, TextAttributesKey> = mapOf(
        "sectionName" to IsSectionAnnotatorHighlighting.SECTION_NAME,
        "paramKey" to IsSectionAnnotatorHighlighting.PARAM_KEY,
        "ref" to IsSectionAnnotatorHighlighting.REFERENCE,
        "flag" to IsSectionAnnotatorHighlighting.FLAG,
        "pp" to IsSectionAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE,
        "dname" to IsSectionAnnotatorHighlighting.DEFINE_NAME,
        "ppref" to IsSectionAnnotatorHighlighting.ISPP_REFERENCE_NAME,
        "unused" to IsSectionAnnotatorHighlighting.UNUSED,
    )
}
