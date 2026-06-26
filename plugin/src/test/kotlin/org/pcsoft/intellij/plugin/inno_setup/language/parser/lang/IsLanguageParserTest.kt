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

package org.pcsoft.intellij.plugin.inno_setup.language.parser.lang

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.impl.DebugUtil
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang.IsLanguageFile
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile
import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase

class IsLanguageParserTest : IsTimedBasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/resources"

    private fun islFile(): IsScriptFile {
        val rawFile = myFixture.file
        if (rawFile is IsLanguageFile) return rawFile
        return InjectedLanguageManager.getInstance(myFixture.project)
            .getTopLevelFile(rawFile) as? IsScriptFile
            ?: error("Expected IsLanguageFile but got ${rawFile.javaClass.name}")
    }

    fun testSimpleIslPsiTree() {
        myFixture.configureByFile("structure/structure.isl")
        val actualTree = DebugUtil.psiToString(islFile(), false).trimEnd()
        assertSameLinesWithFile("$testDataPath/structure/structure.isl.tree", actualTree)
    }

}