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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor

import com.intellij.psi.PsiElement
import com.intellij.psi.impl.FakePsiElement

/**
 * Lightweight, non-physical PSI element that carries pre-rendered quick-documentation HTML from a
 * completion lookup entry to a documentation provider's `generateDoc`.
 *
 * The platform calls `getDocumentationElementForLookupItem` while a lookup entry is highlighted and
 * hands the returned element back to `generateDoc`. Since the lookup objects in this plugin are plain
 * strings (no backing PSI), the provider resolves the matching spec entry up front, renders the HTML,
 * and wraps it in this stub; `generateDoc` then recognises the stub and returns the stored HTML verbatim.
 */
class IsDocLookupStub(private val context: PsiElement, val docHtml: String) : FakePsiElement() {
    override fun getParent(): PsiElement = context
}
