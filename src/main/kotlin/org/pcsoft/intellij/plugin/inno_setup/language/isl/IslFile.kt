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

import com.intellij.psi.FileViewProvider
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile

/**
 * PSI file for Inno Setup language files (`.isl`). Syntactically identical to [IssFile] — it reuses
 * the same ISS language, lexer, parser and PSI — so every `as? IssFile` consumer keeps working.
 * Only the reported file type differs, which lets the ISL-specific tooling (allowed-section
 * restriction in `language/isl/parsing`) distinguish `.isl` from `.iss`.
 */
class IslFile(viewProvider: FileViewProvider) : IssFile(viewProvider) {
    override fun getFileType() = IslFileType.INSTANCE
}
