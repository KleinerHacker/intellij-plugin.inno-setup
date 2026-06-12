package org.pcsoft.intellij.plugin.inno_setup.language.file_type.lang

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.impl.DebugUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.file_type.script.IsScriptFile

class IsLanguageParserTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/resources"

    private fun islFile(): IsScriptFile {
        val rawFile = myFixture.file
        if (rawFile is IsLanguageFile) return rawFile
        return InjectedLanguageManager.getInstance(myFixture.project)
            .getTopLevelFile(rawFile) as? IsScriptFile
            ?: error("Expected IsLanguageFile but got ${rawFile.javaClass.name}")
    }

    fun testSimpleIslPsiTree() {
        myFixture.configureByFile("scripts/simple.isl")
        val actualTree = DebugUtil.psiToString(islFile(), false).trimEnd()
        assertSameLinesWithFile("$testDataPath/scripts/simple.isl.tree", actualTree)
    }

}