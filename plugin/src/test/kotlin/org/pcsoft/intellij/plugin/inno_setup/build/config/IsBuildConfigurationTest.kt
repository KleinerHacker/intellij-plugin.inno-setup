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
 * Developer tests for [IsBuildConfiguration]: the ISCC arguments it contributes and — most importantly —
 * the [IsBuildConfiguration.contentHash] the build's up-to-date check depends on.
 */
class IsBuildConfigurationTest : IsTimedTestCase() {

    // ── ISCC arguments ───────────────────────────────────────────────────────────

    /** Symbols are turned into `/D` arguments, valued ones keeping their value. */
    fun testDefinesBecomeIsccArguments() {
        val config = IsBuildConfiguration("c", compilerSymbols = "DEBUG, VERSION=2")
        assertEquals(listOf("/DDEBUG", "/DVERSION=2"), config.isccDefines)
    }

    /** A configuration without symbols contributes no `/D` argument at all. */
    fun testNoSymbolsYieldNoArguments() {
        assertTrue(IsBuildConfiguration("c").isccDefines.isEmpty())
    }

    /** Raw options are split on whitespace, so each becomes one command-line argument. */
    fun testOptionsAreSplitIntoArguments() {
        val config = IsBuildConfiguration("c", additionalIsccOptions = "/Q  /Sname=sign")
        assertEquals(listOf("/Q", "/Sname=sign"), config.isccOptions)
    }

    /** A quoted value stays one argument, because a path with spaces is still a single option. */
    fun testQuotedOptionStaysOneArgument() {
        val config = IsBuildConfiguration("c", additionalIsccOptions = """/O"C:\My Out" /Q""")
        assertEquals(listOf("""/O"C:\My Out"""", "/Q"), config.isccOptions)
    }

    /** The symbols exposed to the editor's `#ifdef` analysis carry `null` for a value-less symbol. */
    fun testSymbolsMapForEditorAnalysis() {
        val config = IsBuildConfiguration("c", compilerSymbols = "DEBUG VERSION=2")
        assertEquals(mapOf("DEBUG" to null, "VERSION" to "2"), config.symbols)
    }

    // ── content hash: what must NOT trigger a rebuild ────────────────────────────

    /** The same content hashes the same, so an unchanged configuration reuses the previous installer. */
    fun testHashIsStableForEqualContent() {
        val a = IsBuildConfiguration("a", "DEBUG", "out", "/Q")
        val b = IsBuildConfiguration("a", "DEBUG", "out", "/Q")
        assertEquals(a.contentHash(), b.contentHash())
    }

    /** Renaming a configuration must not rebuild: the name changes nothing about what is compiled. */
    fun testHashIgnoresName() {
        val debug = IsBuildConfiguration("Debug", "DEBUG")
        val renamed = debug.copy(name = "Development")
        assertEquals(debug.contentHash(), renamed.contentHash())
    }

    /** Re-spelling the same symbol set (separators, order-preserving duplicates) must not rebuild. */
    fun testHashIgnoresSymbolSpelling() {
        val commas = IsBuildConfiguration("c", compilerSymbols = "DEBUG,VERSION=2")
        val spaces = IsBuildConfiguration("c", compilerSymbols = "DEBUG VERSION = 2")
        val prefixed = IsBuildConfiguration("c", compilerSymbols = "/DDEBUG;/DVERSION=2")
        assertEquals(commas.contentHash(), spaces.contentHash())
        assertEquals(commas.contentHash(), prefixed.contentHash())
    }

    /** A duplicate symbol is dropped by ISCC's last-one-wins rule, so it must not rebuild either. */
    fun testHashIgnoresRedundantDuplicateSymbol() {
        val once = IsBuildConfiguration("c", compilerSymbols = "DEBUG")
        val twice = IsBuildConfiguration("c", compilerSymbols = "DEBUG DEBUG")
        assertEquals(once.contentHash(), twice.contentHash())
    }

    /** Surrounding whitespace in the output override is not part of the path. */
    fun testHashIgnoresOutputDirWhitespace() {
        val plain = IsBuildConfiguration("c", outputDirOverride = "dist")
        val padded = IsBuildConfiguration("c", outputDirOverride = "  dist  ")
        assertEquals(plain.contentHash(), padded.contentHash())
    }

    // ── content hash: what MUST trigger a rebuild ───────────────────────────────

    /** Adding a symbol changes what the preprocessor compiles and must invalidate the installer. */
    fun testHashChangesWithAddedSymbol() {
        val without = IsBuildConfiguration("c")
        val with = IsBuildConfiguration("c", compilerSymbols = "DEBUG")
        assertFalse(without.contentHash() == with.contentHash())
    }

    /** Changing only a symbol's value must invalidate too — `#if VERSION > 2` may flip. */
    fun testHashChangesWithSymbolValue() {
        val two = IsBuildConfiguration("c", compilerSymbols = "VERSION=2")
        val three = IsBuildConfiguration("c", compilerSymbols = "VERSION=3")
        assertFalse(two.contentHash() == three.contentHash())
    }

    /** A different output directory produces a different installer location. */
    fun testHashChangesWithOutputDir() {
        val a = IsBuildConfiguration("c", outputDirOverride = "dist/debug")
        val b = IsBuildConfiguration("c", outputDirOverride = "dist/release")
        assertFalse(a.contentHash() == b.contentHash())
    }

    /** Extra ISCC options change the compiler invocation and therefore the result. */
    fun testHashChangesWithAdditionalOptions() {
        val a = IsBuildConfiguration("c", additionalIsccOptions = "/Q")
        val b = IsBuildConfiguration("c", additionalIsccOptions = "/Q /Sname=sign")
        assertFalse(a.contentHash() == b.contentHash())
    }

    /** The two default configurations differ, so switching between them always rebuilds. */
    fun testDefaultsDifferFromEachOther() {
        val defaults = IsBuildConfiguration.defaults()
        assertEquals(2, defaults.size)
        assertEquals(IsBuildConfiguration.DEFAULT_RELEASE_NAME, defaults[0].name)
        assertEquals(IsBuildConfiguration.DEFAULT_DEBUG_NAME, defaults[1].name)
        assertTrue(defaults[0].compilerSymbols.isEmpty())
        assertEquals(mapOf("DEBUG" to null), defaults[1].symbols)
        assertFalse(defaults[0].contentHash() == defaults[1].contentHash())
    }
}
