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

package org.pcsoft.intellij.plugin.inno_setup.settings

import com.intellij.openapi.components.Service
import com.intellij.openapi.components.SimplePersistentStateComponent
import com.intellij.openapi.components.State
import com.intellij.openapi.components.Storage
import com.intellij.openapi.components.service
import java.io.File
import java.util.concurrent.TimeUnit

@State(name = "IssSettings", storages = [Storage("inno-setup.xml")])
@Service(Service.Level.APP)
class IssSettingsService : SimplePersistentStateComponent<IssSettingsState>(IssSettingsState()) {

    companion object {
        @JvmStatic
        fun getInstance(): IssSettingsService = service()

        fun compareIsVersions(a: String, b: String): Int {
            val aParts = a.split(".").map { it.toIntOrNull() ?: 0 }
            val bParts = b.split(".").map { it.toIntOrNull() ?: 0 }
            val len = maxOf(aParts.size, bParts.size)
            for (i in 0 until len) {
                val diff = aParts.getOrElse(i) { 0 } - bParts.getOrElse(i) { 0 }
                if (diff != 0) return diff
            }
            return 0
        }

        internal fun detectVersionFromPath(path: String): String? {
            if (path.isBlank()) return null
            val dir = File(path)
            if (!dir.resolve("ISCC.exe").exists()) return null
            if (!dir.resolve("Compil32.exe").exists()) return null
            return try {
                val process = ProcessBuilder(dir.resolve("ISCC.exe").absolutePath, "/?")
                    .redirectErrorStream(true)
                    .start()
                val firstLine = process.inputStream.bufferedReader().readLine() ?: ""
                process.waitFor(5, TimeUnit.SECONDS)
                process.destroy()
                Regex("Inno Setup (\\d+)").find(firstLine)
                    ?.groupValues?.get(1)
                    ?.let { "Inno Setup $it" }
            } catch (_: Exception) {
                null
            }
        }
    }

    fun detectVersion(path: String): String? = detectVersionFromPath(path)
}
