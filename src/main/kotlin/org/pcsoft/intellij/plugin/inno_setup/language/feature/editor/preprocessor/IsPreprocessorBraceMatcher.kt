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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.preprocessor

import com.intellij.lang.BracePair
import com.intellij.lang.PairedBraceMatcher
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.IElementType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorTypes

/**
 * Defines brace pairs and brace matching behavior for the Inno Setup preprocessor (ISPP) directives —
 * notably the parentheses `()` used in expressions and function-like macros.
 */
class IsPreprocessorBraceMatcher : PairedBraceMatcher {

    /**
     * Returns brace-matching metadata for the supplied editor context.
     */
    override fun getPairs(): Array<BracePair> = PAIRS

    /**
     * Returns brace-matching metadata for the supplied editor context.
     */
    override fun isPairedBracesAllowedBeforeType(lbraceType: IElementType, contextType: IElementType?): Boolean = true

    /**
     * Returns brace-matching metadata for the supplied editor context.
     */
    override fun getCodeConstructStart(file: PsiFile, openingBraceOffset: Int): Int = openingBraceOffset

    companion object {
        val PAIRS: Array<BracePair> = arrayOf(
            BracePair(IsPreprocessorTypes.LPAREN, IsPreprocessorTypes.RPAREN, false),
        )
    }
}
