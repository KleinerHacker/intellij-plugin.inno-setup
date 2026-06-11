package org.pcsoft.intellij.plugin.inno_setup.language.isl

import com.intellij.lang.injection.InjectedLanguageManager
import com.intellij.psi.impl.DebugUtil
import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.pcsoft.intellij.plugin.inno_setup.language.IssFile

class IslParserTest : BasePlatformTestCase() {

    override fun getTestDataPath() = "src/test/resources"

    private fun islFile(): IssFile {
        val rawFile = myFixture.file
        if (rawFile is IslFile) return rawFile
        return InjectedLanguageManager.getInstance(myFixture.project)
            .getTopLevelFile(rawFile) as? IssFile
            ?: error("Expected IslFile but got ${rawFile.javaClass.name}")
    }

    fun testSimpleIslPsiTree() {
        myFixture.configureByFile("scripts/simple.isl")
        val actualTree = DebugUtil.psiToString(islFile(), false).trimEnd()
        assertSameLinesWithFile("$testDataPath/scripts/simple.isl.tree", actualTree)
    }

}