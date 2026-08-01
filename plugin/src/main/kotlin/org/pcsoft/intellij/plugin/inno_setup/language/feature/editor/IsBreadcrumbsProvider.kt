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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor

import com.intellij.lang.Language
import com.intellij.psi.PsiElement
import com.intellij.ui.breadcrumbs.BreadcrumbsProvider
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptLanguage
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionBlock
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionDirectiveEntry
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.psi.IsSectionParameterEntry
import org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.sectionAtOffset
import javax.swing.Icon

/**
 * Provides the breadcrumbs of an Inno Setup script (`file.iss › [Files] › Source`) and — through
 * [acceptStickyElement] — the PSI-based part of the editor's sticky lines.
 *
 * Sticky lines are collected by the platform from the breadcrumbs infrastructure, but through a separate
 * accept method, so both features can differ: only section blocks are sticky (entries are single-line and
 * would be useless as a sticky line), while the breadcrumbs bar also shows the entry below the section.
 * The preprocessor blocks (`#if`/`#sub`) have no spanning PSI element, so they can be neither breadcrumbs nor
 * sticky lines — the plain-range alternative is `@ApiStatus.Internal` (see [IsFileBreadcrumbsCollector]).
 */
class IsBreadcrumbsProvider : BreadcrumbsProvider {

    override fun getLanguages(): Array<Language> = arrayOf(IsScriptLanguage)

    override fun acceptElement(element: PsiElement): Boolean =
        element is IsSectionBlock || element is IsSectionDirectiveEntry || element is IsSectionParameterEntry

    override fun acceptStickyElement(element: PsiElement): Boolean = element is IsSectionBlock

    override fun getElementInfo(element: PsiElement): String = IsElementPresentation.textOf(element).orEmpty()

    override fun getElementIcon(element: PsiElement): Icon? = IsElementPresentation.iconOf(element)

    /**
     * The enclosing section of a top-level element — resolved by offset, not by PSI nesting.
     *
     * A preprocessor line ends the section block in the grammar (`entry` does not include `preprocessorLine`),
     * so every entry below a `#if` — and the `#…` line itself — is a *sibling* of its section instead of a
     * child. Without this the breadcrumbs of such a line would lack the section (leaving the bar empty, which
     * makes it fall back to the injected file's name), and the section would stop sticking as soon as the
     * caret enters a `#if` block.
     */
    override fun getParent(element: PsiElement): PsiElement? {
        val parent = element.parent
        if (parent is IsSectionBlock || parent !is IsScriptFile) return parent

        val section = parent.sectionAtOffset(element.textRange.startOffset)
        return if (section != null && section !== element) section else parent
    }
}
