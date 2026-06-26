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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section

import com.intellij.codeInsight.intention.IntentionAction
import com.intellij.lang.ASTNode
import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiManager
import com.intellij.psi.tree.TokenSet
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.build.IsScriptCollector
import org.pcsoft.intellij.plugin.inno_setup.language.feature.IsMessagesFileResolver
import org.pcsoft.intellij.plugin.inno_setup.language.feature.IsResolveResult
import org.pcsoft.intellij.plugin.inno_setup.language.feature.include.IsAnnotationSink
import org.pcsoft.intellij.plugin.inno_setup.language.feature.include.PlatformAnnotationSink
import org.pcsoft.intellij.plugin.inno_setup.language.feature.include.declarationScope
import org.pcsoft.intellij.plugin.inno_setup.language.feature.include.toEffectiveScript
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.specTarget
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.issFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi.*
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.quickfix.*
import org.pcsoft.intellij.plugin.inno_setup.services.IsConstantService
import org.pcsoft.intellij.plugin.inno_setup.services.IsLanguageDataService
import org.pcsoft.intellij.plugin.inno_setup.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.services.IsSpecService
import org.pcsoft.intellij.plugin.inno_setup.settings.IsSettingsService
import org.pcsoft.intellij.plugin.inno_setup.types.*
import java.io.File

/**
 * Annotates Inno Setup PSI elements with validation and highlighting information.
 */
class IsSectionAnnotator : Annotator {

    /**
     * Annotates the supplied PSI element when it matches this component's checks.
     */
    override fun annotate(element: PsiElement, annotationHolder: AnnotationHolder) =
        annotate(element, PlatformAnnotationSink(annotationHolder))

    fun annotate(element: PsiElement, holder: IsAnnotationSink) {
        val spec = service<IsSpecService>().spec
        when (element) {
            is IsScriptFile -> annotateFile(element, holder, spec)
            is IsSectionTitle -> annotateSectionName(element, holder, spec)
            is IsSectionBlock -> annotateSection(element, holder, spec)
            is IsSectionParameterEntry -> {
                annotateParameterEntry(element, holder, spec)
                annotateTrailingSemicolon(element, holder)
            }

            is IsSectionParamKey -> annotateParamKey(element, holder, spec)
            is IsSectionDirectiveKey -> annotateDirectiveKey(element, holder, spec)
            is IsSectionParamPair -> annotateParamPairSeparator(element, holder, spec)
            is IsSectionDirectiveEntry -> annotateDirectiveEntrySeparator(element, holder, spec)
            is IsSectionParamValue -> annotateParamValue(element, holder, spec)
            is IsSectionConstant -> annotateConstant(element, holder)
        }
        if (element is IsSectionParamValue) {
            annotateLanguageId(element, holder)
            annotateMessagesFile(element, holder)
        }
    }

    /**
     * Warns when a \[LangOptions] `LanguageID` is neither <code>0</code> nor a recognised Windows
     * locale identifier ([IsLanguageDataService.validIds]). Malformed (non-integer) values are left to
     * the native integer type check, which reports them as errors.
     */
    private fun annotateLanguageId(value: IsSectionParamValue, holder: IsAnnotationSink) {
        if (value.isInCodeSection) return
        val directive = value.containingDirectiveEntry ?: return
        if (!directive.keyText().equals("LanguageID", ignoreCase = true)) return
        if (directive.containingSection?.nameText?.equals("LangOptions", ignoreCase = true) != true) return

        val text = value.singleText.trim()
        if (text.isEmpty()) return
        val numeric = IsLanguageDataService.parseId(text) ?: return // malformed → handled as a type error
        if (numeric == 0 || numeric in service<IsLanguageDataService>().validIds) return

        holder.newAnnotation(
            HighlightSeverity.WARNING,
            "Unknown Windows language identifier '$text' — expected 0 or a valid LCID such as \$0409 (English – United States)"
        ).range(value.textRange).create()
    }

    /**
     * Validates that a `MessagesFile` value in `\[Languages]` points to an existing, readable, and
     * structurally valid ISL file. Emits ERROR for missing/unreadable/incomplete files and WARNING
     * when the Inno Setup installation path is not configured (so `compiler:` paths cannot be checked).
     */
    private fun annotateMessagesFile(value: IsSectionParamValue, holder: IsAnnotationSink) {
        if (value.isInCodeSection) return
        val pair = value.containingParamPair ?: return
        if (!pair.keyText().equals("MessagesFile", ignoreCase = true)) return
        if (pair.containingSection?.nameText?.equals("Languages", ignoreCase = true) != true) return

        val raw = value.singleText.trim()
        if (raw.isEmpty()) return

        val scriptVf = value.containingFile?.virtualFile ?: return
        val scriptDir = scriptVf.parent?.path?.let { File(it) }
        // Resolve #defines and custom messages over the effective (#include-resolved) script, so values
        // contributed by included files are taken into account.
        val scope = value.issFile?.declarationScope()
        val defines = scope?.definedConstants ?: emptyList()
        val customMessages = scope?.findSections("CustomMessages")
            ?.flatMap { it.directiveEntryList }
            ?.associate { it.keyText().substringAfterLast('.') to it.valueText }
            ?: emptyMap()

        val installPath = IsSettingsService.getInstance().state.installationPath
        val expanded = IsMessagesFileResolver.expandValue(raw, defines, scriptDir, customMessages, installPath)
            ?: return  // unresolvable — no annotation

        when (val result = IsMessagesFileResolver.resolveMessagesFile(expanded, scriptDir, installPath)) {
            is IsResolveResult.Missing ->
                holder.newAnnotation(HighlightSeverity.ERROR, "ISL file not found: '${result.resolvedPath}'")
                    .range(value.textRange)
                    .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                    .create()

            is IsResolveResult.Unreadable ->
                holder.newAnnotation(HighlightSeverity.ERROR, "ISL file is not readable: '${result.resolvedPath}'")
                    .range(value.textRange)
                    .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                    .create()

            IsResolveResult.NotConfigured ->
                holder.newAnnotation(
                    HighlightSeverity.WARNING,
                    "Inno Setup installation path not configured — cannot verify existence of '$raw'"
                ).range(value.textRange).create()

            IsResolveResult.Unresolvable -> Unit  // no annotation

            is IsResolveResult.Ok -> annotateIslContent(result.file, value, holder)
        }
    }

