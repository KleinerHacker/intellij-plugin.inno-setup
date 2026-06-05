package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.*
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.*
import org.pcsoft.intellij.plugin.inno_setup.language.ispp.definedConstants
import org.pcsoft.intellij.plugin.inno_setup.language.issFile
import org.pcsoft.intellij.plugin.inno_setup.services.IssConstantService
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.*

class IsiAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val spec = service<IssSpecService>().spec
        when (element) {
            is IssFile -> annotateFile(element, holder, spec)
            is IsiSectionName -> annotateSectionName(element, holder, spec)
            is IsiSection -> annotateSection(element, holder, spec)
            is IsiParameterEntry -> annotateParameterEntry(element, holder, spec)
            is IsiParamKey -> annotateParamKey(element, holder, spec)
            is IsiDirectiveKey -> annotateDirectiveKey(element, holder, spec)
            is IsiParamValue -> annotateParamValue(element, holder, spec)
            is IsiConstant -> annotateConstant(element, holder)
        }
    }

    private fun annotateFile(file: IssFile, holder: AnnotationHolder, spec: InnoSetupSpec) {
        val required = spec.sections.filter { it.required }.map { it.name.lowercase() }.toSet()
        val existing = file.sections().map { it.nameText().lowercase() }.toSet()
        val missing = required - existing
        if (missing.isNotEmpty()) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Required section(s) missing: " + missing.joinToString(", ") { "[$it]" }
            ).fileLevel().create()
        }

        val sections = file.sections()
        val codeIdx = sections.indexOfFirst { it.nameText().equals("Code", ignoreCase = true) }
        if (codeIdx >= 0 && codeIdx < sections.size - 1) {
            sections.subList(codeIdx, sections.size).forEach { section ->
                val isCode = section.nameText().equals("Code", ignoreCase = true)
                val msg = if (isCode)
                    "[Code] must be the last section in the script"
                else
                    "This section appears after [Code], which must be the last section"
                holder.newAnnotation(HighlightSeverity.ERROR, msg)
                    .range(section.sectionHeader.sectionName?.textRange ?: section.sectionHeader.textRange)
                    .create()
            }
        }
    }

    private fun annotateSectionName(name: IsiSectionName, holder: AnnotationHolder, spec: InnoSetupSpec) {
        if (name.isInCodeSection()) return
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

    private fun annotateSection(section: IsiSection, holder: AnnotationHolder, spec: InnoSetupSpec) {
        val specSection = section.specSection(spec) ?: return
        if (specSection.type != "directive") return

        val required = specSection.attributes.filter { it.required }.map { it.name.lowercase() }.toSet()
        val present = section.directiveEntryList.map { it.keyText().lowercase() }.toSet()
        val missing = required - present
        if (missing.isNotEmpty()) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Required directive(s) missing: " + missing.joinToString(", ")
            ).range(section.sectionHeader.sectionName?.textRange ?: section.sectionHeader.textRange).create()
        }
    }

    private fun annotateParameterEntry(entry: IsiParameterEntry, holder: AnnotationHolder, spec: InnoSetupSpec) {
        if (entry.isInCodeSection()) return
        val section = entry.containingSection() ?: return
        val specSection = section.specSection(spec) ?: return
        if (specSection.type != "parameter") return

        val required = specSection.attributes.filter { it.required }.map { it.name.lowercase() }.toSet()
        val present = entry.paramPairList.map { it.keyText().lowercase() }.toSet()
        val missing = required - present
        if (missing.isNotEmpty()) {
            val end = entry.node.lastChildNode
                ?.takeIf { it.elementType == IsiTypes.CRLF }
                ?.startOffset ?: entry.textRange.endOffset
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Required parameter(s) missing: " + missing.joinToString(", ")
            ).range(TextRange(entry.textRange.startOffset, end)).create()
        }
    }

    private fun annotateParamKey(key: IsiParamKey, holder: AnnotationHolder, spec: InnoSetupSpec) {
        if (key.isInCodeSection()) return
        val pair = key.parent as? IsiParamPair ?: return
        val section = pair.containingSection() ?: return
        val specSection = section.specSection(spec) ?: return
        val attr = specSection.attributes.firstOrNull { it.name.equals(pair.keyText(), ignoreCase = true) }
        annotateKey(key.textRange, attr, holder)
    }

    private fun annotateDirectiveKey(key: IsiDirectiveKey, holder: AnnotationHolder, spec: InnoSetupSpec) {
        if (key.isInCodeSection()) return
        val entry = key.parent as? IsiDirectiveEntry ?: return
        val section = entry.containingSection() ?: return
        val specSection = section.specSection(spec) ?: return
        val attr = specSection.attributes.firstOrNull { it.name.equals(entry.keyText(), ignoreCase = true) }
        annotateKey(key.textRange, attr, holder)
    }

    private fun annotateKey(range: TextRange, attr: IssAttributeSpec?, holder: AnnotationHolder) {
        when {
            attr == null ->
                holder.newAnnotation(HighlightSeverity.ERROR, "Unknown parameter: '${range}'")
                    .range(range)
                    .textAttributes(IsiAnnotatorHighlighting.UNKNOWN_REFERENCE)
                    .create()

            attr.deprecated ->
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(range)
                    .textAttributes(IsiAnnotatorHighlighting.DEPRECATED)
                    .create()

            else ->
                highlight(range, IsiAnnotatorHighlighting.PARAM_KEY, holder)
        }
    }

    private fun annotateParamValue(value: IsiParamValue, holder: AnnotationHolder, spec: InnoSetupSpec) {
        if (value.isInCodeSection()) return
        val attr = resolveAttr(value, spec) ?: return
        when (val type = attr.type) {
            is IssFlagTypeSpec -> annotateFlagValue(value, type, holder)
            is IssNativeTypeSpec -> annotateNativeValue(value, type, holder)
            is IssReferenceTypeSpec -> {
                val pair = value.containingParamPair()
                if (pair?.isReferenceParam() == true) {
                    value.identifiers().forEach {
                        highlight(it.textRange, IsiAnnotatorHighlighting.REFERENCE, holder)
                    }
                }
            }
        }
    }

    private fun resolveAttr(value: IsiParamValue, spec: InnoSetupSpec): IssAttributeSpec? {
        val pair = value.containingParamPair()
        if (pair != null) {
            val ss = pair.containingSection()?.specSection(spec) ?: return null
            return ss.attributes.firstOrNull { it.name.equals(pair.keyText(), ignoreCase = true) }
        }
        val dir = value.containingDirectiveEntry() ?: return null
        val ss = dir.containingSection()?.specSection(spec) ?: return null
        return ss.attributes.firstOrNull { it.name.equals(dir.keyText(), ignoreCase = true) }
    }

    private fun annotateFlagValue(value: IsiParamValue, flagType: IssFlagTypeSpec, holder: AnnotationHolder) {
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

                def.deprecated ->
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(node.textRange)
                        .textAttributes(IsiAnnotatorHighlighting.DEPRECATED)
                        .create()

                else ->
                    highlight(node.textRange, IsiAnnotatorHighlighting.FLAG, holder)
            }
        }

        val seen = mutableSetOf<Pair<String, String>>()
        tokenNodes.forEach { (name, node) ->
            val def = flagMap[name] ?: return@forEach
            def.conflicts.forEach conflict@{ conflict ->
                val other = tokenNodes[conflict.flag.lowercase()] ?: return@conflict
                val key = if (name < conflict.flag) name to conflict.flag else conflict.flag to name
                if (!seen.add(key)) return@conflict

                val severity = if (conflict.severity == IssFlagSeveritySpec.ERROR)
                    HighlightSeverity.ERROR else HighlightSeverity.WARNING
                val msg = "Conflicting flags: '${node.text}' and '${other.text}'"
                holder.newAnnotation(severity, msg).range(node.textRange).create()
                holder.newAnnotation(severity, msg).range(other.textRange).create()
            }
        }
    }

    private fun annotateNativeValue(value: IsiParamValue, type: IssNativeTypeSpec, holder: AnnotationHolder) {
        val text = value.singleText()
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
            "integer" -> if (!text.matches(Regex("-?[0-9]+"))) {
                holder.newAnnotation(
                    HighlightSeverity.ERROR,
                    "Expected type '${type.dataType}', got: '$text'"
                ).range(value.textRange).create()
            }
        }
    }

    private fun annotateConstant(constant: IsiConstant, holder: AnnotationHolder) {
        if (constant.isInCodeSection()) return
        val body = constant.constantBody
        val name = body.text.substringBefore(':').substringBefore('|').trim().trimStart('#')

        val builtins = service<IssConstantService>().spec.constants
        val isppNames = constant.issFile()?.definedConstants()?.map { it.first } ?: emptyList()
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
            highlight(constant.textRange, IsiAnnotatorHighlighting.REFERENCE, holder)
        }
    }

    private fun highlight(range: TextRange, key: TextAttributesKey, holder: AnnotationHolder) =
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range).textAttributes(key).create()
}
