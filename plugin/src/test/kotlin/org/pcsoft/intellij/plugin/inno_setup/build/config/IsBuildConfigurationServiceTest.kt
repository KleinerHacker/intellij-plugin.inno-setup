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

import org.pcsoft.intellij.plugin.inno_setup.test.IsTimedBasePlatformTestCase
import java.io.File

/**
 * Integration tests for [IsBuildConfigurationService] against a real `.build` directory on disk.
 */
class IsBuildConfigurationServiceTest : IsTimedBasePlatformTestCase() {

    private lateinit var service: IsBuildConfigurationService
    private lateinit var directory: File

    override fun setUp() {
        super.setUp()
        // The light test project has no base directory on disk, so the store is pointed at a real temp
        // directory — the same layout a project would have, just elsewhere.
        directory = File.createTempFile("inno-build-config", "").let { probe ->
            probe.delete()
            File(probe.parentFile, probe.name + "-" + IsBuildConfigurationService.DIRECTORY_NAME)
        }
        service = IsBuildConfigurationService.getInstance(project)
        service.directoryOverride = directory
    }

    override fun tearDown() {
        try {
            IsBuildConfigurationService.getInstance(project).directoryOverride = null
            directory.listFiles()?.forEach { it.delete() }
            directory.delete()
        } finally {
            super.tearDown()
        }
    }

    /** A project without configurations reports none — the state a fresh checkout is in. */
    fun testEmptyProjectHasNoConfigurations() {
        assertTrue(service.all().isEmpty())
        assertNull(service.defaultConfiguration())
    }

    /** The starter set is created on first use: one without symbols and one defining `DEBUG`. */
    fun testEnsureDefaultsCreatesStarterSet() {
        service.ensureDefaults()

        val names = service.all().map { it.name }
        assertTrue(IsBuildConfiguration.DEFAULT_RELEASE_NAME in names)
        assertTrue(IsBuildConfiguration.DEFAULT_DEBUG_NAME in names)
        assertEquals(
            mapOf("DEBUG" to null),
            service.byName(IsBuildConfiguration.DEFAULT_DEBUG_NAME)?.symbols
        )
        assertTrue(service.byName(IsBuildConfiguration.DEFAULT_RELEASE_NAME)?.compilerSymbols.isNullOrEmpty())
    }

    /** Each configuration becomes one file in `.build`, so they can be reviewed and merged individually. */
    fun testEachConfigurationIsOwnFile() {
        service.ensureDefaults()

        val files = directory.listFiles { f -> f.name.endsWith(IsBuildConfigurationXml.FILE_SUFFIX) }
        assertEquals(2, files?.size)
    }

    /** Running the defaults twice does not duplicate or overwrite anything. */
    fun testEnsureDefaultsIsIdempotent() {
        service.ensureDefaults()
        service.save(service.byName(IsBuildConfiguration.DEFAULT_DEBUG_NAME)!!.copy(compilerSymbols = "DEBUG,X"))

        service.ensureDefaults()

        assertEquals(2, service.all().size)
        assertEquals("DEBUG,X", service.byName(IsBuildConfiguration.DEFAULT_DEBUG_NAME)?.compilerSymbols)
    }

    /** A saved configuration is readable again with all its fields. */
    fun testSaveAndRead() {
        service.save(IsBuildConfiguration("Custom", "A=1", "dist", "/Q"))

        val read = service.byName("Custom")
        assertNotNull(read)
        assertEquals("A=1", read!!.compilerSymbols)
        assertEquals("dist", read.outputDirOverride)
        assertEquals("/Q", read.additionalIsccOptions)
    }

    /** Saving under an existing name replaces that configuration instead of adding a second one. */
    fun testSaveOverwritesSameName() {
        service.save(IsBuildConfiguration("Custom", "A"))
        service.save(IsBuildConfiguration("Custom", "B"))

        assertEquals(1, service.all().size)
        assertEquals("B", service.byName("Custom")?.compilerSymbols)
    }

    /** Deleting removes the configuration and its file. */
    fun testDeleteRemovesConfiguration() {
        service.save(IsBuildConfiguration("Custom"))
        service.delete("Custom")

        assertNull(service.byName("Custom"))
        assertTrue(service.all().isEmpty())
    }

    /** Deleting a name that does not exist is a no-op rather than an error. */
    fun testDeleteUnknownNameIsHarmless() {
        service.save(IsBuildConfiguration("Custom"))
        service.delete("Nope")

        assertEquals(1, service.all().size)
    }

    /** `replaceAll` — the settings page's commit — applies additions, edits and removals in one go. */
    fun testReplaceAllAppliesAdditionsAndRemovals() {
        service.save(IsBuildConfiguration("Old"))
        service.save(IsBuildConfiguration("Kept", "A"))

        service.replaceAll(listOf(IsBuildConfiguration("Kept", "B"), IsBuildConfiguration("New")))

        assertEquals(listOf("Kept", "New"), service.all().map { it.name })
        assertEquals("B", service.byName("Kept")?.compilerSymbols)
    }

    /** An unknown or blank name resolves to no configuration, which is what makes a run report it. */
    fun testByNameForUnknownAndBlank() {
        service.ensureDefaults()
        assertNull(service.byName("Nope"))
        assertNull(service.byName(""))
        assertNull(service.byName(null))
    }

    /** The default a run falls back to is `Debug` when present. */
    fun testDefaultConfigurationPrefersDebug() {
        service.ensureDefaults()
        assertEquals(IsBuildConfiguration.DEFAULT_DEBUG_NAME, service.defaultConfiguration()?.name)
    }

    /** Without a `Debug` configuration the first one is used, so the fallback never fails. */
    fun testDefaultConfigurationFallsBackToFirst() {
        service.save(IsBuildConfiguration("Alpha"))
        service.save(IsBuildConfiguration("Beta"))
        assertEquals("Alpha", service.defaultConfiguration()?.name)
    }

    /** A configuration file edited outside the IDE (a VCS pull) is picked up without a restart. */
    fun testExternalChangeIsPickedUp() {
        service.save(IsBuildConfiguration("Custom", "A"))
        assertEquals("A", service.byName("Custom")?.compilerSymbols)

        val file = File(directory, IsBuildConfigurationXml.fileNameFor("Custom"))
        file.writeText(IsBuildConfigurationXml.toText(IsBuildConfiguration("Custom", "B")))
        file.setLastModified(file.lastModified() + 2000)

        assertEquals("B", service.byName("Custom")?.compilerSymbols)
    }

    /** A corrupt file is skipped instead of breaking every other configuration. */
    fun testCorruptFileIsSkipped() {
        service.save(IsBuildConfiguration("Good"))
        File(directory, "broken" + IsBuildConfigurationXml.FILE_SUFFIX).writeText("<not xml")

        assertEquals(listOf("Good"), service.all().map { it.name })
    }

    /** Unrelated files in `.build` are ignored. */
    fun testForeignFilesAreIgnored() {
        service.save(IsBuildConfiguration("Good"))
        File(directory, "notes.txt").writeText("hello")

        assertEquals(listOf("Good"), service.all().map { it.name })
    }
}
