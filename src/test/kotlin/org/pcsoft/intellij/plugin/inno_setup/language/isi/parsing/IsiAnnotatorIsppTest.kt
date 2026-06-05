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

package org.pcsoft.intellij.plugin.inno_setup.language.isi.parsing

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFileType

class IsiAnnotatorIsppTest : BasePlatformTestCase() {

    fun testKnownIsppConstantProducesNoError() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "#define AppVersion \"1.0\"\n[Files]\nSource: \"app.exe\"; DestDir: \"{#AppVersion}\"\n"
        )
        val highlights = myFixture.doHighlighting()
        val errors = highlights.filter {
            it.severity.name == "ERROR" && it.description?.contains("Unknown constant") == true
        }
        assertTrue(
            "Known ISPP constant {#AppVersion} should produce no 'Unknown constant' error",
            errors.isEmpty()
        )
    }

    fun testUnknownIsppConstantProducesError() {
        myFixture.configureByText(
            IssFileType.INSTANCE,
            "[Files]\nSource: \"app.exe\"; DestDir: \"{#Unknown}\"\n"
        )
        val highlights = myFixture.doHighlighting()
        val errors = highlights.filter {
            it.severity.name == "ERROR" && it.description?.contains("Unknown constant") == true
        }
        assertTrue("Unknown ISPP constant {#Unknown} should produce an error", errors.isNotEmpty())
    }
}
