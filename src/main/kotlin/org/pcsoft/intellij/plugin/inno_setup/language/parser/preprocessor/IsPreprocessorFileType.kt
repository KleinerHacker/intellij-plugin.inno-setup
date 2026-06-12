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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 * File type for the injected ISPP preprocessor language.
 *
 * **Injection-only — intentionally NOT registered as a `<fileType>` in `plugin.xml`.** ISPP never
 * exists as a standalone file on disk: it is injected into the `#…` lines of ISS/ISL scripts (see
 * [IsPreprocessorInjector]). This type exists solely so that the injected [IsPreprocessorFile]
 * (a `PsiFileBase`) can satisfy the non-null `PsiFile.getFileType()` contract. The
 * [getDefaultExtension] value (`ispp`) is nominal and never used for file association.
 */
class IsPreprocessorFileType private constructor() : LanguageFileType(IsPreprocessorLanguage) {
    companion object {
        @JvmField
        val INSTANCE = IsPreprocessorFileType()
    }

    override fun getName(): String = "Inno Setup Preprocessor"
    override fun getDescription(): String = "Inno Setup Preprocessor fragment"
    override fun getDefaultExtension(): String = "ispp"
    override fun getIcon(): Icon? = null
}
