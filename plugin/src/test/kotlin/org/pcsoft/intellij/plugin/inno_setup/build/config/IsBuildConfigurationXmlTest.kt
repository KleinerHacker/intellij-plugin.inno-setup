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

package org.pcsoft.intellij.plugin.inno_setup.build.config

import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedTestCase

/**
 * Developer tests for the `.build` file format handled by [IsBuildConfigurationXml].
 */
class IsBuildConfigurationXmlTest : IsTimedTestCase() {

    /** A fully populated configuration survives a write/read round trip unchanged. */
    fun testRoundTripKeepsAllFields() {
        val config = IsBuildConfiguration("Debug", "DEBUG,VERSION=2", "dist/debug", "/Q")
        assertEquals(config, IsBuildConfigurationXml.fromText(IsBuildConfigurationXml.toText(config)))
    }

    /** Empty optional fields round trip as empty, not as `null`. */
    fun testRoundTripOfEmptyFields() {
        val config = IsBuildConfiguration("Release")
        val read = IsBuildConfigurationXml.fromText(IsBuildConfigurationXml.toText(config))
        assertEquals(config, read)
        assertEquals("", read?.compilerSymbols)
    }

    /** A file without a name is not a configuration and must be rejected rather than loaded nameless. */
    fun testMissingNameIsRejected() {
        assertNull(IsBuildConfigurationXml.fromText("<innoSetupBuildConfiguration compilerSymbols=\"DEBUG\"/>"))
    }

    /** A blank name is rejected for the same reason. */
    fun testBlankNameIsRejected() {
        assertNull(IsBuildConfigurationXml.fromText("<innoSetupBuildConfiguration name=\"  \"/>"))
    }

    /** Malformed XML must not propagate an exception into the settings UI or the build. */
    fun testMalformedXmlIsRejected() {
        assertNull(IsBuildConfigurationXml.fromText("<innoSetupBuildConfiguration"))
    }

    /** The file name is derived from the configuration name plus the fixed suffix. */
    fun testFileNameFromName() {
        assertEquals("Debug.build.xml", IsBuildConfigurationXml.fileNameFor("Debug"))
    }

    /** Characters no file system accepts are replaced, so any name the user types remains storable. */
    fun testFileNameSanitisesIllegalCharacters() {
        assertEquals("a_b_c.build.xml", IsBuildConfigurationXml.fileNameFor("a/b:c"))
    }

    /** Surrounding whitespace is not part of the file name. */
    fun testFileNameTrimsName() {
        assertEquals("Debug.build.xml", IsBuildConfigurationXml.fileNameFor("  Debug  "))
    }
}
