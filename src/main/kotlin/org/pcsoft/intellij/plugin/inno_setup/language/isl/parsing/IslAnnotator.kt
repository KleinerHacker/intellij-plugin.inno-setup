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

package org.pcsoft.intellij.plugin.inno_setup.language.isl.parsing

import com.intellij.lang.annotation.AnnotationHolder
import com.intellij.lang.annotation.Annotator
import com.intellij.lang.annotation.HighlightSeverity
import com.intellij.openapi.components.service
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.IsiAnnotatorHighlighting
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiSection
import org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.psi.IsiSectionName
import org.pcsoft.intellij.plugin.inno_setup.language.isi.specSection
import org.pcsoft.intellij.plugin.inno_setup.language.isl.allowedInLanguageFile
import org.pcsoft.intellij.plugin.inno_setup.language.isl.isInLanguageFile
import org.pcsoft.intellij.plugin.inno_setup.services.IssSpecService

/**
 * ISL-specific validation, additive to [org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing.IsiAnnotator].
 * Acts only inside `.isl` files and flags any section that is not permitted in an Inno Setup
 * language file. Unknown (non-spec) sections are left to the ISI annotator's "Unknown section" check.
 */
class IslAnnotator : Annotator {

    override fun annotate(element: PsiElement, holder: AnnotationHolder) {
        if (!element.isInLanguageFile) return
        if (element !is IsiSectionName) return

        val section = element.parent?.parent as? IsiSection ?: return
        val spec = service<IssSpecService>().spec
        val specSection = section.specSection(spec) ?: return // unknown section → handled by IsiAnnotator
        if (specSection.allowedInLanguageFile) return

        holder.newAnnotation(
            HighlightSeverity.ERROR,
            "Section '[${element.text}]' is not allowed in Inno Setup language (.isl) files"
        )
            .range(element.textRange)
            .textAttributes(IsiAnnotatorHighlighting.UNKNOWN_REFERENCE)
            .create()
    }
}
