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

import com.intellij.extapi.psi.PsiFileBase
import com.intellij.psi.FileViewProvider

/**
 * PSI file root of an injected ISPP fragment.
 *
 * The file is created by the ISPP parser definition when `IsPreprocessorInjector` injects the
 * language into an `IsSectionPreprocessorLine` host. There are no standalone `.ispp` files; see
 * [IsPreprocessorFileType] for why this injection-only language still needs a file type.
 */
class IsPreprocessorFile(viewProvider: FileViewProvider) : PsiFileBase(viewProvider, IsPreprocessorLanguage) {
    /**
     * Returns the synthetic file type used by injected ISPP fragments.
     */
    override fun getFileType() = IsPreprocessorFileType.INSTANCE
}
