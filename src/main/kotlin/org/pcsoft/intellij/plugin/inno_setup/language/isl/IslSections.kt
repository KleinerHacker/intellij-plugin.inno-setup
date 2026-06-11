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

package org.pcsoft.intellij.plugin.inno_setup.language.isl

import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile
import org.pcsoft.intellij.plugin.inno_setup.types.IsiSectionSpec
import org.pcsoft.intellij.plugin.inno_setup.types.IsiSpecTarget

/**
 * Central ISL section policy. The set of sections permitted in `.isl` files is data-driven via the
 * `languageFile` flag in the spec YAML (see `spec/isi-spec.yaml`), so both the annotator and the
 * section-name completion can share the exact same rule.
 */
val IsiSectionSpec.allowedInLanguageFile: Boolean
    get() = languageFile

/** True when [element] belongs to an Inno Setup language file (`.isl`). */
val PsiElement.isInLanguageFile: Boolean
    get() = containingFile is IslFile

/** The spec target ([IsiSpecTarget.ISL] for `.isl`, otherwise [IsiSpecTarget.ISS]) of this file. */
val PsiFile.specTarget: IsiSpecTarget
    get() = when (this) {
        is IslFile -> IsiSpecTarget.ISL
        is IssFile -> IsiSpecTarget.ISS
        else -> throw IllegalStateException()
    }

/** The spec target of the file containing this element. */
val PsiElement.specTarget: IsiSpecTarget
    get() = containingFile.specTarget
