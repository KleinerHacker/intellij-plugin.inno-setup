package org.pcsoft.intellij.plugin.inno_setup.language.isi.navigation

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.language.isi.containingParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.isi.containingSection
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.*
import org.pcsoft.intellij.plugin.inno_setup.language.isi.specSection
import org.pcsoft.intellij.plugin.inno_setup.services.IssConstantService
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.*

class IsiDocumentationProvider : AbstractDocumentationProvider() {

    override fun getCustomDocumentationElement(
        editor: Editor, file: PsiFile,
        contextElement: PsiElement?, targetOffset: Int
    ): PsiElement? {
        val el = contextElement ?: return null
        if (el.node?.elementType != IsiTypes.IDENTIFIER) return null
        return when (val parent = el.parent) {
            is IsiSectionName -> parent
            is IsiDirectiveKey -> parent
            is IsiParamKey -> parent
            is IsiConstantBody -> parent.parent          // → IsiConstant
            is IsiParamValue -> {
                val pair = parent.containingParamPair() ?: return null
                if (pair.keyText().equals("Flags", ignoreCase = true)) el else null
            }

            else -> null
        }
    }

    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        val spec = service<IssSpecService>().spec
        return when (element) {
            is IsiSectionName -> generateSectionDoc(element, spec)
            is IsiParamKey -> generateAttrDoc(element.containingSection(), element.text, spec)
            is IsiDirectiveKey -> generateAttrDoc(element.containingSection(), element.text, spec)
            is IsiConstant -> generateConstantDoc(element)
            else -> {
                if (element.node?.elementType == IsiTypes.IDENTIFIER) {
                    val pair = (element.parent as? IsiParamValue)?.containingParamPair()
                    if (pair?.keyText()?.equals("Flags", ignoreCase = true) == true)
                        return generateFlagDoc(element.text, pair, spec)
                }
                null
            }
        }
    }

    private fun generateSectionDoc(name: IsiSectionName, spec: InnoSetupSpec): String? {
        val sec: IssSectionSpec = spec.sections.firstOrNull { it.name.equals(name.text, ignoreCase = true) }
            ?: return null
        return buildString {
            append(DocumentationMarkup.DEFINITION_START)
            append("<b>[${sec.name}]</b> · ${sec.type}")
            if (sec.required) append(" · <b>required</b>")
            if (sec.deprecated) append(" · <s>deprecated</s>")
            append(DocumentationMarkup.DEFINITION_END)
            append(DocumentationMarkup.CONTENT_START)
            append("<p>${sec.description}</p>")
            append(DocumentationMarkup.CONTENT_END)
        }
    }

    private fun generateAttrDoc(section: IsiSection?, keyText: String, spec: InnoSetupSpec): String? {
        val specSec = section?.specSection(spec) ?: return null
        val attr = specSec.attributes.firstOrNull { it.name.equals(keyText, ignoreCase = true) }
            ?: return null
        val typeStr = when (val t = attr.type) {
            is IssNativeTypeSpec -> t.dataType
            is IssReferenceTypeSpec -> "→ ${t.section}"
            is IssFlagTypeSpec -> "flags"
        }
        return buildString {
            append(DocumentationMarkup.DEFINITION_START)
            append("<b>${attr.name}</b> · $typeStr")
            if (attr.required) append(" · <b>required</b>")
            if (attr.array) append("[]")
            if (attr.deprecated) append(" · <s>deprecated</s>")
            append(DocumentationMarkup.DEFINITION_END)
            append(DocumentationMarkup.CONTENT_START)
            append("<p>${attr.description}</p>")
            append(DocumentationMarkup.CONTENT_END)
        }
    }

    private fun generateFlagDoc(flagName: String, pair: IsiParamPair, spec: InnoSetupSpec): String? {
        val specSec = pair.containingSection()?.specSection(spec) ?: return null
        val attr = specSec.attributes.firstOrNull { it.name.equals("Flags", ignoreCase = true) }
            ?: return null
        val flagType = attr.type as? IssFlagTypeSpec ?: return null
        val flag = flagType.flags.firstOrNull { it.name.equals(flagName, ignoreCase = true) }
            ?: return null
        return buildString {
            append(DocumentationMarkup.DEFINITION_START)
            append("<b>${flag.name}</b> · flag")
            if (flag.deprecated) append(" · <s>deprecated</s>")
            append(DocumentationMarkup.DEFINITION_END)
            append(DocumentationMarkup.CONTENT_START)
            append("<p>${flag.description}</p>")
            if (flag.conflicts.isNotEmpty()) {
                append(DocumentationMarkup.SECTIONS_START)
                append(DocumentationMarkup.SECTION_HEADER_START)
                append("Conflicts")
                append(DocumentationMarkup.SECTION_SEPARATOR)
                flag.conflicts.forEach { c ->
                    append("<p><code>${c.flag}</code> · ${c.severity.name.lowercase()}</p>")
                }
                append(DocumentationMarkup.SECTIONS_END)
            }
            append(DocumentationMarkup.CONTENT_END)
        }
    }

    private fun generateConstantDoc(constant: IsiConstant): String? {
        val body = constant.constantBody.text
            ?.substringBefore(':')?.substringBefore('|')?.trim()?.trimStart('#')
            ?: return null
        val builtins = service<IssConstantService>().spec.constants
        val const = builtins.firstOrNull { it.name.equals(body, ignoreCase = true) } ?: return null
        return buildString {
            append(DocumentationMarkup.DEFINITION_START)
            append("<b>{${const.name}}</b> · ${const.category.name.lowercase().replace('_', ' ')}")
            if (const.deprecated) append(" · <s>deprecated</s>")
            append(DocumentationMarkup.DEFINITION_END)
            append(DocumentationMarkup.CONTENT_START)
            append("<p>${const.description}</p>")
            if (const.syntax != null) {
                append("<p><b>Syntax:</b> <code>{${const.syntax}}</code></p>")
            }
            append(DocumentationMarkup.CONTENT_END)
        }
    }
}
