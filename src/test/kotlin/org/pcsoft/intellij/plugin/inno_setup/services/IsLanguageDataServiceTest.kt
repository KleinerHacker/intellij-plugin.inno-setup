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

package org.pcsoft.intellij.plugin.inno_setup.services

import com.intellij.openapi.components.service
import com.intellij.testFramework.fixtures.BasePlatformTestCase

/** Tests for the Windows-LCID reference data (is-lang-code.yaml) backing LanguageID completion/validation/inlays. */
class IsLanguageDataServiceTest : BasePlatformTestCase() {

    private val service get() = service<IsLanguageDataService>()

    fun testParseIdHandlesHexDecimalAndZero() {
        assertEquals(0x0409, IsLanguageDataService.parseId("\$0409"))
        assertEquals(0, IsLanguageDataService.parseId("0"))
        assertEquals(0, IsLanguageDataService.parseId("\$0"))
        assertEquals(1033, IsLanguageDataService.parseId("1033")) // decimal == 0x0409
    }

    fun testParseIdReturnsNullForMalformed() {
        assertNull(IsLanguageDataService.parseId("\$GG"))
        assertNull(IsLanguageDataService.parseId("abc"))
        assertNull(IsLanguageDataService.parseId(""))
    }

    fun testFromIdResolvesCuratedLocale() {
        assertEquals("German (Germany)", service.fromId(0x0407)?.displayName)
        assertEquals("English (United States)", service.fromId(0x0409)?.displayName)
    }

    fun testEveryRecognisedLocaleResolves() {
        // validIds now derives from the languages list, so every recognised LCID has an entry.
        assertTrue("Afrikaans must be a recognised LCID", 0x0436 in service.validIds)
        assertEquals("Afrikaans (South Africa)", service.fromId(0x0436)?.displayName)
    }

    fun testFromIdReturnsNullForUnassignedLcid() {
        assertFalse("\$9999 must not be a recognised LCID", 0x9999 in service.validIds)
        assertNull(service.fromId(0x9999))
    }

    fun testEveryCuratedEntryHasParseableIdInValidSet() {
        service.entries.forEach { entry ->
            val numeric = IsLanguageDataService.parseId(entry.id)
            assertNotNull("Curated id ${entry.id} must parse", numeric)
            assertTrue("Curated id ${entry.id} must be a valid LCID", numeric!! in service.validIds)
        }
    }

    fun testFromIssNameResolvesBuiltInLanguages() {
        assertEquals("\$0407", service.fromIssName("german")?.id)
        assertEquals("\$0407", service.fromIssName("GERMAN")?.id)
        assertTrue("german must be built-in", service.fromIssName("german")?.builtin == true)
        assertNull(service.fromIssName("klingon"))
    }

    fun testFromMessagesFileResolvesBuiltInLanguage() {
        assertEquals("german", service.fromMessagesFile("compiler:Languages\\German.isl")?.issName)
        assertEquals("english", service.fromMessagesFile("compiler:Default.isl")?.issName)
        assertNull(service.fromMessagesFile("MyCustom.isl"))
    }
}
