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
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.include.IsAnnotationSink
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.feature.include.PlatformAnnotationSink
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsBranchAnalysis
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.IsPreprocessorBranchAnalysis
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.expression.IsPreprocessorExprParser
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser.isExternalPreprocessorSymbol
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.IsSectionSpecTarget
import org.pcsoft.intellij.plugin.inno_setup.preprocessor.types.appliesTo
import org.pcsoft.intellij.plugin.inno_setup.script.build.IsScriptCollector
import org.pcsoft.intellij.plugin.inno_setup.script.language.feature.IsMessagesFileResolver
import org.pcsoft.intellij.plugin.inno_setup.script.language.feature.IsResolveResult
import org.pcsoft.intellij.plugin.inno_setup.script.language.feature.include.declarationScope
import org.pcsoft.intellij.plugin.inno_setup.script.language.feature.include.toEffectiveScript
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.issFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.lang.specTarget
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.*
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.quickfix.*
import org.pcsoft.intellij.plugin.inno_setup.script.services.IsConstantService
import org.pcsoft.intellij.plugin.inno_setup.script.services.IsLanguageDataService
import org.pcsoft.intellij.plugin.inno_setup.script.services.IsSpecService
import org.pcsoft.intellij.plugin.inno_setup.script.settings.IsSettingsService
import org.pcsoft.intellij.plugin.inno_setup.script.types.*
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

        // Conditional compilation: dim the bodies of provably skipped #if branches and stop reporting
        // anything inside them. Diagnostics about code the build never sees are noise at best — a red
        // squiggle in greyed-out dead code is worse than no analysis at all.
        val branches = IsPreprocessorBranchAnalysis.analyze(element.containingFile)
        if (element is IsScriptFile) {
            annotateInactiveBranches(branches, holder)
        } else if (branches.isInactive(element.textRange)) {
            return
        }

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
     * Dims the body of every `#if` branch the analysis proved is not compiled.
     *
     * Emitted once per file (from the [IsScriptFile] visit) rather than per element, because the dead body is
     * plain script text spanning arbitrarily many PSI elements — and because a branch whose condition is
     * merely undecidable must keep its normal colours.
     */
    private fun annotateInactiveBranches(branches: IsBranchAnalysis, holder: IsAnnotationSink) {
        branches.inactiveRanges.forEach { range ->
            holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                .range(range)
                .textAttributes(IsSectionAnnotatorHighlighting.INACTIVE_BRANCH)
                .create()
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
            val required = specSection.attributes.filter { it.required.appliesTo(target) }
            if (required.isEmpty()) return@forEach

            val blocks = effective.findSections(specSection.name)
            if (blocks.isEmpty()) return@forEach   // section absent → covered by the required-section check

            val present = blocks.flatMap { it.directiveEntryList }.map { it.keyText().lowercase() }.toSet()
            // An attribute with alternatives (AppVersion ↔ AppVerName) counts as supplied as soon as any of
            // them is present — Inno Setup accepts either one.
            val missing = required
                .filterNot { attribute ->
                    attribute.name.lowercase() in present ||
                            attribute.required?.except?.byAttribute.orEmpty()
                                .any { it.lowercase() in present }
                }
                .map { it.name.lowercase() }
                .toSet()
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
     * When the effective AppId contains a constant (e.g. a \{code:…} expression), Inno Setup cannot reuse
     * the previously selected language and requires `UsePreviousLanguage` to be set explicitly to `no`;
     * otherwise the compiler rejects the script. `AppId` defaults to `AppName` when omitted, so the
     * `AppName` value is checked in that case. Reported at file level.
     *
     * ISPP emissions (`\{#Name}`) are **not** constants for this rule: the preprocessor replaces them with
     * literal text before ISCC ever sees the script, so the compiled AppId holds no constant at all.
     */
    private fun annotateUsePreviousLanguage(file: IsScriptFile, holder: IsAnnotationSink) {
        if (file.specTarget != IsSectionSpecTarget.ISS) return
        val setup = file.findSection("Setup") ?: return

        // AppId defaults to AppName when not specified, so fall back to the AppName value.
        val appIdValue = (setup.directiveEntryList.firstOrNull { it.keyText().equals("AppId", ignoreCase = true) }
            ?: setup.directiveEntryList.firstOrNull { it.keyText().equals("AppName", ignoreCase = true) })
            ?.paramValue ?: return
        val constants = PsiTreeUtil.findChildrenOfType(appIdValue, IsSectionConstant::class.java)
            .filterNot { it.constantBody.text.trimStart().startsWith("#") }
        if (constants.isEmpty()) return

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
        val declaredFlags = entry.declaredFlags()
        val required = specSection.attributes
            .filter { it.required.appliesTo(target) }
            // A flag of the same entry can waive the requirement (`DestDir` with `dontcopy`) — the same
            // spec-driven mechanism the file-existence check uses.
            .filterNot { attribute ->
                attribute.required?.except?.byFlag.orEmpty().any { waiving ->
                    declaredFlags.any { it.equals(waiving, ignoreCase = true) }
                }
            }
            .map { it.name.lowercase() }
            .toSet()
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
            is IsSectionFileTypeSpec ->
                annotatePathValue(value, requireDirectory = false, value.mustExist(type.mustExists), holder)

            is IsSectionDirectoryTypeSpec ->
                annotatePathValue(value, requireDirectory = true, value.mustExist(type.mustExists), holder)
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
     * Whether the compile-time existence check applies to this value, given the [mustExists] block of
     * its spec type. The check is off when the spec declares no `mustExists`, and it is switched off for
     * this particular entry when one of `mustExists.except.by-flag` is declared on it (`\[Files]`
     * `Source` with `external` or `skipifsourcedoesntexist`) — then only the path characters are checked.
     */
    private fun IsSectionParamValue.mustExist(mustExists: IsSectionMustExists?): Boolean =
        mustExists != null && !hasAnyFlag(mustExists.except?.byFlag.orEmpty())

    /**
     * Default validation for `file`/`directory` typed attributes, dispatching on [mustExist]:
     *
     * - `true` ([annotatePathExistence]): the value is resolved (expanding
     *   ISPP defines, env vars, and built-in constants) relative to the script directory and checked to
     *   exist and be of the expected kind. The `compiler:` prefix and the build-machine installation path
     *   are honoured via [IsMessagesFileResolver]. Values that cannot be unambiguously checked are skipped
     *   without an annotation: empty values, wildcard patterns (`*`, `?`), comma-separated lists, and
     *   values with unresolvable `{…}` placeholders.
     * - `false` ([annotatePathCharacters]): the path need not exist (a
     *   target/runtime path such as `\[Files]` `DestDir`); only invalid path characters are reported.
     *
     * The `\[Languages]` `MessagesFile` directive keeps its dedicated handling
     * ([annotateMessagesFile] — `compiler:` plus ISL content validation) and is intentionally skipped
     * here.
     */
    private fun annotatePathValue(
        value: IsSectionParamValue,
        requireDirectory: Boolean,
        mustExist: Boolean,
        holder: IsAnnotationSink
    ) {
        // [Languages] MessagesFile is handled exclusively by annotateMessagesFile (the documented exception).
        val pair = value.containingParamPair
        if (pair?.keyText().equals("MessagesFile", ignoreCase = true) &&
            pair?.containingSection?.nameText.equals("Languages", ignoreCase = true)
        ) return

        val raw = value.singleText.trim()
        if (raw.isEmpty()) return

        if (mustExist) annotatePathExistence(value, raw, requireDirectory, holder)
        else annotatePathCharacters(value, raw, holder)
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
     * Whether the `:` at [index] in [path] is a legal colon: a drive letter (`X:` at the start, immediately
     * followed by `/` or `\`), a URL scheme separator (`://`), or the terminator of one of the compile-time
     * path prefixes ISCC understands ([COMPILE_TIME_PATH_PREFIXES], e.g.
     * `OutputDir=userdocs:Inno Setup Examples Output`).
     */
    private fun isValidColon(path: String, index: Int): Boolean {
        // URL scheme: "://"
        if (index + 2 <= path.lastIndex && path[index + 1] == '/' && path[index + 2] == '/') return true
        // Compile-time prefix: the colon terminates a known keyword at the very start of the value.
        if (COMPILE_TIME_PATH_PREFIXES.any { it.length == index && path.startsWith(it, ignoreCase = true) })
            return true
        // Drive letter: single letter, then ':' directly before the first '/' or '\'.
        return index == 1 && path[0].isLetter() &&
                index + 1 <= path.lastIndex && (path[index + 1] == '/' || path[index + 1] == '\\')
    }

    /**
     * Whether the entry this value belongs to carries any of [flags] in its `Flags` parameter
     * (case-insensitive). Used to let a flag waive the compile-time existence requirement of a path.
     */
    private fun IsSectionParamValue.hasAnyFlag(flags: Set<String>): Boolean {
        if (flags.isEmpty()) return false
        val declared = containingParameterEntry?.declaredFlags() ?: return false
        return declared.any { declaredFlag -> flags.any { it.equals(declaredFlag, ignoreCase = true) } }
    }

    /** The flag names declared in the `Flags` parameter of this entry (whitespace-separated, unmodified). */
    private fun IsSectionParameterEntry.declaredFlags(): List<String> =
        paramPairList
            .filter { it.keyText().equals("Flags", ignoreCase = true) }
            .flatMap { it.paramValue?.singleText.orEmpty().trim().split(Regex("\\s+")) }
            .filter { it.isNotBlank() }

    /**
     * For `required` file/directory values the path must exist on the build machine; resolves and checks
     * existence and kind (see [annotatePathValue] doc).
     *
     * A missing or unreadable path is reported as a WARNING, not an ERROR: whether a file has to be present
     * at compile time depends on the entry's flags and on how the build is set up (generated artefacts,
     * files produced by a prior build step), so the analysis cannot decide it with certainty. The cases it
     * *can* decide — a directory where a file is expected and vice versa — stay errors.
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
                holder.newAnnotation(HighlightSeverity.WARNING, "$noun not found: '${result.resolvedPath}'")
                    .range(value.textRange)
                    .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                    .create()

            is IsResolveResult.Unreadable ->
                holder.newAnnotation(HighlightSeverity.WARNING, "$noun is not readable: '${result.resolvedPath}'")
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
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Expected a directory but found a file: '${result.file.absolutePath}'"
                    )
                        .range(value.textRange)
                        .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                        .create()
                } else if (!requireDirectory && result.file.isDirectory) {
                    holder.newAnnotation(
                        HighlightSeverity.ERROR,
                        "Expected a file but found a directory: '${result.file.absolutePath}'"
                    )
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

            // Accept decimal or Pascal-style hexadecimal ($ followed by hex digits, e.g. $0409). A decimal
            // number may group its digits with underscores for readability (`7_000_000`) — Inno Setup accepts
            // that in every numeric directive/parameter, and the official DownloadFiles.iss example uses it.
            // The underscore must sit *between* digits, so a leading, trailing or doubled one stays an error.
            IsSectionNativeDataType.INTEGER -> if (!text.matches(INTEGER_VALUE) && !text.matches(HEX_VALUE)) {
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

        // ISCC `/D<name>` symbols of the selected build configuration have no declaration in the script, but
        // they are defined for the build — emitting one via {#Name} is legitimate, not an unknown constant.
        val isExternal = isIspp && constant.containingFile?.isExternalPreprocessorSymbol(name) == true

        // An inline emission may carry a whole ISPP directive rather than a plain name — e.g.
        // `LicenseFile={#file AddBackslash(SourcePath) + "License.txt"}` embeds the `#file` directive. Such a
        // body is not a symbol name and must not be looked up as one; it is validated as the directive it is.
        if (isIspp && annotateInlineDirective(constant, body, bodyText, holder)) return

        val known = when {
            isIspp -> name in isppNames || predefinedNames.any { it.equals(name, ignoreCase = true) } ||
                    isExternal
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
     * Handles an inline emission whose body is an ISPP **directive** rather than a symbol name, e.g.
     * `{#file AddBackslash(SourcePath) + "License.txt"}`.
     *
     * Returns `false` when the body is not a directive, so the caller falls back to the plain
     * `{#Name}` symbol check. Otherwise the directive is validated according to what it is:
     *
     * - [EXPRESSION_DIRECTIVES] take a value expression — it is parsed and its syntax errors are reported.
     * - [FREEFORM_DIRECTIVES] take free-form text (`#pragma`, `#error`) — nothing to check.
     * - every other directive is a declaration or a control-flow directive and cannot produce a value, so
     *   using it inside `{#…}` is reported as an error.
     */
    private fun annotateInlineDirective(
        constant: IsSectionConstant,
        body: IsSectionConstantBody,
        bodyText: String,
        holder: IsAnnotationSink,
    ): Boolean {
        val withoutHash = bodyText.removePrefix("#").trimStart()
        val keyword = withoutHash.takeWhile { it.isLetter() }
        if (keyword.isEmpty()) return false

        val directive = service<IsPreprocessorService>().spec.directives
            .firstOrNull { it.name.equals(keyword, ignoreCase = true) }
            ?: return false
        // `{#Name}` where Name happens to read like a directive is only a directive when an argument
        // follows — `{#file}` alone is a reference to a #define called "file".
        val argument = withoutHash.removePrefix(keyword).trim()
        if (argument.isEmpty()) return false

        body.node.findChildByType(IsSectionTypes.HASH)?.let {
            highlight(it.textRange, IsSectionAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)
        }
        body.node.findChildByType(IsSectionTypes.IDENTIFIER)?.let {
            highlight(it.textRange, IsSectionAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)
        }

        val name = directive.name.lowercase()
        when {
            name in FREEFORM_DIRECTIVES -> Unit

            name in EXPRESSION_DIRECTIVES -> {
                val errors = IsPreprocessorExprParser.parse(argument).errors
                errors.firstOrNull()?.let {
                    holder.newAnnotation(HighlightSeverity.ERROR, "#${directive.name}: ${it.message}")
                        .range(constant.textRange)
                        .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                        .create()
                }
            }

            else -> holder.newAnnotation(
                HighlightSeverity.ERROR,
                "'#${directive.name}' produces no value and cannot be used in an inline emission"
            ).range(constant.textRange)
                .textAttributes(IsSectionAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        }
        return true
    }

    /**
     * Flags a `{cm:Name}` whose message name is not defined in any \[CustomMessages] section. The
     * red highlight covers only the name token, mirroring the unknown-flag/unknown-constant style.
     *
     * The script's own entries are not the only source: the language files shipped with Inno Setup
     * (`Default.isl` and the files under `Languages`) carry a `\[CustomMessages]` section of their own, which
     * is why `{cm:UninstallProgram}` resolves in the official `Languages.iss` example without the script
     * declaring it. Those files can only be read when the installation path is configured, so the check is
     * skipped — and only then — when the script pulls in a `MessagesFile` whose messages are out of reach.
     * A script that declares no `MessagesFile` has no such hidden source and is checked as before.
     */
    private fun annotateCustomMessage(
        constant: IsSectionConstant,
        body: IsSectionConstantBody,
        holder: IsAnnotationSink
    ) {
        val (msgName, nameRange) = body.customMessageNameRange() ?: return
        if (constant.issFile?.declaresUnreadableMessagesFile() == true) return

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

    /**
     * Whether this script declares a `\[Languages]` `MessagesFile` whose `\[CustomMessages]` section cannot
     * be consulted, because no Inno Setup installation path is configured. The language files shipped with
     * Inno Setup define custom messages of their own, so as long as they are out of reach a `{cm:…}` name
     * that is missing from the script may still be perfectly valid.
     */
    private fun IsScriptFile.declaresUnreadableMessagesFile(): Boolean {
        if (!IsSettingsService.getInstance().state.installationPath.isNullOrBlank()) return false
        return declarationScope().findSections("Languages")
            .flatMap { it.parameterEntryList }
            .flatMap { it.paramPairList }
            .any { it.keyText().equals("MessagesFile", ignoreCase = true) }
    }

    private fun highlight(range: TextRange, key: TextAttributesKey, holder: IsAnnotationSink) =
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range).textAttributes(key).create()

    private companion object {
        // Characters that are never valid in a Windows path or filename. The path separators '\\' and '/'
        // and the drive ':' are intentionally excluded (they are valid in paths), as are the wildcards
        // '*'/'?' which legitimately appear in delete/source patterns.
        val INVALID_PATH_CHARS = setOf('<', '>', '"', '|')

        // Keywords ISCC accepts in front of a path, separated by a colon: `compiler:` (relative to the
        // Inno Setup installation directory) and `userdocs:` (relative to the compiling user's Documents
        // folder, e.g. the `OutputDir` of the official example scripts). Their colon is not a drive
        // specifier, so it must not be reported as an invalid path character.
        val COMPILE_TIME_PATH_PREFIXES = listOf("compiler", "userdocs")

        /** Decimal integer, optionally with `_` digit grouping between digits (`7_000_000`). */
        val INTEGER_VALUE = Regex("-?[0-9]+(_[0-9]+)*")

        /** Pascal-style hexadecimal literal (`$0409`). */
        val HEX_VALUE = Regex("\\\$[0-9A-Fa-f]+")

        /** ISPP directives that take a value expression and may therefore appear inside `{#…}`. */
        val EXPRESSION_DIRECTIVES = setOf("emit", "expr", "file", "insert", "append", "include")

        /** ISPP directives whose argument is free-form text, so there is nothing to validate. */
        val FREEFORM_DIRECTIVES = setOf("pragma", "error")
    }
}
