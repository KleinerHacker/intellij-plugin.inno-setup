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

import com.github.jk1.license.render.ReportRenderer
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.SignPluginTask
import java.time.Duration

// :plugin — the publishable IntelliJ plugin. Aggregates the IDE features (completion, find usages, refactoring,
// structure view, documentation, intentions, inlay hints, reference searchers), the build/run integration and
// the settings UI, and embeds the two language content modules. Depends on :language:script (→ preprocessor).
//
// Plugins applied WITHOUT a version are already on the build classpath via buildSrc (kotlin.jvm, intellij
// platform); the rest are versioned here.
plugins {
    id("org.jetbrains.kotlin.jvm")
    id("org.jetbrains.intellij.platform")
    id("org.jetbrains.changelog") version "2.5.0"
    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.8"
    id("com.github.jk1.dependency-license-report") version "3.1.4"
    id("org.cyclonedx.bom") version "3.2.4"
    id("app.cash.licensee") version "1.14.1"
}

kotlin {
    jvmToolchain(21)
}

intellijPlatform {
    instrumentCode = false

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "261"
            untilBuild = provider { null }   // unbounded: covers 2026.1 (261) and future IDEs
        }
        // Release-time changelog injection is wired separately; keep patchPluginXml off the changelog
        // provider so it stays configuration-cache friendly.
        changeNotes = provider { "" }
    }

    signing {
        System.getenv("KEYSTORE_FILE")?.takeIf { it.isNotBlank() }?.let { keyStore = file(it) }
        keyStoreType = "PKCS12"
        System.getenv("KEY_ALIAS")?.takeIf { it.isNotBlank() }?.let { keyStoreKeyAlias = it }

        val pwFile = System.getenv("KEYSTORE_PASSWORD_FILE")?.takeIf { it.isNotBlank() }
        keyStorePassword = if (pwFile != null) {
            providers.fileContents(layout.file(provider { File(pwFile) })).asText.map { it.trim() }
        } else {
            providers.environmentVariable("KEYSTORE_PASSWORD")
        }
    }

    publishing {
        val tokenFile = System.getenv("PUBLISH_TOKEN_FILE")?.takeIf { it.isNotBlank() }
        token = if (tokenFile != null) {
            providers.fileContents(layout.file(provider { File(tokenFile) })).asText.map { it.trim() }
        } else {
            providers.environmentVariable("PUBLISH_TOKEN")
        }
        channels = listOf(providers.gradleProperty("publishChannel").getOrElse("default"))
    }
}

dependencies {
    testImplementation("junit:junit:4.13.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.22.1")

    intellijPlatform {
        val localIdePath = (providers.gradleProperty("localIdePath").orNull
            ?: providers.environmentVariable("LOCAL_IDE_PATH").orNull)?.takeIf { it.isNotBlank() }
        if (localIdePath != null) {
            local(localIdePath)
        } else {
            intellijIdea("2025.3.5")
        }
        testFramework(TestFrameworkType.Platform)
    }

    // Bundle the language modules as regular libraries into the plugin (one shared classloader). Each
    // module ships its own plugin.xml fragment (META-INF/inno-setup-*.xml) that the main plugin.xml pulls
    // in via <xi:include>. :language:script brings :language:preprocessor transitively.
    implementation(project(":language:script"))
    implementation(project(":language:preprocessor"))
}

kover {
    reports {
        filters {
            excludes {
                annotatedBy("org.pcsoft.intellij.plugin.inno_setup.Generated")
            }
        }
    }
}

licenseReport {
    outputDir = layout.buildDirectory.dir("licences").get().asFile.absolutePath
    configurations = arrayOf("runtimeClasspath")
    renderers = arrayOf<ReportRenderer>(
        com.github.jk1.license.render.JsonReportRenderer(),
        com.github.jk1.license.render.SimpleHtmlReportRenderer()
    )
}

plugins.withId("org.jetbrains.kotlin.jvm") {
    plugins.withId("app.cash.licensee") {
        extensions.configure<app.cash.licensee.LicenseeExtension> {
            listOf("Apache-2.0").forEach(::allow)
        }
    }
}

tasks {
    register<SignPluginTask>("selfSignPlugin") {
        val signPluginTask = named<SignPluginTask>("signPlugin")
        group = "intellij platform"
        description = "Sign the plugin locally with the project's own PKCS#12 keystore (.signing/)."

        archiveFile.set(signPluginTask.flatMap { it.archiveFile })
        signedArchiveFile.set(signPluginTask.flatMap { it.signedArchiveFile })
        zipSignerExecutable.set(signPluginTask.flatMap { it.zipSignerExecutable })

        keyStore.set(layout.projectDirectory.file(".signing/keystore.p12"))
        keyStoreType.set("PKCS12")
        keyStoreKeyAlias.set(
            providers.gradleProperty("keyAlias")
                .orElse(providers.environmentVariable("KEY_ALIAS"))
                .orElse("inno-setup")
        )
        keyStorePassword.set(
            providers.gradleProperty("keyPassword")
                .orElse(providers.environmentVariable("KEYSTORE_PASSWORD"))
        )
    }

    test {
        jvmArgs(
            "-Didea.log.config.file=idea/log4j.xml",
            "-Didea.log.level=OFF",
        )
        timeout.set(Duration.ofMinutes(15))
    }
}
