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

package org.pcsoft.intellij.plugin.inno_setup.language.ispp

import com.intellij.openapi.fileTypes.LanguageFileType
import javax.swing.Icon

class IsppFileType private constructor() : LanguageFileType(IsppLanguage) {
    companion object {
        @JvmField
        val INSTANCE = IsppFileType()
    }

    override fun getName(): String = "Inno Setup Preprocessor"
    override fun getDescription(): String = "Inno Setup Preprocessor fragment"
    override fun getDefaultExtension(): String = "ispp"
    override fun getIcon(): Icon? = null
}
