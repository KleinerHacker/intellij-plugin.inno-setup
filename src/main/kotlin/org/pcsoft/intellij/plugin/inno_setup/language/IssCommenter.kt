package org.pcsoft.intellij.plugin.inno_setup.language

import com.intellij.lang.Commenter

class IssCommenter : Commenter {
    override fun getLineCommentPrefix(): String = ";"
    override fun getBlockCommentPrefix(): String? = null
    override fun getBlockCommentSuffix(): String? = null
    override fun getCommentedBlockCommentPrefix(): String? = null
    override fun getCommentedBlockCommentSuffix(): String? = null
}
