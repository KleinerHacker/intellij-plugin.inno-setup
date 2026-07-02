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

package org.pcsoft.intellij.plugin.inno_setup.preprocessor.language.parser

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

/**
 * File type for the injected ISPP preprocessor language.
 *
 * This type is injection-only and intentionally not registered as a `<fileType>` in `plugin.xml`.
 * ISPP never exists as a standalone file on disk; it is injected into preprocessor lines of ISS and
 * ISL scripts. The type exists solely so that [IsPreprocessorFile] can satisfy the non-null
 * `PsiFile.getFileType()` contract. The [getDefaultExtension] value is nominal and never used for
 * file association.
 */
class IsPreprocessorFileType private constructor() : LanguageFileType(IsPreprocessorLanguage) {
    companion object {
        /**
         * Singleton instance used by injected ISPP PSI files.
         */
        @JvmField
        val INSTANCE = IsPreprocessorFileType()
    }

    /**
     * Returns the file type name shown by IntelliJ.
     */
    override fun getName(): String = "Inno Setup Preprocessor"

    /**
     * Returns the file type description shown by IntelliJ.
     */
    override fun getDescription(): String = "Inno Setup Preprocessor fragment"

    /**
     * Returns the nominal extension for injected ISPP fragments.
     */
    override fun getDefaultExtension(): String = "ispp"

    /**
     * Returns no icon because injected ISPP fragments are not standalone files.
     */
    override fun getIcon(): Icon? = null
}
