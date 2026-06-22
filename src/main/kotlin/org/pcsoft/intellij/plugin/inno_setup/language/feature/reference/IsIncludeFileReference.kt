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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.reference

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.openapi.util.TextRange
import com.intellij.openapi.vfs.VfsUtilCore
import com.intellij.psi.*
import com.intellij.psi.util.PsiTreeUtil
import org.pcsoft.intellij.plugin.inno_setup.language.feature.include.IsIncludePaths
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.IsPreprocessorFileType
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorDirective
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.psi.IsPreprocessorQuotedString

/**
 * The file path inside an `#include "…"` directive, resolving to the referenced `*.iss` file.
 *
 * The path is resolved relative to the directory of the script that holds the directive (absolute paths are
 * used as-is). The reference is `soft`, so an unresolved path does not produce a platform error on its own —
 * the dedicated "file not found" diagnostic is raised by `IsPreprocessorAnnotator`.
 *
 * Anchored on the [IsPreprocessorDirective]; [rangeInElement] points at the path within the directive.
 */
class IsIncludeFileReference(
    directive: IsPreprocessorDirective,
    rangeInDirective: TextRange,
    private val path: String,
) : PsiReferenceBase<IsPreprocessorDirective>(directive, rangeInDirective, /* soft = */ true) {

    /**
     * Resolves this reference to the included file, or `null` when it does not exist.
     */
    override fun resolve(): PsiElement? {
        val injMgr = InjectedLanguageManager.getInstance(element.project)
        val hostFile = injMgr.getTopLevelFile(element.containingFile) as? IsScriptFile ?: return null
        val baseDir = hostFile.virtualFile?.parent ?: return null
        val target = IsIncludePaths.resolve(baseDir, path) ?: return null
        return PsiManager.getInstance(element.project).findFile(target)
    }

    /**
     * Returns completion variants available from this reference.
     */
    override fun getVariants(): Array<Any> = emptyArray()

    /**
     * Updates the `#include` path after the referenced file has been renamed: only the file-name segment
     * of the path changes, the directory part is kept.
     */
    override fun handleElementRename(newElementName: String): PsiElement {
        val current = element.getIncludePath() ?: return element
        val dirPart = current.substringBeforeLast('/', "")
            .ifEmpty { current.substringBeforeLast('\\', "") }
        val separator = if (current.contains('\\') && !current.contains('/')) '\\' else '/'
        val newPath = if (dirPart.isEmpty()) newElementName else "$dirPart$separator$newElementName"
        rewritePath(newPath)
        return element
    }

    /**
     * Updates the `#include` path after the referenced file has been moved: the new path is computed
     * relative to the including script's directory (absolute path as a fallback).
     */
    override fun bindToElement(element: PsiElement): PsiElement {
        val target = (element as? PsiFile)?.virtualFile ?: return this.element
        val injMgr = InjectedLanguageManager.getInstance(this.element.project)
        val hostFile = injMgr.getTopLevelFile(this.element.containingFile) as? IsScriptFile ?: return this.element
        val baseDir = hostFile.virtualFile?.parent ?: return this.element
        val newPath = VfsUtilCore.findRelativePath(baseDir, target, '/') ?: target.path
        rewritePath(newPath)
        return this.element
    }

    /**
     * Replaces the quoted-string literal of the directive with a `"<newPath>"` literal.
     */
    private fun rewritePath(newPath: String) {
        val literal = element.getIncludeLiteralString() ?: return
        val dummy = PsiFileFactory.getInstance(element.project)
            .createFileFromText("d.ispp", IsPreprocessorFileType.INSTANCE, "#include \"$newPath\"")
        val newLiteral = PsiTreeUtil.findChildOfType(dummy, IsPreprocessorQuotedString::class.java) ?: return
        literal.replace(newLiteral)
    }
}
