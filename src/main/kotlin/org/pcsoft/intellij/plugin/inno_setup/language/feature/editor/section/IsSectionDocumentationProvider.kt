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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.specTarget
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.containingParamPair
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.containingSection
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.*
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.specSection
import org.pcsoft.intellij.plugin.inno_setup.services.IsConstantService
import org.pcsoft.intellij.plugin.inno_setup.services.IsSpecService
import org.pcsoft.intellij.plugin.inno_setup.types.*

private fun StringBuilder.appendVersionSection(since: String?, until: String?) {
    if (since == null && until == null) return
    append(DocumentationMarkup.SECTIONS_START)
    append(DocumentationMarkup.SECTION_HEADER_START)
    append("Version")
    append(DocumentationMarkup.SECTION_SEPARATOR)
    since?.let { append("<p>Available since Inno Setup <b>$it</b></p>") }
    until?.let { append("<p>Removed in Inno Setup <b>$it</b></p>") }
    append(DocumentationMarkup.SECTIONS_END)
}

/**
 * Provides context-aware IntelliJ Platform behavior for Inno Setup PSI elements.
 */
class IsSectionDocumentationProvider : AbstractDocumentationProvider() {

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getCustomDocumentationElement(
        editor: Editor, file: PsiFile,
        contextElement: PsiElement?, targetOffset: Int
    ): PsiElement? {
        val el = contextElement ?: return null
        if (el.node?.elementType != IsSectionTypes.IDENTIFIER) return null

        return when (val parent = el.parent) {
            is IsSectionTitle -> parent
            is IsSectionDirectiveKey -> parent
            is IsSectionParamKey -> parent
            is IsSectionConstantBody -> parent.parent          // → IsSectionConstant
            is IsSectionParamValue -> {
                val pair = parent.containingParamPair ?: return null
                if (pair.keyText().equals("Flags", ignoreCase = true)) el else null
            }

            else -> null
        }
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        val spec = service<IsSpecService>().spec

        return when (element) {
            is IsSectionTitle -> generateSectionDoc(element, spec)
            is IsSectionParamKey -> generateAttrDoc(element.containingSection, element.text, spec)
            is IsSectionDirectiveKey -> generateAttrDoc(element.containingSection, element.text, spec)
            is IsSectionConstant -> generateConstantDoc(element)
            else -> {
                if (element.node?.elementType == IsSectionTypes.IDENTIFIER) {
                    val pair = (element.parent as? IsSectionParamValue)?.containingParamPair
                    if (pair?.keyText()?.equals("Flags", ignoreCase = true) == true)
                        return generateFlagDoc(element.text, pair, spec)
                }
                null
            }
        }
    }

    private fun generateSectionDoc(name: IsSectionTitle, spec: IsSectionSpec): String? {
        val sec: IsSectionDefSpec = spec.sections.firstOrNull { it.name.equals(name.text, ignoreCase = true) }
            ?: return null

        return buildString {
            append(DocumentationMarkup.DEFINITION_START)
            val target = name.specTarget
            append("<b>[${sec.name}]</b> · ${sec.type.typeName}")
            if (sec.required.appliesTo(target)) append(" · <b>required</b>")
            if (sec.deprecated.appliesTo(target)) append(" · <s>deprecated</s>")
            append(DocumentationMarkup.DEFINITION_END)
            append(DocumentationMarkup.CONTENT_START)
            append("<p>${sec.description}</p>")
            appendVersionSection(sec.since, sec.until)
            append(DocumentationMarkup.CONTENT_END)
        }
    }

