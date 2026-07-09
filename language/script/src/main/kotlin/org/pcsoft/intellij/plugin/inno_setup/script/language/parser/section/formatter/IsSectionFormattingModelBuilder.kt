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

package org.pcsoft.intellij.plugin.inno_setup.script.language.parser.section.formatter

import com.intellij.formatting.Block
import com.intellij.formatting.FormattingContext
import com.intellij.formatting.FormattingModel
import com.intellij.formatting.FormattingModelBuilder
import com.intellij.formatting.FormattingModelProvider
import com.intellij.formatting.Indent
import com.intellij.formatting.Spacing
import com.intellij.formatting.Wrap
import com.intellij.lang.ASTNode
import com.intellij.psi.formatter.common.AbstractBlock

/**
 * Enables *Reformat Code* for Inno Setup `.iss`/`.isl` files. The whole file is exposed as a single opaque
 * leaf block, so the model itself never rewrites whitespace — that would be unsafe given line breaks are real
 * `CRLF` tokens and the `[Code]` section is free-form Pascal Script. All actual formatting (bracket, `=`, `:`,
 * `;` spacing, blank lines, leading-key trimming and preprocessor operator spacing) is performed with precise,
 * PSI-guided edits by [IsFormatterPostFormatProcessor], which the reformat pipeline runs right after this
 * (no-op) model pass.
 */
class IsSectionFormattingModelBuilder : FormattingModelBuilder {

    override fun createModel(formattingContext: FormattingContext): FormattingModel {
        val root = LeafBlock(formattingContext.node)
        return FormattingModelProvider.createFormattingModelForPsiFile(
            formattingContext.containingFile, root, formattingContext.codeStyleSettings,
        )
    }

    /** A single leaf covering the whole file: no children, no spacing, no indentation changes. */
    private class LeafBlock(node: ASTNode) : AbstractBlock(node, null as Wrap?, null) {
        override fun buildChildren(): List<Block> = emptyList()
        override fun getSpacing(child1: Block?, child2: Block): Spacing? = null
        override fun getIndent(): Indent = Indent.getNoneIndent()
        override fun isLeaf(): Boolean = true
    }
}
