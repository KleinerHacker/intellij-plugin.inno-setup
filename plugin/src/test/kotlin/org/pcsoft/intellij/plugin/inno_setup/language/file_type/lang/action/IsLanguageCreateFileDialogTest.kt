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

package org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.action

import com.intellij.openapi.components.service
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.util.Disposer
import com.intellij.ui.components.JBTextField
import org.pcsoft.intellij.plugin.inno_setup.script.services.IsLanguageDataService
import org.pcsoft.intellij.plugin.inno_setup.script.types.IsLanguageDataSpec
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests for [IsLanguageCreateFileDialog]'s exposed input properties (the values consumed by
 * [IsLanguageCreateFileAction.buildTemplate]). The dialog is never shown; only its model is exercised.
 */
class IsLanguageCreateFileDialogTest : IsTimedBasePlatformTestCase() {

    private fun withDialog(block: (IsLanguageCreateFileDialog) -> Unit) {
        val dialog = IsLanguageCreateFileDialog(project)
        try {
            block(dialog)
        } finally {
            Disposer.dispose(dialog.disposable)
        }
    }

    private fun setField(dialog: IsLanguageCreateFileDialog, fieldName: String, text: String) {
        val field = IsLanguageCreateFileDialog::class.java.getDeclaredField(fieldName).apply { isAccessible = true }
        (field.get(dialog) as JBTextField).text = text
    }

    @Suppress("UNCHECKED_CAST")
    private fun combo(dialog: IsLanguageCreateFileDialog): ComboBox<IsLanguageDataSpec> {
        val field =
            IsLanguageCreateFileDialog::class.java.getDeclaredField("languageIdCombo").apply { isAccessible = true }
        return field.get(dialog) as ComboBox<IsLanguageDataSpec>
    }

    private val english get() = service<IsLanguageDataService>().fromIssName("english")!!

    fun testEnglishIsPreselected() = withDialog { dialog ->
        assertEquals("English id must be pre-selected", english.id, dialog.languageId)
    }

    fun testLanguageNamePrefilledFromSelection() = withDialog { dialog ->
        // The init block pre-fills the (editable) name field from the default selection.
        assertEquals(english.displayName, dialog.languageName)
    }

    fun testChangingSelectionUpdatesIdAndName() = withDialog { dialog ->
        val other = service<IsLanguageDataService>().entries.first { it.id != english.id }
        combo(dialog).selectedItem = other
        assertEquals("languageId follows the combo selection", other.id, dialog.languageId)
        assertEquals("Selecting a language re-fills the name field", other.displayName, dialog.languageName)
    }

    fun testNameRemainsEditableAfterSelection() = withDialog { dialog ->
        setField(dialog, "languageNameField", "My Custom Language")
        assertEquals("My Custom Language", dialog.languageName)
        // The combo selection is unchanged, so the id still reflects the default.
        assertEquals(english.id, dialog.languageId)
    }

    fun testGettersTrimWhitespace() = withDialog { dialog ->
        setField(dialog, "fileNameField", "  German  ")
        assertEquals("German", dialog.fileName)
    }
}
