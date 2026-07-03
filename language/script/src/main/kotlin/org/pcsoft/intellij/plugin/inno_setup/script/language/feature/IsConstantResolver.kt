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

package org.pcsoft.intellij.plugin.inno_setup.script.language.feature

import com.intellij.openapi.util.SystemInfo
import java.io.File

/**
 * Resolves Inno Setup built-in directory constants (`{win}`, `{src}`, `{tmp}`, …) to their
 * platform-specific paths at edit time. Returns `null` for constants whose value is only
 * known at install time (`{app}`, `{group}`, `{autopf}`, …) or for OS-specific constants
 * queried on an incompatible OS.
 */
object IsConstantResolver {

    fun resolve(name: String, scriptDir: File?): String? = when (name.lowercase()) {
        "src" -> scriptDir?.absolutePath
        "tmp" -> System.getProperty("java.io.tmpdir")
        "userdocs" -> userHome()?.let { "$it${sep}Documents" }
        "userappdata" -> if (SystemInfo.isWindows) System.getenv("APPDATA")
        else userHome()?.let { "$it${sep}.config" }

        "localappdata" -> if (SystemInfo.isWindows) System.getenv("LOCALAPPDATA")
        else userHome()?.let { "$it${sep}.local${sep}share" }

        "userpf" -> if (SystemInfo.isWindows)
            System.getenv("USERPROFILE")
                ?.let { "$it${sep}AppData${sep}Roaming${sep}Microsoft${sep}Windows${sep}Start Menu${sep}Programs" }
        else null

        "win" -> if (SystemInfo.isWindows) System.getenv("WINDIR") else null
        "sys" -> if (SystemInfo.isWindows) System.getenv("WINDIR")?.let { "$it${sep}System32" } else null
        "sysnative" -> if (SystemInfo.isWindows) System.getenv("WINDIR")?.let { "$it${sep}Sysnative" } else null
        "syswow64" -> if (SystemInfo.isWindows) System.getenv("WINDIR")?.let { "$it${sep}SysWOW64" } else null
        "sd" -> if (SystemInfo.isWindows) System.getenv("SystemDrive") else null
        "pf", "pf64", "commonpf", "commonpf64" ->
            if (SystemInfo.isWindows) System.getenv("ProgramFiles") else null

        "pf32", "commonpf32" ->
            if (SystemInfo.isWindows)
                System.getenv("ProgramFiles(x86)") ?: System.getenv("ProgramFiles")
            else null

        "cf", "cf64", "commoncf", "commoncf64" ->
            if (SystemInfo.isWindows) System.getenv("CommonProgramFiles") else null

        "cf32", "commoncf32" ->
            if (SystemInfo.isWindows)
                System.getenv("CommonProgramFiles(x86)") ?: System.getenv("CommonProgramFiles")
            else null

        // Install-time or runtime constants — unknowable at edit time.
        "app", "group", "autopf", "autopf32", "autopf64", "autocf", "autocf32", "autocf64",
        "autoappdata", "fonts", "dao", "dotnet11", "dotnet20", "sendto", "userdesktop",
        "commonstartmenu", "commonstartup", "commondesktop", "userstartmenu", "userstartup",
        "userprograms", "templates", "commontemplates", "commonappdata",
        "hwnd", "wizardhwnd", "language", "srcexe", "uninstallexe", "cmd",
        "computername", "username", "groupname", "sysuserinfoname", "sysuserinfoorg",
        "userfavorites", "internet_cache", "cookies", "history", "regfile" -> null

        else -> null
    }

    private val sep: Char get() = File.separatorChar

    private fun userHome(): String? = System.getProperty("user.home")
}
