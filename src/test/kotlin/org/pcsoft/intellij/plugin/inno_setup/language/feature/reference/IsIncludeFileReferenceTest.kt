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

package org.pcsoft.intellij.plugin.inno_setup.language.feature.reference

import com.intellij.psi.PsiFile
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.isppDirectives
import org.pcsoft.intellij.plugin.inno_setup.language.parser.preprocessor.parsing.psi.IsPreprocessorDirectiveEx

/**
 * Tests for [IsIncludeFileReference]: an `#include "…"` path resolving to the referenced `*.iss` file.
 */
class IsIncludeFileReferenceTest : BasePlatformTestCase() {

    private fun includeReference(main: IsScriptFile) =
        main.isppDirectives
            .first { (it as IsPreprocessorDirectiveEx).isInclude() }
            .references.filterIsInstance<IsIncludeFileReference>().first()

    fun testIncludeResolvesToExistingFile() {
        val part = myFixture.addFileToProject("sub/part.iss", "[Files]\n")
        val main = myFixture.addFileToProject("main.iss", "#include \"sub/part.iss\"\n") as IsScriptFile

        val resolved = includeReference(main).resolve()
        assertEquals("Include must resolve to the referenced file", part, resolved as? PsiFile)
    }

    fun testMissingIncludeDoesNotResolve() {
        val main = myFixture.addFileToProject("main.iss", "#include \"nope.iss\"\n") as IsScriptFile
        assertNull("A missing include path must not resolve", includeReference(main).resolve())
    }
}
