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
import org.jetbrains.intellij.platform.gradle.IntelliJPlatformType
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
    id("org.jetbrains.kotlinx.kover") version "0.9.9"
    id("com.github.jk1.dependency-license-report") version "3.1.4"
    id("org.cyclonedx.bom") version "3.3.0"
    id("app.cash.licensee") version "1.14.1"
}

kotlin {
    // See the convention plugin (inno-setup.platform-module): compile on JDK 25 for the Java 25 platform
    // jars of IntelliJ 2026.2, but emit Java 21 bytecode for load compatibility across the supported range.
    jvmToolchain(25)
    compilerOptions {
        jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_21)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

intellijPlatform {
    instrumentCode = false

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "262"
            untilBuild = provider { null }   // unbounded: covers 2026.2 (262) and future IDEs
        }
        // Release-time changelog injection is wired separately; keep patchPluginXml off the changelog
        // provider so it stays configuration-cache friendly.
        changeNotes = provider { "" }
    }

    // The plugin declares sinceBuild=262 with an unbounded untilBuild, so it is offered to *every* 262+
    // IDE — not just IntelliJ IDEA. Verifying only the development IDE would let a break in a sibling
    // product surface in the marketplace instead of in the build. These four cover the IDEs the plugin is
    // meant to support; the version is taken from the catalog so it can never drift from the platform the
    // plugin is compiled against. See .claude/rules/plugin-verification.md — the list MUST be kept current.
    pluginVerification {
        ides {
            val verificationVersion = libs.versions.idea.get()
            listOf(
                IntelliJPlatformType.IntellijIdea,
                IntelliJPlatformType.Rider,
                IntelliJPlatformType.CLion,
                IntelliJPlatformType.GoLand,
            ).forEach { create(it, verificationVersion) }
        }
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
    // See inno-setup.platform-module: pin transitive Kotlin artifacts to the catalog version so an older
    // stdlib (pulled via jackson-module-kotlin → kotlin-reflect) cannot crash the platform's coroutine
    // debug probes with a "Debug metadata version mismatch" and hang the tests.
    constraints {
        implementation(libs.kotlin.stdlib)
        implementation(libs.kotlin.reflect)
    }

    testImplementation("junit:junit:4.13.2")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.1")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.22.1")

    intellijPlatform {
        val localIdePath = (providers.gradleProperty("localIdePath").orNull
            ?: providers.environmentVariable("LOCAL_IDE_PATH").orNull)?.takeIf { it.isNotBlank() }
        if (localIdePath != null) {
            local(localIdePath)
        } else {
            intellijIdea(libs.versions.idea.get())
        }
        testFramework(TestFrameworkType.Platform)
        // Since 2026.2 the platform is split into content modules: StructureAwareNavBarModelExtension
        // (used by IsStructureAwareNavbar) now ships in the separate intellij.platform.structureView
        // module, which must be requested explicitly instead of coming in via the monolithic classpath.
        bundledModule("intellij.platform.structureView")
        // Also since 2026.2: core intellij.spellchecker(.xml) — pulled in transitively via
        // com.intellij.modules.lang — now needs intellij.libraries.lucene.common, which was moved into the
        // bundled intellij.libraries.misc.plugin. Without it the test plugin is excluded and every feature
        // test fails with no language support. (See the same note in the :language convention.)
        bundledPlugin("intellij.libraries.misc.plugin")
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
        // Platform tests log through java.util.logging (JUL) via TestLoggerFactory — NOT log4j. See the
        // detailed explanation in the :language convention (inno-setup.platform-module.gradle.kts).
        systemProperty("intellij.console.log.level", "off")
        systemProperty("idea.log.config.file", "${rootDir}/gradle/test-logging.properties")
        systemProperty("idea.split.test.logs", "true")
        timeout.set(Duration.ofMinutes(15))
    }
}
