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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.action

import com.intellij.openapi.components.service
import com.intellij.openapi.util.Disposer
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import com.intellij.ui.components.JBTextField
import org.pcsoft.intellij.plugin.inno_setup.services.IsLanguageDataService

/**
 * Tests for [IsScriptCreateFileDialog]'s exposed input properties (the values consumed by
 * [IsScriptCreateFileAction.buildTemplate]). The dialog is never shown; only its model is exercised.
 */
class IsScriptCreateFileDialogTest : BasePlatformTestCase() {

    private fun withDialog(block: (IsScriptCreateFileDialog) -> Unit) {
        val dialog = IsScriptCreateFileDialog(project)
        try {
            block(dialog)
        } finally {
            Disposer.dispose(dialog.disposable)
        }
    }

    /** Sets the text of a private [JBTextField] field on the dialog (no public setter exists). */
    private fun setField(dialog: IsScriptCreateFileDialog, fieldName: String, text: String) {
        val field = IsScriptCreateFileDialog::class.java.getDeclaredField(fieldName).apply { isAccessible = true }
        (field.get(dialog) as JBTextField).text = text
    }

    fun testDefaults() = withDialog { dialog ->
        assertEquals("App version must default to 1.0", "1.0", dialog.appVersion)
        assertEquals("File name is empty by default", "", dialog.fileName)
        assertEquals("App name is empty by default", "", dialog.appName)
    }

    fun testEnglishIsPreselected() = withDialog { dialog ->
        val english = service<IsLanguageDataService>().fromIssName("english")
        assertNotNull("english must be a built-in language", english)
        assertEquals(
            "English must be the single pre-selected language",
            listOf(english),
            dialog.selectedLanguages
        )
    }

    fun testGettersTrimWhitespace() = withDialog { dialog ->
        setField(dialog, "fileNameField", "  setup  ")
        setField(dialog, "appNameField", "  My App  ")
        setField(dialog, "appVersionField", "  2.0  ")
        assertEquals("setup", dialog.fileName)
        assertEquals("My App", dialog.appName)
        assertEquals("2.0", dialog.appVersion)
    }
}
