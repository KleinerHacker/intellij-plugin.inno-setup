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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.preprocessor

import com.intellij.lang.documentation.AbstractDocumentationProvider
import com.intellij.lang.documentation.DocumentationMarkup
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.components.service
import com.intellij.openapi.editor.Editor
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.PsiManager
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.IsDocLookupStub
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.specTarget
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.computeValue
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.inferParameterTypes
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.inferType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.precedingDefine
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirectiveEx
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorTypes
import org.pcsoft.intellij.plugin.inno_setup.services.IsPreprocessorService
import org.pcsoft.intellij.plugin.inno_setup.types.IsPreprocessorSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsSectionSpecTarget
import org.pcsoft.intellij.plugin.inno_setup.types.appliesTo

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
 * Quick-documentation for the ISPP injection: the directive keyword (e.g. `#define`, `#include`) and
 * predefined ISPP variables (e.g. `__LINE__`, `PREPROCVER`) when used inside a directive expression.
 *
 * Backed by the bundled ISPP spec ([IsPreprocessorService]); shows description, syntax/type, the
 * `deprecated` marker and the `since`/`until` version range.
 */
class IsPreprocessorDocumentationProvider : AbstractDocumentationProvider() {

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun getCustomDocumentationElement(
        editor: Editor, file: PsiFile,
        contextElement: PsiElement?, targetOffset: Int
    ): PsiElement? {
        val el = contextElement ?: return null
        if (el.node?.elementType != IsPreprocessorTypes.IDENTIFIER) return null
        return el
    }

    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun generateDoc(element: PsiElement, originalElement: PsiElement?): String? {
        if (element is IsDocLookupStub) return element.docHtml
        if (element.node?.elementType != IsPreprocessorTypes.IDENTIFIER) return null
        val spec = service<IsPreprocessorService>().spec

        // The directive keyword is a direct child of the directive (`#<keyword>`); its parent is the
        // directive itself, whereas value identifiers sit under the `value` node.
        val directive = element.parent as? IsPreprocessorDirective
        if (directive != null && directive.identifier?.textRange == element.textRange) {
            return generateDirectiveDoc(element, element.text, spec)
        }

        // Otherwise: a predefined ISPP variable, a built-in function, or a referenced own `#define`.
        return generateVariableDoc(element.text, spec)
            ?: generateFunctionDoc(element.text, spec)
            ?: resolveUserDefine(element)?.let { generateUserDefineDoc(it) }
    }

    /**
     * Quick-documentation for the highlighted completion entry. The lookup objects are plain strings
     * (the directive keyword without `#`, or a predefined ISPP variable name); predefined variables are
     * resolved first, then directive keywords. User-defined `#define` names carry no spec doc and yield
     * no popup. The rendered HTML is wrapped in an [IsDocLookupStub] handed back to [generateDoc].
     */
    override fun getDocumentationElementForLookupItem(
        psiManager: PsiManager, obj: Any?, element: PsiElement?
    ): PsiElement? {
        val name = obj as? String ?: return null
        val ctx = element ?: return null
        val spec = service<IsPreprocessorService>().spec
        val html = generateVariableDoc(name, spec)
            ?: generateFunctionDoc(name, spec)
            ?: precedingDefineFor(ctx, name)?.let { generateUserDefineDoc(it) }
            ?: generateDirectiveDoc(ctx, name, spec)
            ?: return null
        return IsDocLookupStub(ctx, html)
    }

    /**
     * The own `#define` an IDENTIFIER refers to: either the directive whose name it *is* (definition site), or
     * the nearest preceding `#define` of that name in the host file (a reference). `null` when it is neither.
     */
    private fun resolveUserDefine(element: PsiElement): IsPreprocessorDirectiveEx? {
        val enclosing = PsiTreeUtil.getParentOfType(element, IsPreprocessorDirective::class.java)
                as? IsPreprocessorDirectiveEx
        if (enclosing != null && enclosing.isDefine() &&
            enclosing.nameIdentifier?.textRange == element.textRange
        ) {
            return enclosing
        }
        return precedingDefineFor(element, element.text)
    }

