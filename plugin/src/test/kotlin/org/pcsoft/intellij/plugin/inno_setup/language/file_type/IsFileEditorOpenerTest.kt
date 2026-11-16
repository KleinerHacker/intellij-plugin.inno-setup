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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type

import com.intellij.openapi.fileEditor.FileEditorManager
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests for [openFileInEditor], the helper the "New ..." file actions use to show a freshly created file.
 */
class IsFileEditorOpenerTest : IsTimedBasePlatformTestCase() {

    /**
     * Use case: a new file was created by one of the "New ..." actions. After [openFileInEditor] the file must
     * be open in the editor of the project and be the selected one.
     */
    fun testOpenedFileIsShownAndSelected() {
        val file = myFixture.addFileToProject("new.iss", "[Setup]\n").virtualFile

        openFileInEditor(project, file)

        val manager = FileEditorManager.getInstance(project)
        assertTrue("The file must be open in the editor", manager.openFiles.any { it == file })
        assertEquals("The file must be the selected one", file, manager.selectedFiles.firstOrNull())
    }
}
