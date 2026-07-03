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

package org.pcsoft.intellij.plugin.inno_setup.script.language.feature.editor

import com.intellij.lang.Commenter

/**
 * Defines line and block comment syntax for Inno Setup files.
 */
class IsCommenter : Commenter {
    /**
     * Returns comment syntax used by the editor.
     */
    override fun getLineCommentPrefix(): String = ";"

    /**
     * Returns comment syntax used by the editor.
     */
    override fun getBlockCommentPrefix(): String? = null

    /**
     * Returns comment syntax used by the editor.
     */
    override fun getBlockCommentSuffix(): String? = null

    /**
     * Returns comment syntax used by the editor.
     */
    override fun getCommentedBlockCommentPrefix(): String? = null

    /**
     * Returns comment syntax used by the editor.
     */
    override fun getCommentedBlockCommentSuffix(): String? = null
}
