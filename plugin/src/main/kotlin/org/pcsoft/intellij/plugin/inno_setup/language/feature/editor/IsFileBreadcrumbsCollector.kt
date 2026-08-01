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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.editor

import com.intellij.codeInsight.breadcrumbs.FileBreadcrumbsCollector
import com.intellij.injected.editor.VirtualFileWindow
import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.Disposable
import com.intellij.openapi.editor.Document
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile
import com.intellij.psi.PsiDocumentManager
import com.intellij.psi.PsiManager
import com.intellij.ui.components.breadcrumbs.Crumb
import com.intellij.ui.components.breadcrumbs.StickyLineInfo
import com.intellij.xml.breadcrumbs.PsiFileBreadcrumbsCollector
import org.pcsoft.intellij.plugin.inno_setup.script.language.feature.editor.section.IsPreprocessorBlockRanges
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.lang.IsLanguageFileType

/**
 * Breadcrumbs and sticky lines of Inno Setup files, on top of the platform's PSI-based collector.
 *
 * Two things need to be added to the delegate:
 * - **Preprocessor blocks**: `#if … #endif` and `#sub … #endsub` have no spanning PSI element (they are
 *   matched line-wise, see [IsPreprocessorBlockRanges]), so they can only be contributed as plain text
 *   ranges — branch-aware, so an `#elif`/`#else` sticks instead of / below the block's opener.
 * - **Injected ISPP lines**: with the caret on a `#…` line the platform asks the collector with the
 *   *injected* file (a [VirtualFileWindow], whose file type is still the host's). The PSI of that file is
 *   ISPP, so the delegate finds nothing and the breadcrumbs bar falls back to the injected file's name
 *   (`<Injected ISPP file>`). Everything is therefore resolved on the host file and host offset, which makes
 *   a preprocessor line behave exactly like any other line of the script.
 */
class IsFileBreadcrumbsCollector(private val project: Project) : FileBreadcrumbsCollector() {

    private val delegate = PsiFileBreadcrumbsCollector(project)

    override fun handlesFile(virtualFile: VirtualFile): Boolean {
        val fileType = virtualFile.fileType
        return fileType is IsScriptFileType || fileType is IsLanguageFileType
    }

    override fun watchForChanges(file: VirtualFile, editor: Editor, disposable: Disposable, changesHandler: Runnable) =
        delegate.watchForChanges(file, editor, disposable, changesHandler)

    override fun computeCrumbs(
        virtualFile: VirtualFile, document: Document, offset: Int, forcedShown: Boolean?
    ): Iterable<Crumb> = onHost(virtualFile, document, offset) { hostFile, hostDocument, hostOffset ->
        delegate.computeCrumbs(hostFile, hostDocument, hostOffset, forcedShown)
    }

    override fun computeStickyLineInfos(virtualFile: VirtualFile, document: Document, offset: Int): List<StickyLineInfo> =
        onHost(virtualFile, document, offset) { hostFile, hostDocument, hostOffset ->
            delegate.computeStickyLineInfos(hostFile, hostDocument, hostOffset) +
                    preprocessorInfos(hostFile, hostOffset)
        }

    /** The active branch of every preprocessor block enclosing [offset], as sticky line infos. */
    private fun preprocessorInfos(virtualFile: VirtualFile, offset: Int): List<StickyLineInfo> {
        val file = PsiManager.getInstance(project).findFile(virtualFile) as? IsScriptFile ?: return emptyList()
        return IsPreprocessorBlockRanges.stickyRangesAt(file, offset).map { StickyLineInfo(it) }
    }

    /**
     * Runs [block] on the host file, document and offset — unchanged for a normal file, translated out of the
     * injection when the platform asks with an injected ISPP fragment.
     */
    private fun <T> onHost(
        virtualFile: VirtualFile, document: Document, offset: Int,
        block: (VirtualFile, Document, Int) -> T,
    ): T {
        if (virtualFile !is VirtualFileWindow) return block(virtualFile, document, offset)

        val injectedFile = PsiManager.getInstance(project).findFile(virtualFile)
            ?: return block(virtualFile, document, offset)
        val manager = InjectedLanguageManager.getInstance(project)
        val hostFile = manager.getTopLevelFile(injectedFile)
        val hostVirtualFile = hostFile.virtualFile ?: return block(virtualFile, document, offset)
        val hostDocument = PsiDocumentManager.getInstance(project).getDocument(hostFile)
            ?: return block(virtualFile, document, offset)

        return block(hostVirtualFile, hostDocument, manager.injectedToHost(injectedFile, offset))
    }
}
