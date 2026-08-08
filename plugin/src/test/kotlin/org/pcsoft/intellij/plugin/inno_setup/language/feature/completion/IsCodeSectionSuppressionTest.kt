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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.completion

import org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section.IsParameterFlipIntentionAction
import org.pcsoft.intellij.plugin.inno_setup.language.feature.editor.section.IsSectionDocumentationProvider
import org.pcsoft.intellij.plugin.inno_setup.script.language.file_type.IsScriptFileType
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * Tests the editor features that are switched off inside \[Code], where the content is free-form Pascal
 * rather than ISS syntax: quick documentation and the parameter-flip intention. Code completion is
 * deliberately *not* restricted there.
 */
class IsCodeSectionSuppressionTest : IsTimedBasePlatformTestCase() {

    /**
     * Quick documentation resolves no ISS target inside \[Code]: an identifier there is never a directive
     * key, so the provider must not claim it.
     */
    fun testQuickDocIsNotOfferedInCodeSection() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Code]\nprocedure Foo();\nbegin\n  AppName<caret>\nend;\n")
        val context = myFixture.file.findElementAt(myFixture.caretOffset - 1)
        val target = IsSectionDocumentationProvider().getCustomDocumentationElement(
            myFixture.editor, myFixture.file, context, myFixture.caretOffset
        )
        assertNull("[Code] must not provide a quick-doc target, was: $target", target)
    }

    /**
     * Outside \[Code] the same provider still resolves a directive key, so the test above states something
     * about \[Code] rather than about broken documentation.
     */
    fun testQuickDocIsStillOfferedOutsideCodeSection() {
        myFixture.configureByText(IsScriptFileType.INSTANCE, "[Setup]\nAppName<caret>=Test\n")
        val context = myFixture.file.findElementAt(myFixture.caretOffset - 1)
        val target = IsSectionDocumentationProvider().getCustomDocumentationElement(
            myFixture.editor, myFixture.file, context, myFixture.caretOffset
        )
        assertNotNull("[Setup] must provide a quick-doc target for a directive key", target)
    }

    /**
     * The "Flip parameters" intention must not be offered inside \[Code]: a `;` there ends a Pascal
     * statement instead of separating ISS parameters, so a flip would corrupt the script.
     */
    fun testParameterFlipIntentionIsNotAvailableInCodeSection() {
        myFixture.configureByText(
            IsScriptFileType.INSTANCE,
            "[Code]\nprocedure Foo();\nbegin\n  a := 1;<caret> b := 2;\nend;\n"
        )
        val available = IsParameterFlipIntentionAction().isAvailable(project, myFixture.editor, myFixture.file)
        assertFalse("[Code] must not offer the parameter flip intention", available)
    }
}
