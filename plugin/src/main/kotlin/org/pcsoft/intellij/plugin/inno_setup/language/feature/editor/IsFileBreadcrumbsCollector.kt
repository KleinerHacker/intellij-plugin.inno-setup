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
import com.intellij.xml.breadcrumbs.PsiFileBreadcrumbsCollector
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.lang.IsLanguageFileType

/**
 * Breadcrumbs for **injected ISPP lines** — nothing else.
 *
 * With the caret on a `#…` line the platform asks the collector with the *injected* file (a
 * [VirtualFileWindow], whose file type is still the host's). The PSI of that file is ISPP, so the platform's
 * own collector finds nothing and the breadcrumbs bar falls back to the injected file's name
 * (`<Injected ISPP file>`). This collector therefore claims exactly that case and resolves file, document and
 * offset on the host, which makes a preprocessor line behave like any other line of the script. Every other
 * file is left to the platform — see [handlesFile].
 *
 * Sticky lines are never contributed here: the only API for non-PSI (plain range) sticky lines is
 * `computeStickyLineInfos`/`StickyLineInfo`, which is `@ApiStatus.Internal` and therefore off limits for a
 * published plugin. They come exclusively from the PSI-based [IsBreadcrumbsProvider.acceptStickyElement],
 * evaluated by the platform's collector; `#if`/`#sub` blocks have no spanning PSI element and are
 * consequently not sticky.
 */
class IsFileBreadcrumbsCollector(private val project: Project) : FileBreadcrumbsCollector() {

    private val delegate = PsiFileBreadcrumbsCollector(project)

    /**
     * Only the injected case is claimed. For a normal `.iss`/`.isl` file the platform's own
     * `PsiFileBreadcrumbsCollector` (registered with `order="last"`, so this collector wins whenever it
     * claims a file) must stay in charge — it is the only collector that turns
     * [IsBreadcrumbsProvider.acceptStickyElement] into the editor's sticky lines. Claiming the host file too
     * would silence them, because the sticky-line part of the base class returns an empty list and the only
     * way to fill it (`computeStickyLineInfos`) is `@ApiStatus.Internal`.
     */
    override fun handlesFile(virtualFile: VirtualFile): Boolean {
        if (virtualFile !is VirtualFileWindow) return false
        val fileType = virtualFile.delegate.fileType
        return fileType is IsScriptFileType || fileType is IsLanguageFileType
    }

    override fun watchForChanges(file: VirtualFile, editor: Editor, disposable: Disposable, changesHandler: Runnable) =
        delegate.watchForChanges(file, editor, disposable, changesHandler)

    override fun computeCrumbs(
        virtualFile: VirtualFile, document: Document, offset: Int, forcedShown: Boolean?
    ): Iterable<Crumb> = onHost(virtualFile, document, offset) { hostFile, hostDocument, hostOffset ->
        delegate.computeCrumbs(hostFile, hostDocument, hostOffset, forcedShown)
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