    /** The nearest `#define` named [name] declared before [element]'s line in the host ISS file. */
    private fun precedingDefineFor(element: PsiElement, name: String): IsPreprocessorDirectiveEx? {
        val injMgr = InjectedLanguageManager.getInstance(element.project)
        val host = injMgr.getTopLevelFile(element.containingFile) as? IsScriptFile ?: return null
        val hostLine = injMgr.getInjectionHost(element) ?: return null
        return host.precedingDefine(name, hostLine.textRange.startOffset)
    }

    /**
     * Quick-doc for a user `#define`: its inferred type and, for a simple macro, its computed value; for a
     * function-like macro the (probable) parameter types and return type. Unknown types/values render as `any`
     * resp. a "not computable" note.
     */
    private fun generateUserDefineDoc(define: IsPreprocessorDirectiveEx): String? {
        val name = define.getDefineName()?.ifEmpty { null } ?: return null
        return buildString {
            append(DocumentationMarkup.DEFINITION_START)
            if (define.isFunctionMacro()) {
                val params = define.inferParameterTypes().joinToString(", ") { "${it.first}: ${it.second.name.lowercase()}" }
                append("<b>$name</b>($params) · macro · ${define.inferType().name.lowercase()}")
            } else {
                append("<b>$name</b> · define · ${define.inferType().name.lowercase()}")
            }
            append(DocumentationMarkup.DEFINITION_END)
            if (!define.isFunctionMacro()) {
                append(DocumentationMarkup.CONTENT_START)
                val value = define.computeValue()?.display() ?: "<i>not computable</i>"
                append("<p><b>Value:</b> <code>$value</code></p>")
                append(DocumentationMarkup.CONTENT_END)
            }
        }
    }

    private fun generateDirectiveDoc(element: PsiElement, keyword: String, spec: IsPreprocessorSpec): String? {
        val directive = spec.directives.firstOrNull { it.name.equals(keyword, ignoreCase = true) }
            ?: return null

        return buildString {
            append(DocumentationMarkup.DEFINITION_START)
            append("<b>#${directive.name}</b> · directive")
            if (directive.deprecated.appliesTo(hostTarget(element))) append(" · <s>deprecated</s>")
            append(DocumentationMarkup.DEFINITION_END)
            append(DocumentationMarkup.CONTENT_START)
            append("<p>${directive.description}</p>")
            append("<p><b>Syntax:</b> <code>${directive.syntax}</code></p>")
            appendVersionSection(directive.since, directive.until)
            append(DocumentationMarkup.CONTENT_END)
        }
    }

    private fun generateVariableDoc(name: String, spec: IsPreprocessorSpec): String? {
        val variable = spec.predefinedVariables.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: return null

        return buildString {
            append(DocumentationMarkup.DEFINITION_START)
            append("<b>${variable.name}</b> · ${variable.type.typeName}")
            append(DocumentationMarkup.DEFINITION_END)
            append(DocumentationMarkup.CONTENT_START)
            append("<p>${variable.description}</p>")
            appendVersionSection(variable.since, variable.until)
            append(DocumentationMarkup.CONTENT_END)
        }
    }

    private fun generateFunctionDoc(name: String, spec: IsPreprocessorSpec): String? {
        val function = spec.builtinFunctions.firstOrNull { it.name.equals(name, ignoreCase = true) }
            ?: return null

        return buildString {
            append(DocumentationMarkup.DEFINITION_START)
            append("<b>${function.name}</b> · function · ${function.returnType.typeName}")
            append(DocumentationMarkup.DEFINITION_END)
            append(DocumentationMarkup.CONTENT_START)
            append("<p>${function.description}</p>")
            append("<p><b>Signature:</b> <code>${function.signature}</code></p>")
            appendVersionSection(function.since, function.until)
            append(DocumentationMarkup.CONTENT_END)
        }
    }

    /** Spec target of the ISS/ISL host file the injected ISPP fragment lives in (defaults to ISS). */
    private fun hostTarget(element: PsiElement): IsSectionSpecTarget =
        runCatching {
            InjectedLanguageManager.getInstance(element.project)
                .getTopLevelFile(element.containingFile)?.specTarget
        }.getOrNull() ?: IsSectionSpecTarget.ISS
}