    private fun generateAttrDoc(section: IsSectionBlock?, keyText: String, spec: IsSectionSpec): String? {
        val specSec = section?.specSection(spec) ?: return null
        val attr = specSec.attributes.firstOrNull { it.name.equals(keyText, ignoreCase = true) }
            ?: return null
        val typeStr = when (val t = attr.type) {
            is IsSectionNativeTypeSpec -> t.dataType.typeName
            is IsSectionReferenceTypeSpec -> "→ ${t.section}"
            is IsSectionFlagTypeSpec -> "flags"
        }

        return buildString {
            append(DocumentationMarkup.DEFINITION_START)
            val target = section.specTarget
            append("<b>${attr.name}</b> · $typeStr")
            if (attr.required.appliesTo(target)) append(" · <b>required</b>")
            if (attr.array) append("[]")
            if (attr.deprecated.appliesTo(target)) append(" · <s>deprecated</s>")
            append(DocumentationMarkup.DEFINITION_END)
            append(DocumentationMarkup.CONTENT_START)
            append("<p>${attr.description}</p>")
            appendVersionSection(attr.since, attr.until)
            append(DocumentationMarkup.CONTENT_END)
        }
    }

    private fun generateFlagDoc(flagName: String, pair: IsSectionParamPair, spec: IsSectionSpec): String? {
        val specSec = pair.containingSection?.specSection(spec) ?: return null
        val attr = specSec.attributes.firstOrNull { it.name.equals("Flags", ignoreCase = true) }
            ?: return null
        val flagType = attr.type as? IsSectionFlagTypeSpec ?: return null
        val flag = flagType.flags.firstOrNull { it.name.equals(flagName, ignoreCase = true) }
            ?: return null

        return buildString {
            append(DocumentationMarkup.DEFINITION_START)
            append("<b>${flag.name}</b> · flag")
            if (flag.deprecated.appliesTo(pair.specTarget)) append(" · <s>deprecated</s>")
            append(DocumentationMarkup.DEFINITION_END)
            append(DocumentationMarkup.CONTENT_START)
            append("<p>${flag.description}</p>")
            val requires = flag.conflicts.filter { it.type == IsSectionFlagType.REQUIRES }
            val conflicts = flag.conflicts.filter { it.type != IsSectionFlagType.REQUIRES }
            if (conflicts.isNotEmpty()) {
                append(DocumentationMarkup.SECTIONS_START)
                append(DocumentationMarkup.SECTION_HEADER_START)
                append("Conflicts")
                append(DocumentationMarkup.SECTION_SEPARATOR)
                conflicts.forEach { c ->
                    append("<p><code>${c.flag}</code> · ${c.type.name.lowercase()}</p>")
                }
                append(DocumentationMarkup.SECTIONS_END)
            }
            if (requires.isNotEmpty()) {
                append(DocumentationMarkup.SECTIONS_START)
                append(DocumentationMarkup.SECTION_HEADER_START)
                append("Requires")
                append(DocumentationMarkup.SECTION_SEPARATOR)
                requires.forEach { c ->
                    append("<p><code>${c.flag}</code></p>")
                }
                append(DocumentationMarkup.SECTIONS_END)
            }
            appendVersionSection(flag.since, flag.until)
            append(DocumentationMarkup.CONTENT_END)
        }
    }

    private fun generateConstantDoc(constant: IsSectionConstant): String? {
        val body = constant.constantBody.text
            ?.substringBefore(':')?.substringBefore('|')?.trim()?.trimStart('#')
            ?: return null
        val builtins = service<IsConstantService>().spec.constants
        val const = builtins.firstOrNull { it.name.equals(body, ignoreCase = true) } ?: return null

        return buildString {
            append(DocumentationMarkup.DEFINITION_START)
            append("<b>{${const.name}}</b> · ${const.type.name.lowercase().replace('_', ' ')}")
            if (const.deprecated.appliesTo(constant.specTarget)) append(" · <s>deprecated</s>")
            append(DocumentationMarkup.DEFINITION_END)
            append(DocumentationMarkup.CONTENT_START)
            append("<p>${const.description}</p>")
            if (const.syntax != null) {
                append("<p><b>Syntax:</b> <code>{${const.syntax}}</code></p>")
            }
            appendVersionSection(const.since, const.until)
            append(DocumentationMarkup.CONTENT_END)
        }
    }
}
