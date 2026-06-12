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

package org.pcsoft.intellij.plugin.inno_setup.language.feature

import com.intellij.openapi.util.SystemInfo
import junit.framework.TestCase
import java.io.File

class IsConstantResolverTest : TestCase() {

    fun testSrcResolvesToScriptDir() {
        val dir = File(System.getProperty("java.io.tmpdir"))
        assertEquals(dir.absolutePath, IsConstantResolver.resolve("src", dir))
    }

    fun testSrcNullWhenNoDirGiven() {
        assertNull(IsConstantResolver.resolve("src", null))
    }

    fun testTmpReturnsJavaTmpDir() {
        val expected = System.getProperty("java.io.tmpdir")
        assertEquals(expected, IsConstantResolver.resolve("tmp", null))
    }

    fun testWinOnWindowsOnly() {
        val result = IsConstantResolver.resolve("win", null)
        if (SystemInfo.isWindows) {
            assertNotNull("{win} must resolve on Windows", result)
        } else {
            assertNull("{win} must not resolve on non-Windows", result)
        }
    }

    fun testSysOnWindowsOnly() {
        val result = IsConstantResolver.resolve("sys", null)
        if (SystemInfo.isWindows) {
            assertNotNull("{sys} must resolve on Windows", result)
            assertTrue(result!!.contains("System32", ignoreCase = true))
        } else {
            assertNull("{sys} must not resolve on non-Windows", result)
        }
    }

    fun testInstallTimeConstantsReturnNull() {
        for (name in listOf("app", "group", "autopf", "autopf64", "autopf32", "language")) {
            assertNull("Install-time constant {$name} must return null", IsConstantResolver.resolve(name, null))
        }
    }

    fun testUnknownConstantReturnsNull() {
        assertNull(IsConstantResolver.resolve("definitelyNotAConstant", null))
    }

    fun testCaseInsensitive() {
        val lower = IsConstantResolver.resolve("tmp", null)
        val upper = IsConstantResolver.resolve("TMP", null)
        assertEquals(lower, upper)
    }
}
