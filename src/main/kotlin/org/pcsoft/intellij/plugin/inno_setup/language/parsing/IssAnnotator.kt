package org.pcsoft.intellij.plugin.inno_setup.language.parsing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.colors.TextAttributesKey
import com.intellij.openapi.util.TextRange
import com.intellij.psi.PsiElement
import com.intellij.psi.tree.TokenSet
import org.pcsoft.intellij.plugin.inno_setup.language.*
import org.pcsoft.intellij.plugin.inno_setup.language.extractDefineValueFromLine
import org.pcsoft.intellij.plugin.inno_setup.language.parsing.psi.*
import org.pcsoft.intellij.plugin.inno_setup.services.IssConstantService
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.*

class IssAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        val spec = service<IssSpecService>().spec
        when (element) {
            is IssFile                  -> annotateFile(element, holder, spec)
            is IssSectionName           -> annotateSectionName(element, holder, spec)
            is IssSection               -> annotateSection(element, holder, spec)
            is IssParameterEntry        -> annotateParameterEntry(element, holder, spec)
            is IssParamKey              -> annotateParamKey(element, holder, spec)
            is IssDirectiveKey          -> annotateDirectiveKey(element, holder, spec)
            is IssParamValue            -> annotateParamValue(element, holder, spec)
            is IssConstant              -> annotateConstant(element, holder)
            is IssPreprocessorDirective -> annotatePreprocessorDirective(element, holder)
        }
    }

    private fun annotateFile(file: IssFile, holder: AnnotationHolder, spec: InnoSetupSpec) {
        val required = spec.sections.filter { it.required }.map { it.name.lowercase() }.toSet()
        val existing = file.sections().map { it.nameText().lowercase() }.toSet()
        val missing  = required - existing
        if (missing.isNotEmpty()) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Required section(s) missing: " + missing.joinToString(", ") { "[$it]" }
            ).fileLevel().create()
        }

        val sections = file.sections()
        val codeIdx  = sections.indexOfFirst { it.nameText().equals("Code", ignoreCase = true) }
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

    private fun annotateSectionName(name: IssSectionName, holder: AnnotationHolder, spec: InnoSetupSpec) {
        if (name.isInCodeSection()) return
        val section     = name.parent?.parent as? IssSection ?: return
        val specSection = section.specSection(spec)
        if (specSection == null) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unknown section: '${name.text}'")
                .range(name.textRange)
                .textAttributes(IssAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        } else {
            highlight(name.textRange, IssAnnotatorHighlighting.SECTION_NAME, holder)
        }
    }

    private fun annotateSection(section: IssSection, holder: AnnotationHolder, spec: InnoSetupSpec) {
        val specSection = section.specSection(spec) ?: return
        if (specSection.type != "directive") return

        val required = specSection.attributes.filter { it.required }.map { it.name.lowercase() }.toSet()
        val present  = section.directiveEntryList.map { it.keyText().lowercase() }.toSet()
        val missing  = required - present
        if (missing.isNotEmpty()) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Required directive(s) missing: " + missing.joinToString(", ")
            ).range(section.sectionHeader.sectionName?.textRange ?: section.sectionHeader.textRange).create()
        }
    }

    private fun annotateParameterEntry(entry: IssParameterEntry, holder: AnnotationHolder, spec: InnoSetupSpec) {
        if (entry.isInCodeSection()) return
        val section     = entry.containingSection() ?: return
        val specSection = section.specSection(spec) ?: return
        if (specSection.type != "parameter") return

        val required = specSection.attributes.filter { it.required }.map { it.name.lowercase() }.toSet()
        val present  = entry.paramPairList.map { it.keyText().lowercase() }.toSet()
        val missing  = required - present
        if (missing.isNotEmpty()) {
            val end = entry.node.lastChildNode
                ?.takeIf { it.elementType == IssTypes.CRLF }
                ?.startOffset ?: entry.textRange.endOffset
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Required parameter(s) missing: " + missing.joinToString(", ")
            ).range(TextRange(entry.textRange.startOffset, end)).create()
        }
    }

    private fun annotateParamKey(key: IssParamKey, holder: AnnotationHolder, spec: InnoSetupSpec) {
        if (key.isInCodeSection()) return
        val pair        = key.parent as? IssParamPair ?: return
        val section     = pair.containingSection() ?: return
        val specSection = section.specSection(spec) ?: return
        val attr = specSection.attributes.firstOrNull { it.name.equals(pair.keyText(), ignoreCase = true) }
        annotateKey(key.textRange, attr, holder)
    }

    private fun annotateDirectiveKey(key: IssDirectiveKey, holder: AnnotationHolder, spec: InnoSetupSpec) {
        if (key.isInCodeSection()) return
        val entry       = key.parent as? IssDirectiveEntry ?: return
        val section     = entry.containingSection() ?: return
        val specSection = section.specSection(spec) ?: return
        val attr = specSection.attributes.firstOrNull { it.name.equals(entry.keyText(), ignoreCase = true) }
        annotateKey(key.textRange, attr, holder)
    }

    private fun annotateKey(range: TextRange, attr: IssAttributeSpec?, holder: AnnotationHolder) {
        when {
            attr == null ->
                holder.newAnnotation(HighlightSeverity.ERROR, "Unknown parameter: '${range}'")
                    .range(range)
                    .textAttributes(IssAnnotatorHighlighting.UNKNOWN_REFERENCE)
                    .create()
            attr.deprecated ->
                holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(range)
                    .textAttributes(IssAnnotatorHighlighting.DEPRECATED)
                    .create()
            else ->
                highlight(range, IssAnnotatorHighlighting.PARAM_KEY, holder)
        }
    }

    private fun annotateParamValue(value: IssParamValue, holder: AnnotationHolder, spec: InnoSetupSpec) {
        if (value.isInCodeSection()) return
        val attr = resolveAttr(value, spec) ?: return
        when (val type = attr.type) {
            is IssFlagTypeSpec    -> annotateFlagValue(value, type, holder)
            is IssNativeTypeSpec  -> annotateNativeValue(value, type, holder)
            is IssReferenceTypeSpec -> {
                val pair = value.containingParamPair()
                if (pair?.isReferenceParam() == true) {
                    value.identifiers().forEach {
                        highlight(it.textRange, IssAnnotatorHighlighting.REFERENCE, holder)
                    }
                }
            }
        }
    }

    private fun resolveAttr(value: IssParamValue, spec: InnoSetupSpec): IssAttributeSpec? {
        val pair = value.containingParamPair()
        if (pair != null) {
            val ss = pair.containingSection()?.specSection(spec) ?: return null
            return ss.attributes.firstOrNull { it.name.equals(pair.keyText(), ignoreCase = true) }
        }
        val dir = value.containingDirectiveEntry() ?: return null
        val ss  = dir.containingSection()?.specSection(spec) ?: return null
        return ss.attributes.firstOrNull { it.name.equals(dir.keyText(), ignoreCase = true) }
    }

    private fun annotateFlagValue(value: IssParamValue, flagType: IssFlagTypeSpec, holder: AnnotationHolder) {
        val flagMap    = flagType.flags.associateBy { it.name.lowercase() }
        val tokenNodes = value.node
            .getChildren(TokenSet.create(IssTypes.IDENTIFIER))
            .associateBy { it.text.lowercase() }

        tokenNodes.forEach { (name, node) ->
            val def = flagMap[name]
            when {
                def == null ->
                    holder.newAnnotation(HighlightSeverity.ERROR, "Unknown flag: '${node.text}'")
                        .range(node.textRange)
                        .textAttributes(IssAnnotatorHighlighting.UNKNOWN_REFERENCE)
                        .create()
                def.deprecated ->
                    holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                        .range(node.textRange)
                        .textAttributes(IssAnnotatorHighlighting.DEPRECATED)
                        .create()
                else ->
                    highlight(node.textRange, IssAnnotatorHighlighting.FLAG, holder)
            }
        }

        val seen = mutableSetOf<Pair<String, String>>()
        tokenNodes.forEach { (name, node) ->
            val def = flagMap[name] ?: return@forEach
            def.conflicts.forEach conflict@{ conflict ->
                val other = tokenNodes[conflict.flag.lowercase()] ?: return@conflict
                val key   = if (name < conflict.flag) name to conflict.flag else conflict.flag to name
                if (!seen.add(key)) return@conflict

                val severity = if (conflict.severity == IssFlagSeveritySpec.ERROR)
                    HighlightSeverity.ERROR else HighlightSeverity.WARNING
                val msg = "Conflicting flags: '${node.text}' and '${other.text}'"
                holder.newAnnotation(severity, msg).range(node.textRange).create()
                holder.newAnnotation(severity, msg).range(other.textRange).create()
            }
        }
    }

    private fun annotateNativeValue(value: IssParamValue, type: IssNativeTypeSpec, holder: AnnotationHolder) {
        val text = value.singleText()
        val valid = when (type.dataType.lowercase()) {
            "integer" -> text.matches(Regex("-?[0-9]+"))
            "boolean" -> text.lowercase() in setOf("yes", "no")
            else      -> true
        }
        if (!valid) {
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Expected type '${type.dataType}', got: '$text'"
            ).range(value.textRange).create()
        }
    }

    private fun annotateConstant(constant: IssConstant, holder: AnnotationHolder) {
        if (constant.isInCodeSection()) return
        val body = constant.constantBody
        val name = body.text.substringBefore(':').substringBefore('|').trim().trimStart('#')

        val builtins  = service<IssConstantService>().spec.constants
        val isppNames = constant.issFile()?.definedConstants()?.map { it.first } ?: emptyList()
        val isIspp    = body.text.trimStart().startsWith("#")

        val known = when {
            isIspp  -> name in isppNames
            else    -> builtins.any { it.name.equals(name, ignoreCase = true) }
        }

        if (!known) {
            holder.newAnnotation(HighlightSeverity.ERROR, "Unknown constant: '{${body.text}}'")
                .range(constant.textRange)
                .textAttributes(IssAnnotatorHighlighting.UNKNOWN_REFERENCE)
                .create()
        } else if (isIspp) {
            // Color "#Name" of {#Name} blue; the name additionally italic. Braces stay neutral.
            body.node.findChildByType(IssTypes.HASH)?.let {
                highlight(it.textRange, IssAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)
            }
            body.node.findChildByType(IssTypes.IDENTIFIER)?.let {
                highlight(it.textRange, IssAnnotatorHighlighting.ISPP_REFERENCE_NAME, holder)
            }
        } else {
            highlight(constant.textRange, IssAnnotatorHighlighting.REFERENCE, holder)
        }
    }

    private fun annotatePreprocessorDirective(directive: IssPreprocessorDirective, holder: AnnotationHolder) {
        val hash    = directive.node.findChildByType(IssTypes.HASH) ?: return
        val keyword = directive.node.findChildByType(IssTypes.IDENTIFIER) ?: return
        val keywordRange = TextRange(hash.startOffset, keyword.textRange.endOffset)
        highlight(keywordRange, IssAnnotatorHighlighting.PREPROCESSOR_DIRECTIVE, holder)

        val ex = directive as? IssPreprocessorDirectiveEx ?: return

        // For a #define: the type qualifier gets keyword styling, the name is always italic.
        if (ex.isDefine()) {
            ex.getDefineTypeIdentifier()?.let {
                highlight(it.textRange, IssAnnotatorHighlighting.DEFINE_TYPE, holder)
            }
            ex.getNameIdentifier()?.let {
                highlight(it.textRange, IssAnnotatorHighlighting.DEFINE_NAME, holder)
            }
        }

        // Validate the value of typed defines: #define int/str/float Name Value
        // The ISS lexer in YYINITIAL does not produce NUMBER or QUOTE tokens, so we read
        // the value from the raw source line via extractDefineValueFromLine.
        val typeName = ex.getDefineTypeName() ?: return
        val name     = ex.getDefineName()     ?: return

        val fileText = directive.containingFile.text
        val rawVal   = extractDefineValueFromLine(fileText, directive.textOffset, typeName, name)
            ?: return  // no value — nothing to validate

        val valid = when (typeName.lowercase()) {
            "int", "integer" -> rawVal.matches(Regex("-?[0-9]+"))
            "float", "double" -> rawVal.matches(Regex("-?[0-9]*\\.?[0-9]+([eE][+-]?[0-9]+)?"))
            "str", "string", "any" -> true
            else -> true
        }
        if (!valid) {
            // Locate the value text in the source line so the squiggle is precise.
            val lineStart = fileText.lastIndexOf('\n', directive.textOffset).let { if (it < 0) 0 else it + 1 }
            val valOffset = fileText.indexOf(rawVal, directive.textOffset)
            val errorRange = if (valOffset in lineStart until directive.textRange.endOffset)
                TextRange(valOffset, valOffset + rawVal.length)
            else
                directive.textRange
            holder.newAnnotation(
                HighlightSeverity.ERROR,
                "Type '$typeName' requires a ${typeDescription(typeName)} value, got: '$rawVal'"
            ).range(errorRange).create()
        }
    }

    private fun typeDescription(typeName: String) = when (typeName.lowercase()) {
        "int", "integer"   -> "integer"
        "float", "double"  -> "numeric"
        else               -> typeName
    }

    private fun highlight(range: TextRange, key: TextAttributesKey, holder: AnnotationHolder) =
        holder.newSilentAnnotation(HighlightSeverity.INFORMATION)
            .range(range).textAttributes(key).create()
}
