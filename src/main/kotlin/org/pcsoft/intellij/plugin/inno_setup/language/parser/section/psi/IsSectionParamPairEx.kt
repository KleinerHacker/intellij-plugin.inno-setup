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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.section.psi

import com.intellij.psi.PsiElement

/**
 * Declares additional behavior mixed into generated PSI interfaces.
 */
interface IsSectionParamPairEx : PsiElement {
    /**
     * Returns the normalized key text represented by this PSI element.
     */
    fun keyText(): String
    /**
     * Returns whether this PSI element declares a named entry.
     */
    fun isNameDeclaration(): Boolean
    /**
     * Returns whether this PSI element value references another section entry.
     */
    fun isReferenceParam(): Boolean
}
