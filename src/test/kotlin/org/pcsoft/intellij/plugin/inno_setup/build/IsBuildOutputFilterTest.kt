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

package org.pcsoft.intellij.plugin.inno_setup.build

import com.intellij.execution.filters.OpenFileHyperlinkInfo
import com.intellij.openapi.util.io.FileUtil
import com.intellij.openapi.vfs.LocalFileSystem
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/**
 * Tests [IsBuildOutputFilter]: the `line XX` token of an ISCC error line becomes a file hyperlink.
 *
 * Uses a real on-disk file because ISCC reports local paths and the filter resolves them via
 * [LocalFileSystem] (the in-memory fixture VFS would not be found there).
 */
class IsBuildOutputFilterTest : BasePlatformTestCase() {

    private val filter get() = IsBuildOutputFilter(project)

    /** Returns the VFS path of a freshly created on-disk `.iss` file. */
    private fun diskScriptPath(): String {
        val ioFile = FileUtil.createTempFile("setup", ".iss", true)
        ioFile.writeText("[Setup]\nAppName=x\n")
        return LocalFileSystem.getInstance().refreshAndFindFileByIoFile(ioFile)!!.path
    }

    fun testLinksLineTokenForResolvableError() {
        val line = "Error on line 2 in ${diskScriptPath()}: Unknown directive\n"

        val result = filter.applyFilter(line, line.length)
        assertNotNull(result)
        val item = result!!.resultItems.single()
        assertEquals("line 2", line.substring(item.highlightStartOffset, item.highlightEndOffset))
        assertTrue(item.hyperlinkInfo is OpenFileHyperlinkInfo)
    }

    fun testOffsetsRespectEntireLength() {
        val line = "Error on line 1 in ${diskScriptPath()}: oops\n"
        val prefix = 1000 // pretend the console already contains 1000 chars before this line

        val result = filter.applyFilter(line, prefix + line.length)
        val item = result!!.resultItems.single()
        assertEquals("line 1", line.substring(item.highlightStartOffset - prefix, item.highlightEndOffset - prefix))
    }

    fun testPlainLineHasNoLink() {
        assertNull(filter.applyFilter("Compiling setup.iss...\n", 24))
    }

    fun testUnresolvableFileHasNoLink() {
        val line = "Error on line 3 in Z:/does/not/exist.iss: x\n"
        assertNull(filter.applyFilter(line, line.length))
    }
}
