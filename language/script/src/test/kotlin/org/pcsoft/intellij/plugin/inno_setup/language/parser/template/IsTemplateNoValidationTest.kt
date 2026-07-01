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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.template

import com.intellij.lang.annotation.HighlightSeverity
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

/**
 * A `.ist` template is free text: even content that would be invalid `.iss` must not produce any
 * ERROR/WARNING highlights, because no annotator/validator is registered for IST.
 */
class IsTemplateNoValidationTest : IsTimedBasePlatformTestCase() {

    fun testBrokenIssContentHasNoHighlights() {
        myFixture.configureByText(
            "broken.ist",
            "[NoSuchSection]\nthis is = totally :: invalid\nSource \"unterminated\nrandom free text 123\n"
        )
        val problems = myFixture.doHighlighting()
            .filter { it.severity >= HighlightSeverity.WARNING }
        assertTrue(
            "A template must not report ERROR/WARNING highlights, found:\n" +
                    problems.joinToString("\n") { "  ${it.severity}: ${it.description}" },
            problems.isEmpty()
        )
    }
}
