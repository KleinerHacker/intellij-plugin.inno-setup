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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.find

import com.intellij.lang.refactoring.RefactoringSupportProvider
import com.intellij.psi.PsiElement
import org.pcsoft.intellij.plugin.inno_setup.language.parser.section.parsing.psi.IsSectionParamPairEx

/**
 * Provides context-aware IntelliJ Platform behavior for Inno Setup PSI elements.
 */
class IsSectionRefactoringSupportProvider : RefactoringSupportProvider() {
    /**
     * Returns or performs the public behavior represented by this member.
     */
    override fun isInplaceRenameAvailable(element: PsiElement, context: PsiElement?): Boolean =
        element is IsSectionParamPairEx && element.isNameDeclaration()
}
