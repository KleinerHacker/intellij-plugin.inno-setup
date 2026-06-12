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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi

import com.intellij.psi.PsiNameIdentifierOwner

/**
 * Declares additional behavior mixed into generated PSI interfaces.
 */
interface IsPreprocessorDirectiveEx : PsiNameIdentifierOwner {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    fun isDefine(): Boolean
    /**
     * Returns or performs the public behavior represented by this member.
     */
    fun getDefineName(): String?
    /**
     * Returns or performs the public behavior represented by this member.
     */
    fun getDefineValue(): String?

    /** A function-like macro: the name is immediately followed by `(` (no whitespace). */
    fun isFunctionMacro(): Boolean

    /** For function-like macros: the expression after the `(…)` parameter list, or `null` if absent. */
    fun getMacroBody(): String?
}
