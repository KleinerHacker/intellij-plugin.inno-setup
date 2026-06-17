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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.include

import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.openapi.vfs.VirtualFile
import java.io.File

/**
 * Shared resolution of an ISPP `#include` path. A path is resolved relative to the directory of the
 * including script (absolute paths are used as-is). The single source of truth for the relative/absolute
 * semantics, reused by the file reference, the effective-script resolver and the build-time collector.
 */
object IsIncludePaths {

    /**
     * The [VirtualFile] an `#include` path points to, or `null` when it does not exist. Relative paths are
     * resolved against [baseDir] within the same virtual file system (so `..`/subdirectories work); absolute
     * paths are looked up on the local file system.
     */
    fun resolve(baseDir: VirtualFile, includePath: String): VirtualFile? {
        val normalized = includePath.replace('\\', '/')
        return if (File(includePath).isAbsolute)
            LocalFileSystem.getInstance().findFileByPath(normalized)
        else
            baseDir.findFileByRelativePath(normalized)
    }
}