    private fun annotateIslContent(file: File, value: IsSectionParamValue, holder: IsAnnotationSink) {
        val vf = LocalFileSystem.getInstance().findFileByIoFile(file) ?: return
        val psiFile = PsiManager.getInstance(value.project).findFile(vf) as? IsScriptFile ?: run {
            holder.newAnnotation(HighlightSeverity.ERROR, "ISL file cannot be parsed: '${file.absolutePath}'")
                .range(value.textRange).create()
            return
        }
        val langOptions = psiFile.findSection("LangOptions") ?: run {
            holder.newAnnotation(HighlightSeverity.ERROR, "Referenced ISL file is missing [LangOptions] section")
                .range(value.textRange).create()
            return
        }
        val present = langOptions.directiveEntryList.map { it.keyText().lowercase() }.toSet()
        // Required [LangOptions] directives for .isl files come from the spec — the single source of truth.
        val required = service<IsSpecService>().spec.sections
            .firstOrNull { it.name.equals("LangOptions", ignoreCase = true) }
            ?.attributes
            ?.filter { it.required.appliesTo(IsSectionSpecTarget.ISL) }
            ?.map { it.name.lowercase() }
            ?.toSet()
            ?: emptySet()
        val missing = required - present
        if (missing.isNotEmpty()) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Referenced ISL file is incomplete: required directive(s) missing: " +
                        missing.joinToString(", ")
            ).range(value.textRange).create()
        }
    }

    private fun annotateFile(file: IsScriptFile, holder: IsAnnotationSink, spec: IsSectionSpec) {
        // Required sections are file-type specific: \[Setup] in scripts, \[LangOptions] in .isl files.
        val target = file.specTarget

        // A fragment pulled in via #include by another script is intentionally incomplete on its own — its
        // mandatory sections/directives are validated as part of the including script's effective view. So
        // file-level mandatory checks are skipped here (per-line parameter checks still run).
        val isFragment = target == IsSectionSpecTarget.ISS &&
                file.virtualFile?.let { IsScriptCollector(file.project).isIncludedByOther(it) } == true

        if (!isFragment) {
            // Mandatory checks run on the effective, fully #include-resolved script (required sections and
            // directives may legitimately be spread across the main file and its includes). For .isl files
            // there are no includes, so the file itself is used.
            val effective = if (target == IsSectionSpecTarget.ISS) file.toEffectiveScript() else file

            val required = spec.sections.filter { it.required.appliesTo(target) }.map { it.name.lowercase() }.toSet()
            val existing = effective.sections.map { it.nameText.lowercase() }.toSet()
            val missing = required - existing
            if (missing.isNotEmpty()) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Required section(s) missing: " + missing.joinToString(", ") { "[$it]" }
                ).fileLevel()
                    .withFix(AddMissingSectionsQuickFix(missing.toList(), spec))
                    .create()
            }

            annotateRequiredDirectives(file, effective, holder, spec, target)
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
                    .range(section.header.title?.textRange ?: section.header.textRange)
                if (isCode) builder.withFix(MoveCodeSectionLastQuickFix(file))
                builder.create()
            }
        }

        annotateUsePreviousLanguage(file, holder)
    }

    /**
     * Validates required directives over the **effective** script: present directives are the union across
     * all same-named section blocks of [effective] (so a required directive may sit in the main file or in
     * any include). Missing ones are reported at file level on the edited [file]; the quick fix targets the
     * first matching block actually present in the edited file (if any).
     */
    private fun annotateRequiredDirectives(
        file: IsScriptFile,
        effective: IsScriptFile,
        holder: IsAnnotationSink,
        spec: IsSectionSpec,
        target: IsSectionSpecTarget,
    ) {
        spec.sections.filter { it.type == IsSectionType.DIRECTIVE }.forEach { specSection ->
            val required = specSection.attributes
                .filter { it.required.appliesTo(target) }
                .map { it.name.lowercase() }
                .toSet()
            if (required.isEmpty()) return@forEach

            val blocks = effective.findSections(specSection.name)
            if (blocks.isEmpty()) return@forEach   // section absent → covered by the required-section check

            val present = blocks.flatMap { it.directiveEntryList }.map { it.keyText().lowercase() }.toSet()
            val missing = required - present
            if (missing.isEmpty()) return@forEach

            val message = "Required directive(s) missing in [${specSection.name}]: " + missing.joinToString(", ")
            // Anchor on the section header in the edited file when present (so the marker sits on the actual
            // section, not over unrelated lines); fall back to file level when the section lives only in an
            // include.
            val editedBlock = file.findSection(specSection.name)
            val builder = if (editedBlock != null) {
                holder.newAnnotation(HighlightSeverity.ERROR, message)
                    .range(editedBlock.header.title?.textRange ?: editedBlock.header.textRange)
                    .withFix(AddMissingDirectivesQuickFix(editedBlock, missing.toList(), specSection))
            } else {
                holder.newAnnotation(HighlightSeverity.ERROR, message).fileLevel()
            }
            builder.create()
        }
    }

    /**
     * When the effective AppId contains a constant (e.g. an \{#define} or \{code:…} expression), Inno
     * Setup cannot reuse the previously selected language and requires `UsePreviousLanguage` to be set
     * explicitly to `no`; otherwise the compiler rejects the script. `AppId` defaults to `AppName` when
     * omitted, so the `AppName` value is checked in that case. Reported at file level.
     */
    private fun annotateUsePreviousLanguage(file: IsScriptFile, holder: IsAnnotationSink) {
        if (file.specTarget != IsSectionSpecTarget.ISS) return
        val setup = file.findSection("Setup") ?: return

        // AppId defaults to AppName when not specified, so fall back to the AppName value.
        val appIdValue = (setup.directiveEntryList.firstOrNull { it.keyText().equals("AppId", ignoreCase = true) }
            ?: setup.directiveEntryList.firstOrNull { it.keyText().equals("AppName", ignoreCase = true) })
            ?.paramValue ?: return
        if (PsiTreeUtil.findChildrenOfType(appIdValue, IsSectionConstant::class.java).isEmpty()) return

        val entry = setup.directiveEntryList
            .firstOrNull { it.keyText().equals("UsePreviousLanguage", ignoreCase = true) }
        if (entry?.valueText?.trim().equals("no", ignoreCase = true)) return

        holder.newAnnotation(
            HighlightSeverity.ERROR,
            "UsePreviousLanguage must be set to \"no\" when AppId includes constants"
        ).fileLevel().withFix(SetUsePreviousLanguageNoQuickFix(file)).create()
    }

    private fun annotateSectionName(name: IsSectionTitle, holder: IsAnnotationSink, spec: IsSectionSpec) {
        if (name.isInCodeSection) return
        val section = name.parent?.parent as? IsSectionBlock ?: return
        val specSection = section.specSection(spec)
        if (specSection == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unknown section: '${name.text}'")
                .range(name.textRange)
                .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        } else {
            highlight(name.textRange, IsSectionAnnotatorHighlighting.SECTION_NAME, holder)
        }
    }

    private fun annotateSection(section: IsSectionBlock, holder: IsAnnotationSink, spec: IsSectionSpec) {
        // \[Code] is free-form Pascal — no ISI-level checks apply.
        if (section.nameText.equals("Code", ignoreCase = true)) return

        if (section.directiveEntryList.isEmpty() && section.parameterEntryList.isEmpty()) {
            val range = section.header.title?.textRange ?: section.header.textRange
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Empty section — consider removing it")
                .range(range)
                .textAttributes(IsSectionAnnotatorHighlighting.UNUSED)
                .withFix(RemoveEmptySectionQuickFix(section))
                .create()
        }
        // Required-directive validation is performed file-level over the effective (#include-resolved) script
        // in annotateRequiredDirectives, since required directives may be spread across the main file and its
        // includes.
    }

    private fun annotateTrailingSemicolon(entry: IsSectionParameterEntry, holder: IsAnnotationSink) {
        if (entry.isInCodeSection) return
        var node = entry.node.lastChildNode
        if (node?.elementType == IsSectionTypes.CRLF) node = node.treePrev
        if (node?.elementType == IsSectionTypes.SEMICOLON) {
            holder.newAnnotation(HighlightSeverity.WEAK_WARNING, "Trailing semicolon is optional")
                .range(node.textRange)
                .textAttributes(IsSectionAnnotatorHighlighting.UNUSED)
                .withFix(RemoveTrailingSemicolonQuickFix(entry))
                .create()
        }
    }

    private fun annotateParameterEntry(entry: IsSectionParameterEntry, holder: IsAnnotationSink, spec: IsSectionSpec) {
        if (entry.isInCodeSection) return
        val section = entry.containingSection ?: return
        val specSection = section.specSection(spec) ?: return
        if (specSection.type != IsSectionType.PARAMETER) return

        val target = section.specTarget
        val required =
            specSection.attributes.filter { it.required.appliesTo(target) }.map { it.name.lowercase() }.toSet()
        val present = entry.paramPairList.map { it.keyText().lowercase() }.toSet()
        val missing = required - present
        if (missing.isNotEmpty()) {
            val end = entry.node.lastChildNode
                ?.takeIf { it.elementType == IsSectionTypes.CRLF }
                ?.startOffset ?: entry.textRange.endOffset
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Required parameter(s) missing: " + missing.joinToString(", ")
            ).range(TextRange(entry.textRange.startOffset, end))
                .withFix(AddMissingParametersQuickFix(entry, missing.toList(), specSection))
                .create()
        }
    }

    private fun annotateParamKey(key: IsSectionParamKey, holder: IsAnnotationSink, spec: IsSectionSpec) {
        if (key.isInCodeSection) return
        val pair = key.parent as? IsSectionParamPair ?: return
        val section = pair.containingSection ?: return
        val specSection = section.specSection(spec) ?: return
        // A ':'-separated pair in a directive section ([Setup], [Messages], [CustomMessages],
        // [LangOptions]) is a wrong-separator mistake. The red mark is placed on the ':' token by
        // annotateParamPairSeparator (visiting the pair, which contains the token); skip key checks.
        if (specSection.type == IsSectionType.DIRECTIVE) return
        // Internationalized sections ([Messages], [CustomMessages]) allow arbitrary user-defined key
        // names — never flag an unrecognized name as unknown, even when colon syntax is used by mistake.
        if (specSection.internationalization) {
            highlight(key.textRange, IsSectionAnnotatorHighlighting.PARAM_KEY, holder)
            return
        }
        val attr = specSection.attributes.firstOrNull { it.name.equals(pair.keyText(), ignoreCase = true) }
        annotateKey(key.textRange, attr, holder, key.specTarget, pair.keyText())
    }

    private fun annotateDirectiveKey(key: IsSectionDirectiveKey, holder: IsAnnotationSink, spec: IsSectionSpec) {
        if (key.isInCodeSection) return
        val entry = key.parent as? IsSectionDirectiveEntry ?: return
        val section = entry.containingSection ?: return
        val specSection = section.specSection(spec) ?: return

        // An '='-separated entry in a parameter section ([Files], [Icons], [Registry], …) is a
        // wrong-separator mistake. The red mark is placed on the '=' token by
        // annotateDirectiveEntrySeparator (visiting the entry, which contains the token); skip key checks.
        if (specSection.type == IsSectionType.PARAMETER) return

        // Internationalized sections (\[Messages], \[CustomMessages]) allow a "lang." prefix and,
        // for \[CustomMessages], arbitrary user-defined names. Strip the prefix before matching and
        // never flag an unrecognized name as unknown (the predefined list is advisory only).
        if (specSection.internationalization) {
            val baseName = entry.keyText().substringAfterLast('.')
            val attr = specSection.attributes.firstOrNull { it.name.equals(baseName, ignoreCase = true) }
            if (attr != null) {
                annotateKey(key.textRange, attr, holder, key.specTarget, baseName)
            } else {
                highlight(key.textRange, IsSectionAnnotatorHighlighting.PARAM_KEY, holder)
            }
            annotateLanguagePrefix(key, entry, holder)
            return
        }

        val attr = specSection.attributes.firstOrNull { it.name.equals(entry.keyText(), ignoreCase = true) }
        annotateKey(key.textRange, attr, holder, key.specTarget, entry.keyText())
    }

    /**
     * In an internationalized section, a `lang.` key prefix must match a `Name` declared in
     * \[Languages]. An unknown prefix is flagged in red over the prefix segment.
     */
    private fun annotateLanguagePrefix(
        key: IsSectionDirectiveKey,
        entry: IsSectionDirectiveEntry,
        holder: IsAnnotationSink
    ) {
        val full = entry.keyText()
        val dot = full.indexOf('.')
        if (dot <= 0) return
        val prefix = full.substring(0, dot)
        // [Languages] entries may be contributed by an #include — resolve over the effective script.
        val declared = key.issFile?.declarationScope()?.findSections("Languages")
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
                .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        }
    }

    /**
     * Flags a `Key: Value` pair sitting in a directive section (`\[Setup]`, `\[Messages]`, `\[CustomMessages]`,
     * `\[LangOptions]`), where entries must instead use `Key=Value`. The red mark covers the wrong ':'.
     */
    private fun annotateParamPairSeparator(pair: IsSectionParamPair, holder: IsAnnotationSink, spec: IsSectionSpec) {
        if (pair.isInCodeSection) return
        val section = pair.containingSection ?: return
        val specSection = section.specSection(spec) ?: return
        if (specSection.type != IsSectionType.DIRECTIVE) return
        val colon = pair.node.findChildByType(IsSectionTypes.COLON) ?: return
        reportSeparatorMismatch(
            colon.textRange, section.nameText, "=", ":", "${pair.keyText()}=Value",
            ReplaceSeparatorQuickFix(pair, IsSectionTypes.COLON, ":", "="), holder
        )
    }

    /**
     * Flags a `Key=Value` entry sitting in a parameter section (`\[Files]`, `\[Icons]`, `\[Registry]`, …),
     * where entries must instead use `Key: Value`. The red mark covers the wrong '='.
     */
    private fun annotateDirectiveEntrySeparator(
        entry: IsSectionDirectiveEntry,
        holder: IsAnnotationSink,
        spec: IsSectionSpec
    ) {
        if (entry.isInCodeSection) return
        val section = entry.containingSection ?: return
        val specSection = section.specSection(spec) ?: return
        if (specSection.type != IsSectionType.PARAMETER) return
        val eq = entry.node.findChildByType(IsSectionTypes.EQ) ?: return
        reportSeparatorMismatch(
            eq.textRange, section.nameText, ":", "=", "${entry.keyText()}: Value",
            ReplaceSeparatorQuickFix(entry, IsSectionTypes.EQ, "=", ":"), holder
        )
    }

    /**
     * Flags an entry that uses the wrong key/value separator for its section: ':' inside a directive
     * section, or '=' inside a parameter section. The red range covers the offending separator.
     */
    private fun reportSeparatorMismatch(
        range: TextRange,
        sectionName: String,
        expected: String,
        wrong: String,
        example: String,
        fix: IntentionAction,
        holder: IsAnnotationSink
    ) {
        // No custom text attributes: the default ERROR highlight draws a red wavy underline under
        // the separator, rather than recolouring the character (UNKNOWN_REFERENCE would paint it red).
        holder.newAnnotation(
            HighlightSeverity.ERROR,
            "Section [$sectionName] separates key and value with '$expected' (e.g. $example), not '$wrong'"
        )
            .range(range)
            .withFix(fix)
            .create()
    }

    private fun annotateKey(
        range: TextRange,
        attr: IsSectionAttributeSpec?,
        holder: IsAnnotationSink,
        target: IsSectionSpecTarget,
        keyName: String
    ) {
        when {
            attr == null ->
                holder.newAnnotation(HighlightSeverity.ERROR, "Unknown parameter: '$keyName'")
                    .range(range)
                    .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                    .create()

            attr.deprecated.appliesTo(target) ->
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(range)
                    .textAttributes(IsSectionAnnotatorHighlighting.DEPRECATED)
                    .create()

            else -> {
                annotateVersion(range, attr.name, attr.since, attr.until, holder)
                highlight(range, IsSectionAnnotatorHighlighting.PARAM_KEY, holder)
            }
        }
    }

    private fun annotateVersion(
        range: TextRange, name: String,
        since: String?, until: String?,
        holder: IsAnnotationSink
    ) {
        val minVersion = IsSettingsService.getInstance().state.minInnoVersion ?: return
        until?.let {
            if (IsSettingsService.compareIsVersions(it, minVersion) <= 0) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "'$name' was removed in Inno Setup $it"
                ).range(range).create()
                return
            }
        }
        since?.let {
            if (IsSettingsService.compareIsVersions(it, minVersion) > 0) {
                holder.newAnnotation(
                    HighlightSeverity.WARNING,
                    "'$name' requires Inno Setup $it or later (configured minimum: $minVersion)"
                ).range(range).create()
            }
        }
    }

    private fun annotateParamValue(value: IsSectionParamValue, holder: IsAnnotationSink, spec: IsSectionSpec) {
        if (value.isInCodeSection) return
        val attr = resolveAttr(value, spec) ?: return
        when (val type = attr.type) {
            is IsSectionFlagTypeSpec -> annotateFlagValue(value, type, holder)
            is IsSectionNativeTypeSpec -> annotateNativeValue(value, type, holder)
            is IsSectionFileTypeSpec -> annotatePathValue(value, requireDirectory = false, type.existence, holder)
            is IsSectionDirectoryTypeSpec -> annotatePathValue(value, requireDirectory = true, type.existence, holder)
            is IsSectionReferenceTypeSpec -> {
                val pair = value.containingParamPair
                if (pair?.isReferenceParam() == true) {
                    value.identifiers.forEach {
                        highlight(it.textRange, IsSectionAnnotatorHighlighting.REFERENCE, holder)
                    }
                }
            }
        }
    }

    /**
     * Default validation for `file`/`directory` typed attributes, dispatching on [existence]:
     *
     * - [IsSectionPathExistence.REQUIRED] ([annotatePathExistence]): the value is resolved (expanding
     *   ISPP defines, env vars, and built-in constants) relative to the script directory and checked to
     *   exist and be of the expected kind. The `compiler:` prefix and the build-machine installation path
     *   are honoured via [IsMessagesFileResolver]. Values that cannot be unambiguously checked are skipped
     *   without an annotation: empty values, wildcard patterns (`*`, `?`), comma-separated lists, and
     *   values with unresolvable `{…}` placeholders.
     * - [IsSectionPathExistence.OPTIONAL] ([annotatePathCharacters]): the path need not exist (a
     *   target/runtime path such as `\[Files]` `DestDir`); only invalid path characters are reported.
     *
     * The `\[Languages]` `MessagesFile` directive keeps its dedicated handling
     * ([annotateMessagesFile] — `compiler:` plus ISL content validation) and is intentionally skipped
     * here.
     */
    private fun annotatePathValue(
        value: IsSectionParamValue,
        requireDirectory: Boolean,
        existence: IsSectionPathExistence,
        holder: IsAnnotationSink
    ) {
        // [Languages] MessagesFile is handled exclusively by annotateMessagesFile (the documented exception).
        val pair = value.containingParamPair
        if (pair?.keyText().equals("MessagesFile", ignoreCase = true) &&
            pair?.containingSection?.nameText.equals("Languages", ignoreCase = true)
        ) return

        val raw = value.singleText.trim()
        if (raw.isEmpty()) return

        when (existence) {
            IsSectionPathExistence.OPTIONAL -> annotatePathCharacters(value, raw, holder)
            IsSectionPathExistence.REQUIRED -> annotatePathExistence(value, raw, requireDirectory, holder)
        }
    }

    /**
     * For `optional` file/directory values (target/runtime paths such as `\[Files]` `DestDir`) the path
     * need not exist at compile time, so only the literal value is checked for characters that are
     * invalid in a Windows path. `{…}` constants/ISPP emissions are stripped first (they may legitimately
     * contain `|`, e.g. `{code:Foo|bar}`), so only user-typed invalid characters are reported.
     *
     * A `:` is only valid as a drive specifier — directly before the first `/` or `\` (e.g. `C:\App`) —
     * or as a URL scheme separator (`scheme://…`, since e.g. `\[Icons]` `Filename` may be a URL). Any
     * other colon is reported as invalid.
     */
    private fun annotatePathCharacters(value: IsSectionParamValue, raw: String, holder: IsAnnotationSink) {
        val stripped = raw.replace(Regex("\\{[^}]*}"), "")
        val invalid = sortedSetOf<Char>()
        stripped.forEachIndexed { i, c ->
            when {
                c in INVALID_PATH_CHARS -> invalid.add(c)
                c == ':' && !isValidColon(stripped, i) -> invalid.add(':')
            }
        }
        if (invalid.isEmpty()) return
        holder.newAnnotation(
            HighlightSeverity.ERROR,
            "Invalid character(s) in path: " + invalid.joinToString(" ") { "'$it'" }
        ).range(value.textRange)
            .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
            .create()
    }

    /**
     * Whether the `:` at [index] in [path] is a legal Windows colon: a drive letter (`X:` at the start,
     * immediately followed by `/` or `\`) or a URL scheme separator (`://`).
     */
    private fun isValidColon(path: String, index: Int): Boolean {
        // URL scheme: "://"
        if (index + 2 <= path.lastIndex && path[index + 1] == '/' && path[index + 2] == '/') return true
        // Drive letter: single letter, then ':' directly before the first '/' or '\'.
        return index == 1 && path[0].isLetter() &&
                index + 1 <= path.lastIndex && (path[index + 1] == '/' || path[index + 1] == '\\')
    }

    /**
     * For `required` file/directory values the path must exist on the build machine; resolves and checks
     * existence and kind (see [annotatePathValue] doc).
     */
    private fun annotatePathExistence(
        value: IsSectionParamValue,
        raw: String,
        requireDirectory: Boolean,
        holder: IsAnnotationSink
    ) {
        // Wildcards (patterns) and comma-separated lists cannot be checked as a single path.
        if (raw.any { it == '*' || it == '?' } || raw.contains(',')) return

        val scriptVf = value.containingFile?.virtualFile ?: return
        // Relative paths are resolved against the script's directory on disk. When that directory is not a
        // real filesystem location (e.g. an in-memory/temp VFS), relative resolution is unreliable, so it
        // is left to resolveMessagesFile to skip it (scriptDir == null → Unresolvable). Absolute and
        // `compiler:` paths do not depend on scriptDir and are still validated.
        val scriptDir = scriptVf.parent?.path?.let { File(it) }?.takeIf { it.isDirectory }
        val scope = value.issFile?.declarationScope()
        val defines = scope?.definedConstants ?: emptyList()
        val installPath = IsSettingsService.getInstance().state.installationPath

        val expanded = IsMessagesFileResolver.expandValue(raw, defines, scriptDir, emptyMap(), installPath)
            ?: return  // unresolvable placeholder — no annotation
        if (expanded.any { it == '*' || it == '?' }) return

        val noun = if (requireDirectory) "Directory" else "File"
        when (val result = IsMessagesFileResolver.resolveMessagesFile(expanded, scriptDir, installPath)) {
            is IsResolveResult.Missing ->
                holder.newAnnotation(HighlightSeverity.ERROR, "$noun not found: '${result.resolvedPath}'")
                    .range(value.textRange)
                    .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                    .create()

            is IsResolveResult.Unreadable ->
                holder.newAnnotation(HighlightSeverity.ERROR, "$noun is not readable: '${result.resolvedPath}'")
                    .range(value.textRange)
                    .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                    .create()

            IsResolveResult.NotConfigured ->
                holder.newAnnotation(
                    HighlightSeverity.WARNING,
                    "Inno Setup installation path not configured — cannot verify existence of '$raw'"
                ).range(value.textRange).create()

            IsResolveResult.Unresolvable -> Unit  // no annotation

            is IsResolveResult.Ok -> {
                if (requireDirectory && !result.file.isDirectory) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Expected a directory but found a file: '${result.file.absolutePath}'")
                        .range(value.textRange)
                        .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                        .create()
                } else if (!requireDirectory && result.file.isDirectory) {
                    holder.newAnnotation(HighlightSeverity.ERROR, "Expected a file but found a directory: '${result.file.absolutePath}'")
                        .range(value.textRange)
                        .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                        .create()
                }
            }
        }
    }

    private fun resolveAttr(value: IsSectionParamValue, spec: IsSectionSpec): IsSectionAttributeSpec? {
        val pair = value.containingParamPair
        if (pair != null) {
            val ss = pair.containingSection?.specSection(spec) ?: return null
            return ss.attributes.firstOrNull { it.name.equals(pair.keyText(), ignoreCase = true) }
        }
        val dir = value.containingDirectiveEntry ?: return null
        val ss = dir.containingSection?.specSection(spec) ?: return null
        return ss.attributes.firstOrNull { it.name.equals(dir.keyText(), ignoreCase = true) }
    }

    private fun annotateFlagValue(
        value: IsSectionParamValue,
        flagType: IsSectionFlagTypeSpec,
        holder: IsAnnotationSink
    ) {
        val target = value.specTarget
        val flagMap = flagType.flags.associateBy { it.name.lowercase() }
        // All flag tokens in order. A flag may legitimately appear here more than once (duplicate) — keep
        // every occurrence so each is validated and highlighted (a deduped map is used for cross-flag lookups).
        val tokenList = value.node.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER)).toList()
        val byName: Map<String, List<ASTNode>> = tokenList.groupBy { it.text.lowercase() }
        // First occurrence per flag name — the authority for conflict/required cross-references.
        val tokenNodes: Map<String, ASTNode> = byName.mapValues { it.value.first() }

        // Validate and highlight every occurrence (so duplicates do not silently lose their colour).
        tokenList.forEach { node ->
            val def = flagMap[node.text.lowercase()]
            when {
                def == null ->
                    holder.newAnnotation(HighlightSeverity.ERROR, "Unknown flag: '${node.text}'")
                        .range(node.textRange)
                        .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                        .create()

                def.deprecated.appliesTo(target) ->
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(node.textRange)
                        .textAttributes(IsSectionAnnotatorHighlighting.DEPRECATED)
                        .create()

                else -> {
                    annotateVersion(node.textRange, def.name, def.since, def.until, holder)
                    highlight(node.textRange, IsSectionAnnotatorHighlighting.FLAG, holder)
                }
            }
        }

        // Duplicate flags: every occurrence after the first of a (known) flag is an error. Unknown flags are
        // already reported above, so they are skipped here.
        byName.forEach { (name, nodes) ->
            if (nodes.size < 2 || flagMap[name] == null) return@forEach
            nodes.drop(1).forEach { dup ->
                holder.newAnnotation(HighlightSeverity.ERROR, "Duplicate flag: '${dup.text}'")
                    .range(dup.textRange)
                    .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                    .withFix(RemoveDuplicateFlagsQuickFix(value, dup.text))
                    .create()
            }
        }

        val seen = mutableSetOf<Pair<String, String>>()
        tokenNodes.forEach { (name, node) ->
            val def = flagMap[name] ?: return@forEach
            def.conflicts.forEach conflict@{ conflict ->
                // 'requires' is a dependency, not a conflict — handled in its own pass below.
                if (conflict.type == IsSectionFlagType.REQUIRES) return@conflict

                val otherName = conflict.flag.lowercase()
                val other = tokenNodes[otherName] ?: return@conflict

                if (conflict.type == IsSectionFlagType.REDUNDANT) {
                    // Asymmetric: 'name' implicitly sets 'otherName' (or nullifies its effect),
                    // so the other flag is redundant. Only that flag is marked (grey, like unused).
                    if (!seen.add(name to otherName)) return@conflict
                    holder.newAnnotation(
                        HighlightSeverity.WEAK_WARNING,
                        "Flag '${other.text}' is redundant — already implied by '${node.text}'"
                    )
                        .range(other.textRange)
                        .textAttributes(IsSectionAnnotatorHighlighting.UNUSED)
                        .withFix(RemoveRedundantFlagQuickFix(value, other.text))
                        .create()
                    return@conflict
                }

                val key = if (name < otherName) name to otherName else otherName to name
                if (!seen.add(key)) return@conflict

                val severity = if (conflict.type == IsSectionFlagType.FORBIDDEN)
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
                .filter { it.type == IsSectionFlagType.REQUIRES }
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

    private fun annotateNativeValue(
        value: IsSectionParamValue,
        type: IsSectionNativeTypeSpec,
        holder: IsAnnotationSink
    ) {
        val text = value.singleText
        when (type.dataType) {
            IsSectionNativeDataType.BOOLEAN -> {
                if (text.lowercase() in setOf("yes", "no")) {
                    value.node.getChildren(TokenSet.create(IsSectionTypes.IDENTIFIER)).forEach {
                        highlight(it.textRange, IsSectionSyntaxHighlighting.KEYWORD, holder)
                    }
                } else {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Expected type '${type.dataType.typeName}', got: '$text'"
                    ).range(value.textRange).create()
                }
            }

            // Accept decimal (-?[0-9]+) or Pascal-style hexadecimal ($ followed by hex digits, e.g. $0409)
            IsSectionNativeDataType.INTEGER -> if (!text.matches(Regex("-?[0-9]+")) && !text.matches(Regex("\\\$[0-9A-Fa-f]+"))) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Expected type '${type.dataType.typeName}', got: '$text'"
                ).range(value.textRange).create()
            }

            IsSectionNativeDataType.STRING -> Unit  // free-form text — no validation
        }
    }

    private fun annotateConstant(constant: IsSectionConstant, holder: IsAnnotationSink) {
        if (constant.isInCodeSection) return
        val body = constant.constantBody
        val bodyText = body.text.trimStart()
        val name = bodyText.substringBefore(':').substringBefore('|').trim().trimStart('#')

        val builtins = service<IsConstantService>().spec.constants
        // #defines may live in an included file — resolve over the effective (#include-resolved) script.
        val isppNames = constant.issFile?.declarationScope()?.definedConstants?.map { it.first } ?: emptyList()
        // Value-bearing ISPP predefined variables ({#__LINE__}, {#SourcePath}, …) are valid inline
        // emissions too; the valueless `void` symbols are not emittable via {#…} and stay unknown here.
        val predefinedNames = service<IsPreprocessorService>().emittableVariables.map { it.name }
        val isIspp = bodyText.startsWith("#")
        val isEnv = bodyText.startsWith("%")

        // {%ENV} or {%ENV|default} — validate the environment variable name
        if (isEnv) {
            val rest = bodyText.removePrefix("%")
            val pipe = rest.indexOf('|')
            val envName = if (pipe >= 0) rest.substring(0, pipe) else rest
            val hasDefault = pipe >= 0
            if (System.getenv(envName) == null && !hasDefault) {
                val startOffset = constant.textRange.startOffset + 1 + (bodyText.length - rest.length)
                val endOffset = startOffset + envName.length
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Unknown environment variable '$envName'"
                ).range(TextRange(startOffset, endOffset))
                    .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                    .create()
            } else {
                highlight(constant.textRange, IsSectionAnnotatorHighlighting.REFERENCE, holder)
            }
            return
        }

        val known = when {
            isIspp -> name in isppNames || predefinedNames.any { it.equals(name, ignoreCase = true) }
            else -> builtins.any { it.name.equals(name, ignoreCase = true) }
        }

        if (!known) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unknown constant: '{${body.text}}'")
                .range(constant.textRange)
                .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        } else if (isIspp) {
            body.node.findChildByType(IsSectionTypes.HASH)?.let {
                highlight(it.textRange, IsSectionAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)
            }
            body.node.findChildByType(IsSectionTypes.IDENTIFIER)?.let {
                highlight(it.textRange, IsSectionAnnotatorHighlighting.ISPP_REFERENCE_NAME, holder)
            }
        } else {
            val constSpec = builtins.firstOrNull { it.name.equals(name, ignoreCase = true) }
            // Version (since/until) and deprecation are independent dimensions: a constant may be both
            // removed in a version (red "removed"/"requires" line) and deprecated (struck through).
            if (constSpec != null) {
                annotateVersion(constant.textRange, "{${constSpec.name}}", constSpec.since, constSpec.until, holder)
            }
            if (constSpec != null && constSpec.deprecated.appliesTo(constant.specTarget)) {
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(constant.textRange)
                    .textAttributes(IsSectionAnnotatorHighlighting.DEPRECATED)
                    .create()
            } else {
                highlight(constant.textRange, IsSectionAnnotatorHighlighting.REFERENCE, holder)
            }
            if (name.equals("cm", ignoreCase = true)) {
                // Render the `cm` keyword italic (layers over the reference colour above).
                body.node.findChildByType(IsSectionTypes.IDENTIFIER)?.let {
                    highlight(it.textRange, IsSectionAnnotatorHighlighting.CUSTOM_MESSAGE_PREFIX, holder)
                }
                annotateCustomMessage(constant, body, holder)
            }
        }
    }

    /**
     * Flags a `{cm:Name}` whose message name is not defined in any \[CustomMessages] section. The
     * red highlight covers only the name token, mirroring the unknown-flag/unknown-constant style.
     */
    private fun annotateCustomMessage(
        constant: IsSectionConstant,
        body: IsSectionConstantBody,
        holder: IsAnnotationSink
    ) {
        val (msgName, nameRange) = body.customMessageNameRange() ?: return

        // [CustomMessages] entries may be contributed by an #include — resolve over the effective script.
        val declared = constant.issFile?.declarationScope()?.findSections("CustomMessages")
            ?.flatMap { it.directiveEntryList }
            ?.mapNotNull { (it as? IsSectionDirectiveEntryEx)?.customMessageName() }
            ?: emptyList()
        if (declared.none { it.equals(msgName, ignoreCase = true) }) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Unknown custom message: '$msgName' (not defined in [CustomMessages])"
            )
                .range(nameRange.shiftRight(body.textRange.startOffset))
                .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        }
    }

    private fun highlight(range: TextRange, key: TextAttributesKey, holder: IsAnnotationSink) =
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range).textAttributes(key).create()

    private companion object {
        // Characters that are never valid in a Windows path or filename. The path separators '\\' and '/'
        // and the drive ':' are intentionally excluded (they are valid in paths), as are the wildcards
        // '*'/'?' which legitimately appear in delete/source patterns.
        val INVALID_PATH_CHARS = setOf('<', '>', '"', '|')
    }
}
