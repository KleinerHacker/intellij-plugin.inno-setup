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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.template

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.openapi.fileTypes.FileType
import com.intellij.psi.FileViewProvider
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorHost

/**
 * PSI file for `.ist` (Inno Setup Template) files. A template is an ISPP host (so `#…` lines are recognised),
 * but it has no section constants or effective-script analysis, so it uses the [IsPreprocessorHost] defaults.
 */
class IsTemplateFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, IsTemplateLanguage), IsPreprocessorHost {
    override fun getFileType(): FileType = IsTemplateFileType.INSTANCE

    override fun toString(): String = "Inno Setup Template File"
}
