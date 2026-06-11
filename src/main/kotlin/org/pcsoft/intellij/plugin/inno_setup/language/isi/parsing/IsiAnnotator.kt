/*
 * Copyright (c) KleinerHacker alias Pfeiffer C Soft 2026.
 * This work is licensed under the Apache License, Version 2.0.
 * You may not use this file except in compliance with the License.
 * You may obtain a copy of the License at:
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, this software is distributed on an “AS IS” BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and limitations.
 */

package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.services.IssLanguageService
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isl.specTarget
import org.pcsoft.intellij.plugin.inno_setup.language.isi.*
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.*
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.quickfix.AddMissingDirectivesQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.quickfix.AddMissingFlagsQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.quickfix.AddMissingParametersQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.quickfix.AddMissingSectionsQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.quickfix.MoveCodeSectionLastQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.quickfix.RemoveEmptySectionQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.quickfix.RemoveRedundantFlagQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.quickfix.RemoveTrailingSemicolonQuickFix
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.language.issFile
import org.pcsoft.intellij.plugin.inno_setup.services.IssConstantService
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.settings.IssSettingsService
import org.pcsoft.intellij.plugin.inno_setup.types.*

class IsiAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val spec = service<IssSpecService>().spec
        when (element) {
            is IssFile -> annotateFile(element, holder, spec)
            is IsiSectionName -> annotateSectionName(element, holder, spec)
            is IsiSection -> annotateSection(element, holder, spec)
            is IsiParameterEntry -> {
                annotateParameterEntry(element, holder, spec)
                annotateTrailingSemicolon(element, holder)
            }
            is IsiParamKey -> annotateParamKey(element, holder, spec)
            is IsiDirectiveKey -> annotateDirectiveKey(element, holder, spec)
            is IsiParamValue -> annotateParamValue(element, holder, spec)
            is IsiConstant -> annotateConstant(element, holder)
        }
        if (element is IsiParamValue) annotateLanguageId(element, holder)
    }

    /**
     * Warns when a `[LangOptions]` `LanguageID` is neither <code>0</code> nor a recognised Windows
     * locale identifier ([IssLanguageService.validIds]). Malformed (non-integer) values are left to
     * the native integer type check, which reports them as errors.
     */
    private fun annotateLanguageId(value: IsiParamValue, holder: AnnotationHolder) {
        if (value.isInCodeSection) return
        val directive = value.containingDirectiveEntry ?: return
        if (!directive.keyText().equals("LanguageID", ignoreCase = true)) return
        if (directive.containingSection?.nameText?.equals("LangOptions", ignoreCase = true) != true) return

        val text = value.singleText.trim()
        if (text.isEmpty()) return
        val numeric = IssLanguageService.parseId(text) ?: return // malformed → handled as a type error
        if (numeric == 0 || numeric in service<IssLanguageService>().validIds) return

        holder.newAnnotation(
            HighlightSeverity.WARNING,
            "Unknown Windows language identifier '$text' — expected 0 or a valid LCID such as \$0409 (English – United States)"
        ).range(value.textRange).create()
    }

    private fun annotateFile(file: IssFile, holder: AnnotationHolder, spec: IsiSpec) {
        // Required sections are file-type specific: [Setup] in scripts, [LangOptions] in .isl files.
        val target = file.specTarget
        val required = spec.sections.filter { it.required.appliesTo(target) }.map { it.name.lowercase() }.toSet()
        val existing = file.sections.map { it.nameText.lowercase() }.toSet()
        val missing = required - existing
        if (missing.isNotEmpty()) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Required section(s) missing: " + missing.joinToString(", ") { "[$it]" }
            ).fileLevel()
                .withFix(AddMissingSectionsQuickFix(missing.toList(), spec))
                .create()
        }

        val sections = file.sections
        val codeIdx = sections.indexOfFirst { it.nameText.equals("Code", ignoreCase = true) }
        if (codeIdx >= 0 && codeIdx < sections.size - 1) {
            sections.subList(codeIdx, sections.size).forEach { section ->
                val isCode = section.nameText.equals("Code", ignoreCase = true)
                val msg = if (isCode)
                    "[Code] must be the last section in the script"
                else
                    "This section appears after [Code], which must be the last section"
                val builder = holder.newAnnotation(HighlightSeverity.ERROR, msg)
                    .range(section.sectionHeader.sectionName?.textRange ?: section.sectionHeader.textRange)
                if (isCode) builder.withFix(MoveCodeSectionLastQuickFix(file))
                builder.create()
            }
        }
    }

    private fun annotateSectionName(name: IsiSectionName, holder: AnnotationHolder, spec: IsiSpec) {
        if (name.isInCodeSection) return
        val section = name.parent?.parent as? IsiSection ?: return
        val specSection = section.specSection(spec)
        if (specSection == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unknown section: '${name.text}'")
                .range(name.textRange)
                .textAttributes(IsiAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        } else {
            highlight(name.textRange, IsiAnnotatorHighlighting.SECTION_NAME, holder)
        }
    }

    private fun annotateSection(section: IsiSection, holder: AnnotationHolder, spec: IsiSpec) {
        // [Code] is free-form Pascal — no ISI-level checks apply.
        if (section.nameText.equals("Code", ignoreCase = true)) return

        if (section.directiveEntryList.isEmpty() && section.parameterEntryList.isEmpty()) {
            val range = section.sectionHeader.sectionName?.textRange ?: section.sectionHeader.textRange
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Empty section — consider removing it")
                .range(range)
                .textAttributes(IsiAnnotatorHighlighting.UNUSED)
                .withFix(RemoveEmptySectionQuickFix(section))
                .create()
        }

        val specSection = section.specSection(spec) ?: return
        if (specSection.type != "directive") return

        val target = section.specTarget
        val required = specSection.attributes.filter { it.required.appliesTo(target) }.map { it.name.lowercase() }.toSet()
        val present = section.directiveEntryList.map { it.keyText().lowercase() }.toSet()
        val missing = required - present
        if (missing.isNotEmpty()) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Required directive(s) missing: " + missing.joinToString(", ")
            ).range(section.sectionHeader.sectionName?.textRange ?: section.sectionHeader.textRange)
                .withFix(AddMissingDirectivesQuickFix(section, missing.toList(), specSection))
                .create()
        }
    }

    private fun annotateTrailingSemicolon(entry: IsiParameterEntry, holder: AnnotationHolder) {
        if (entry.isInCodeSection) return
        var node = entry.node.lastChildNode
        if (node?.elementType == IsiTypes.CRLF) node = node.treePrev
        if (node?.elementType == IsiTypes.SEMICOLON) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Trailing semicolon is optional")
                .range(node.textRange)
                .textAttributes(IsiAnnotatorHighlighting.UNUSED)
                .withFix(RemoveTrailingSemicolonQuickFix(entry))
                .create()
        }
    }

    private fun annotateParameterEntry(entry: IsiParameterEntry, holder: AnnotationHolder, spec: IsiSpec) {
        if (entry.isInCodeSection) return
        val section = entry.containingSection ?: return
        val specSection = section.specSection(spec) ?: return
        if (specSection.type != "parameter") return

        val target = section.specTarget
        val required = specSection.attributes.filter { it.required.appliesTo(target) }.map { it.name.lowercase() }.toSet()
        val present = entry.paramPairList.map { it.keyText().lowercase() }.toSet()
        val missing = required - present
        if (missing.isNotEmpty()) {
            val end = entry.node.lastChildNode
                ?.takeIf { it.elementType == IsiTypes.CRLF }
                ?.startOffset ?: entry.textRange.endOffset
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Required parameter(s) missing: " + missing.joinToString(", ")
            ).range(TextRange(entry.textRange.startOffset, end))
                .withFix(AddMissingParametersQuickFix(entry, missing.toList(), specSection))
                .create()
        }
    }

    private fun annotateParamKey(key: IsiParamKey, holder: AnnotationHolder, spec: IsiSpec) {
        if (key.isInCodeSection) return
        val pair = key.parent as? IsiParamPair ?: return
        val section = pair.containingSection ?: return
        val specSection = section.specSection(spec) ?: return
        val attr = specSection.attributes.firstOrNull { it.name.equals(pair.keyText(), ignoreCase = true) }
        annotateKey(key.textRange, attr, holder, key.specTarget)
    }

    private fun annotateDirectiveKey(key: IsiDirectiveKey, holder: AnnotationHolder, spec: IsiSpec) {
        if (key.isInCodeSection) return
        val entry = key.parent as? IsiDirectiveEntry ?: return
        val section = entry.containingSection ?: return
        val specSection = section.specSection(spec) ?: return

        // Internationalized sections ([Messages], [CustomMessages]) allow a "lang." prefix and,
        // for [CustomMessages], arbitrary user-defined names. Strip the prefix before matching and
        // never flag an unrecognized name as unknown (the predefined list is advisory only).
        if (specSection.internationalization) {
            val baseName = entry.keyText().substringAfterLast('.')
            val attr = specSection.attributes.firstOrNull { it.name.equals(baseName, ignoreCase = true) }
            if (attr != null) {
                annotateKey(key.textRange, attr, holder, key.specTarget)
            } else {
                highlight(key.textRange, IsiAnnotatorHighlighting.PARAM_KEY, holder)
            }
            annotateLanguagePrefix(key, entry, holder)
            return
        }

        val attr = specSection.attributes.firstOrNull { it.name.equals(entry.keyText(), ignoreCase = true) }
        annotateKey(key.textRange, attr, holder, key.specTarget)
    }

    /**
     * In an internationalized section, a `lang.` key prefix must match a `Name` declared in
     * [Languages]. An unknown prefix is flagged in red over the prefix segment.
     */
    private fun annotateLanguagePrefix(key: IsiDirectiveKey, entry: IsiDirectiveEntry, holder: AnnotationHolder) {
        val full = entry.keyText()
        val dot = full.indexOf('.')
        if (dot <= 0) return
        val prefix = full.substring(0, dot)
        val declared = key.issFile?.findSections("Languages")
            ?.flatMap { it.nameDeclarations }
            ?.map { it.valueUnquoted }
            ?: emptyList()
        if (declared.none { it.equals(prefix, ignoreCase = true) }) {
            val start = key.textRange.startOffset
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Unknown language prefix '$prefix' (no matching Name in [Languages])"
            )
                .range(TextRange(start, start + dot))
                .textAttributes(IsiAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        }
    }

    private fun annotateKey(range: TextRange, attr: IsiAttributeSpec?, holder: AnnotationHolder, target: IsiSpecTarget) {
        when {
            attr == null ->
                holder.newAnnotation(HighlightSeverity.ERROR, "Unknown parameter: '${range}'")
                    .range(range)
                    .textAttributes(IsiAnnotatorHighlighting.UNKNOWN_REFERENCE)
                    .create()

            attr.deprecated.appliesTo(target) ->
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(range)
                    .textAttributes(IsiAnnotatorHighlighting.DEPRECATED)
                    .create()

            else -> {
                annotateVersion(range, attr.name, attr.since, attr.until, holder)
                highlight(range, IsiAnnotatorHighlighting.PARAM_KEY, holder)
            }
        }
    }

    private fun annotateVersion(
        range: TextRange, name: String,
        since: String?, until: String?,
        holder: AnnotationHolder
    ) {
        val minVersion = IssSettingsService.getInstance().state.minInnoVersion ?: return
        until?.let {
            if (IssSettingsService.compareIsVersions(it, minVersion) <= 0) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'$name' was removed in Inno Setup $it"
                ).range(range).create()
                return
            }
        }
        since?.let {
            if (IssSettingsService.compareIsVersions(it, minVersion) > 0) {
                holder.newAnnotation(
                    HighlightSeverity.WARNING,
                    "'$name' requires Inno Setup $it or later (configured minimum: $minVersion)"
                ).range(range).create()
            }
        }
    }

    private fun annotateParamValue(value: IsiParamValue, holder: AnnotationHolder, spec: IsiSpec) {
        if (value.isInCodeSection) return
        val attr = resolveAttr(value, spec) ?: return
        when (val type = attr.type) {
            is IsiFlagTypeSpec -> annotateFlagValue(value, type, holder)
            is IsiNativeTypeSpec -> annotateNativeValue(value, type, holder)
            is IsiReferenceTypeSpec -> {
                val pair = value.containingParamPair
                if (pair?.isReferenceParam() == true) {
                    value.identifiers.forEach {
                        highlight(it.textRange, IsiAnnotatorHighlighting.REFERENCE, holder)
                    }
                }
            }
        }
    }

    private fun resolveAttr(value: IsiParamValue, spec: IsiSpec): IsiAttributeSpec? {
        val pair = value.containingParamPair
        if (pair != null) {
            val ss = pair.containingSection?.specSection(spec) ?: return null
            return ss.attributes.firstOrNull { it.name.equals(pair.keyText(), ignoreCase = true) }
        }
        val dir = value.containingDirectiveEntry ?: return null
        val ss = dir.containingSection?.specSection(spec) ?: return null
        return ss.attributes.firstOrNull { it.name.equals(dir.keyText(), ignoreCase = true) }
    }

    private fun annotateFlagValue(value: IsiParamValue, flagType: IsiFlagTypeSpec, holder: AnnotationHolder) {
        val target = value.specTarget
        val flagMap = flagType.flags.associateBy { it.name.lowercase() }
        val tokenNodes = value.node
            .getChildren(TokenSet.create(IsiTypes.IDENTIFIER))
            .associateBy { it.text.lowercase() }

        tokenNodes.forEach { (name, node) ->
            val def = flagMap[name]
            when {
                def == null ->
                    holder.newAnnotation(HighlightSeverity.ERROR, "Unknown flag: '${node.text}'")
                        .range(node.textRange)
                        .textAttributes(IsiAnnotatorHighlighting.UNKNOWN_REFERENCE)
                        .create()

                def.deprecated.appliesTo(target) ->
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(node.textRange)
                        .textAttributes(IsiAnnotatorHighlighting.DEPRECATED)
                        .create()

                else -> {
                    annotateVersion(node.textRange, def.name, def.since, def.until, holder)
                    highlight(node.textRange, IsiAnnotatorHighlighting.FLAG, holder)
                }
            }
        }

        val seen = mutableSetOf<Pair<String, String>>()
        tokenNodes.forEach { (name, node) ->
            val def = flagMap[name] ?: return@forEach
            def.conflicts.forEach conflict@{ conflict ->
                // 'requires' is a dependency, not a conflict — handled in its own pass below.
                if (conflict.type == IsiFlagType.REQUIRES) return@conflict

                val otherName = conflict.flag.lowercase()
                val other = tokenNodes[otherName] ?: return@conflict

                if (conflict.type == IsiFlagType.REDUNDANT) {
                    // Asymmetric: 'name' implicitly sets 'otherName' (or nullifies its effect),
                    // so the other flag is redundant. Only that flag is marked (grey, like unused).
                    if (!seen.add(name to otherName)) return@conflict
                    holder.newAnnotation(
                        HighlightSeverity.WEAK_WARNING,
                        "Flag '${other.text}' is redundant — already implied by '${node.text}'"
                    )
                        .range(other.textRange)
                        .textAttributes(IsiAnnotatorHighlighting.UNUSED)
                        .withFix(RemoveRedundantFlagQuickFix(value, other.text))
                        .create()
                    return@conflict
                }

                val key = if (name < otherName) name to otherName else otherName to name
                if (!seen.add(key)) return@conflict

                val severity = if (conflict.type == IsiFlagType.FORBIDDEN)
                    HighlightSeverity.ERROR else HighlightSeverity.WARNING
                val msg = "Conflicting flags: '${node.text}' and '${other.text}'"
                holder.newAnnotation(severity, msg).range(node.textRange).create()
                holder.newAnnotation(severity, msg).range(other.textRange).create()
            }
        }

        // Required flags: a flag may mandate the presence of other flags in the same value.
        tokenNodes.forEach { (name, node) ->
            val def = flagMap[name] ?: return@forEach
            val missing = def.conflicts
                .filter { it.type == IsiFlagType.REQUIRES }
                .map { it.flag }
                .filter { tokenNodes[it.lowercase()] == null }
            if (missing.isEmpty()) return@forEach

            val msg = "Flag '${node.text}' requires ${missing.joinToString(", ") { "'$it'" }}"
            holder.newAnnotation(HighlightSeverity.ERROR, msg)
                .range(node.textRange)
                .withFix(AddMissingFlagsQuickFix(value, missing))
                .create()
        }
    }

    private fun annotateNativeValue(value: IsiParamValue, type: IsiNativeTypeSpec, holder: AnnotationHolder) {
        val text = value.singleText
        when (type.dataType.lowercase()) {
            "boolean" -> {
                if (text.lowercase() in setOf("yes", "no")) {
                    value.node.getChildren(TokenSet.create(IsiTypes.IDENTIFIER)).forEach {
                        highlight(it.textRange, IsiSyntaxHighlighting.KEYWORD, holder)
                    }
                } else {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Expected type '${type.dataType}', got: '$text'"
                    ).range(value.textRange).create()
                }
            }

            // Accept decimal (-?[0-9]+) or Pascal-style hexadecimal ($ followed by hex digits, e.g. $0409)
            "integer" -> if (!text.matches(Regex("-?[0-9]+")) && !text.matches(Regex("\\\$[0-9A-Fa-f]+"))) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Expected type '${type.dataType}', got: '$text'"
                ).range(value.textRange).create()
            }
        }
    }

    private fun annotateConstant(constant: IsiConstant, holder: AnnotationHolder) {
        if (constant.isInCodeSection) return
        val body = constant.constantBody
        val name = body.text.substringBefore(':').substringBefore('|').trim().trimStart('#')

        val builtins = service<IssConstantService>().spec.constants
        val isppNames = constant.issFile?.definedConstants?.map { it.first } ?: emptyList()
        val isIspp = body.text.trimStart().startsWith("#")

        val known = when {
            isIspp -> name in isppNames
            else -> builtins.any { it.name.equals(name, ignoreCase = true) }
        }

        if (!known) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unknown constant: '{${body.text}}'")
                .range(constant.textRange)
                .textAttributes(IsiAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        } else if (isIspp) {
            body.node.findChildByType(IsiTypes.HASH)?.let {
                highlight(it.textRange, IsiAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)
            }
            body.node.findChildByType(IsiTypes.IDENTIFIER)?.let {
                highlight(it.textRange, IsiAnnotatorHighlighting.ISPP_REFERENCE_NAME, holder)
            }
        } else {
            val constSpec = builtins.firstOrNull { it.name.equals(name, ignoreCase = true) }
            if (constSpec != null) {
                annotateVersion(constant.textRange, "{${constSpec.name}}", constSpec.since, constSpec.until, holder)
            }
            highlight(constant.textRange, IsiAnnotatorHighlighting.REFERENCE, holder)
            if (name.equals("cm", ignoreCase = true)) {
                // Render the `cm` keyword italic (layers over the reference colour above).
                body.node.findChildByType(IsiTypes.IDENTIFIER)?.let {
                    highlight(it.textRange, IsiAnnotatorHighlighting.CUSTOM_MESSAGE_PREFIX, holder)
                }
                annotateCustomMessage(constant, body, holder)
            }
        }
    }

    /**
     * Flags a `{cm:Name}` whose message name is not defined in any [CustomMessages] section. The
     * red highlight covers only the name token, mirroring the unknown-flag/unknown-constant style.
     */
    private fun annotateCustomMessage(constant: IsiConstant, body: IsiConstantBody, holder: AnnotationHolder) {
        val colon = body.node.findChildByType(IsiTypes.COLON) ?: return
        val nameNode = body.node.getChildren(TokenSet.create(IsiTypes.IDENTIFIER))
            .firstOrNull { it.startOffset > colon.startOffset } ?: return
        val msgName = nameNode.text
        if (msgName.isEmpty()) return

        val declared = constant.issFile?.findSections("CustomMessages")
            ?.flatMap { it.directiveEntryList }
            ?.mapNotNull { (it as? IsiDirectiveEntryEx)?.customMessageName() }
            ?: emptyList()
        if (declared.none { it.equals(msgName, ignoreCase = true) }) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Unknown custom message: '$msgName' (not defined in [CustomMessages])"
            )
                .range(nameNode.textRange)
                .textAttributes(IsiAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        }
    }

    private fun highlight(range: TextRange, key: TextAttributesKey, holder: AnnotationHolder) =
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range).textAttributes(key).create()
}
