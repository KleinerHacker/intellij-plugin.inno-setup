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
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import java.time.Duration
import org.jetbrains.intellij.platform.gradle.TestFrameworkType
import org.jetbrains.intellij.platform.gradle.tasks.SignPluginTask

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.4.0"
    id("org.jetbrains.changelog") version "2.5.0"
    id("org.jetbrains.grammarkit") version "2023.3.0.3"
    id("org.jetbrains.intellij.platform")  // version managed by settings plugin
    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.8"
    id("com.github.jk1.dependency-license-report") version "3.1.4"
    id("org.cyclonedx.bom") version "3.2.4"
    id("app.cash.licensee") version "1.14.1"
}

val generatedRoot = "build/parsing/gen"
val parsingRoot = "src/main/resources/parsing"

val rootPackage = "org/pcsoft/intellij/plugin/inno_setup"
val languagePackage = "$rootPackage/language"
val preprocessorPackage = "$languagePackage/parser/preprocessor"
val sectionPackage = "$languagePackage/parser/section"
val templatePackage = "$languagePackage/parser/template"

intellijPlatform {
    instrumentCode = false

    pluginConfiguration {
        ideaVersion {
            sinceBuild = "261"
            untilBuild = provider { null }   // unbounded: covers 2026.1 (261) and future IDEs
        }
    }

    signing {
        // Sign from a PKCS#12 keystore. Only non-secret values (the keystore path and key alias) go
        // through env; the store password is read from a file (KEYSTORE_PASSWORD_FILE) so no secret
        // value lives in the build/signer process environment. file("") would fail at configuration
        // time, hence the presence guards.
        System.getenv("KEYSTORE_FILE")?.takeIf { it.isNotBlank() }?.let { keyStore = file(it) }
        keyStoreType = "PKCS12"
        System.getenv("KEY_ALIAS")?.takeIf { it.isNotBlank() }?.let { keyStoreKeyAlias = it }

        // Prefer the password from a file (KEYSTORE_PASSWORD_FILE); fall back to the env var.
        val pwFile = System.getenv("KEYSTORE_PASSWORD_FILE")?.takeIf { it.isNotBlank() }
        keyStorePassword = if (pwFile != null) {
            providers.fileContents(layout.file(provider { File(pwFile) })).asText.map { it.trim() }
        } else {
            providers.environmentVariable("KEYSTORE_PASSWORD")
        }
    }

    publishing {
        // Token via the in-process Marketplace API: reading it from a file keeps it out of the
        // environment entirely. Falls back to the env var.
        val tokenFile = System.getenv("PUBLISH_TOKEN_FILE")?.takeIf { it.isNotBlank() }
        token = if (tokenFile != null) {
            providers.fileContents(layout.file(provider { File(tokenFile) })).asText.map { it.trim() }
        } else {
            providers.environmentVariable("PUBLISH_TOKEN")
        }
        // Default channel "default" = stable; pre-releases go to a separate channel via -PpublishChannel=eap
        channels = listOf(providers.gradleProperty("publishChannel").getOrElse("default"))
    }
}

kotlin {
    jvmToolchain(21)
}

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    testImplementation("junit:junit:4.13.2")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.22.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.22.0")

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        // Compile/test against a local IDE installation when one is configured, otherwise download the
        // default SDK. The local path is kept out of the repository: set it per machine via the Gradle
        // property `localIdePath` (e.g. in ~/.gradle/gradle.properties or `-PlocalIdePath=…`) or the
        // env var `LOCAL_IDE_PATH`. Point it at an IDE install root (the folder with lib/, plugins/, bin/).
        val localIdePath = (providers.gradleProperty("localIdePath").orNull
            ?: providers.environmentVariable("LOCAL_IDE_PATH").orNull)?.takeIf { it.isNotBlank() }
        if (localIdePath != null) {
            local(localIdePath)
        } else {
            intellijIdea("2025.3.5")
        }
        testFramework(TestFrameworkType.Platform)
    }
}

kover {
    reports {
        filters {
            excludes {
                // Declarations annotated @Generated are intentionally uncoverable
                // (defensive / provably-unreachable code).
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
            listOf(
                "Apache-2.0",
            ).forEach(::allow)
        }
    }
}

tasks {
    //region Signing
    // Local convenience task: sign the built plugin with the project's own PKCS#12 keystore stored
    // under .signing/ (gitignored). Keystore path + alias are fixed; the keystore password is taken
    // from -PkeyPassword=… or the KEYSTORE_PASSWORD env var. (No interactive prompt: a console-reading
    // provider is not serializable by the configuration cache, which is enabled project-wide.)
    register<SignPluginTask>("selfSignPlugin") {
        val signPluginTask = named<SignPluginTask>("signPlugin")
        group = "intellij platform"
        description = "Sign the plugin locally with the project's own PKCS#12 keystore (.signing/)."

        // Reuse the wiring of the built-in signPlugin task (input archive + signer tool + output path).
        archiveFile.set(signPluginTask.flatMap { it.archiveFile })
        signedArchiveFile.set(signPluginTask.flatMap { it.signedArchiveFile })
        zipSignerExecutable.set(signPluginTask.flatMap { it.zipSignerExecutable })

        // Hard-coded keystore; never committed (see .gitignore).
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
    //endregion

    test {
        jvmArgs(
            "-Djava.awt.headless=true",
            "-Didea.log.config.file=idea/log4j.xml",
            "-Didea.log.level=OFF",
        )
        // Hard backstop so a hung test can never stall the whole build indefinitely. Per-method timeouts are
        // enforced in-JVM by IsTimedBasePlatformTestCase/IsTimedTestCase; this only catches a non-interruptible
        // runaway (e.g. a busy loop) that the watchdog cannot abort.
        timeout.set(Duration.ofMinutes(5))
    }

    //region Dokka
    register<Copy>("copyDokka") {
        group = "dokka"
        description = "Copy all Dokka to MkDocs"
        from(File("build/dokka"))
        into(File("docs/docs/dokka"))
        dependsOn("dokkaGeneratePublicationHtml")
    }

    register<Delete>("deleteDokka") {
        group = "dokka"
        description = "Delete Dokka"
        delete(File("docs/docs/dokka"))
    }
    //endregion

    //region Licencing
    register<Copy>("copyLicenceReport") {
        group = "licencing"
        description = "Copy licence report to MkDocs"
        from(File("build/licences"))
        into(File("docs/docs/licences"))
        dependsOn("generateLicenseReport")
    }

    register<Delete>("deleteLicenceReport") {
        group = "licencing"
        description = "Delete licence report"
        delete(File("docs/docs/licences"))
    }
    //endregion

    //region MkDocs
    // mike spawns `mkdocs` as a subprocess; on Windows the Python Scripts dir
    // (where mkdocs.exe lives) is often not on PATH. Resolve it once and prepend
    // it to PATH for the mike tasks. In CI (setup-python) it is already on PATH.
    val pythonScriptsDir: String? by lazy {
        runCatching {
            providers.exec {
                commandLine("python", "-c", "import sysconfig; print(sysconfig.get_path('scripts'))")
            }.standardOutput.asText.get().trim().ifEmpty { null }
        }.getOrNull()
    }
    fun Exec.withMikePath() {
        pythonScriptsDir?.let { dir ->
            environment("PATH", dir + File.pathSeparator + System.getenv("PATH"))
        }
    }

    register<Exec>("installMkDocs") {
        group = null
        description = "Install mkdocs"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "mkdocs")
    }

    register<Exec>("installMkDocsMaterial") {
        group = null
        description = "Install mkdocs-material"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "mkdocs-material")
    }

    register<Exec>("installGitHubPages") {
        group = null
        description = "Install ghp-import"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "ghp-import")
    }

    register<Exec>("installMike") {
        group = null
        description = "Install mike for versioned docs deployment"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "mike")
    }

    register<Exec>("installI18N") {
        group = null
        description = "Install i18n"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "--upgrade", "mkdocs-static-i18n")
    }

    register("installDocs") {
        group = "MKDocs"
        description = "Install mkdocs and dependencies"
        dependsOn("installMkDocs")
        dependsOn("installMkDocsMaterial")
        dependsOn("installGitHubPages")
        dependsOn("installI18N")
        dependsOn("installMike")
    }

    register<Exec>("runDocs") {
        group = "MKDocs"
        description = "Run mkdocs serve and open browser (no version selector — that only appears on the deployed site)"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "serve", "-o", "-w", ".", "-w", "./docs")
        dependsOn("installDocs", "copyDokka", "copyLicenceReport")
        finalizedBy("deleteDokka", "deleteLicenceReport")
    }

    register<Exec>("buildDocs") {
        group = "MKDocs"
        description = "Build the mkdocs site into build/docs (per mkdocs.yml site_dir; no serve, no deploy) — usable as a generation test"
        workingDir = file("docs")
        // --strict fails the build on warnings (broken links, missing pages …) so it acts as a test;
        // --clean wipes the previous output first.
        commandLine("python", "-m", "mkdocs", "build", "--clean", "--strict")
        dependsOn("installDocs", "copyDokka", "copyLicenceReport")
        finalizedBy("deleteDokka", "deleteLicenceReport")
    }

    register<Exec>("deployDocs") {
        group = "MKDocs"
        description = "Deploy a versioned docs snapshot via mike. Requires -Pversion=<tag> and a pre-configured git push target."
        workingDir = file("docs")
        val ver = (project.findProperty("version") as String?)
            ?: error("Pass -Pversion=<tag> to deployDocs")
        val setLatest = (project.findProperty("setLatest") as String?) != "false"
        val args = buildList {
            add("python"); add("-c"); add("from mike.driver import main; main()"); add("deploy"); add("--push")
            // Materialise the 'latest' alias as a full copy, not mike's default symlink:
            // GitHub Pages does not resolve git symlinks reliably, and the gh-pages root
            // redirect points at latest/, so it must be a real directory.
            if (setLatest) { add("--alias-type"); add("copy"); add("--update-aliases"); add(ver); add("latest") } else add(ver)
        }
        commandLine(args)
        withMikePath()
        dependsOn("installDocs", "copyDokka", "copyLicenceReport")
        finalizedBy("deleteDokka", "deleteLicenceReport")
    }

    register<Exec>("setDefaultDocs") {
        group = "MKDocs"
        description = "Set the default docs version shown at the root URL via mike (run once after the first release deploy)."
        workingDir = file("docs")
        commandLine("python", "-c", "from mike.driver import main; main()", "set-default", "--push", "latest")
        withMikePath()
        dependsOn("installDocs")
    }
    //endregion

    //region Grammar-Kit

    register<GenerateParserTask>("generateIsSectionParser") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsSectionGrammar.bnf"))
        targetRootOutputDir.set(layout.projectDirectory.dir(generatedRoot))
        pathToParser.set("$sectionPackage/IsSectionParser.java")
        pathToPsiRoot.set("$sectionPackage/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateIsSectionLexer") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsSectionLexer.flex"))
        targetOutputDir.set(file("$generatedRoot/$sectionPackage"))
        purgeOldFiles.set(true)
    }

    register<GenerateParserTask>("generateIsPreprocessorParser") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsPreprocessorGrammar.bnf"))
        targetRootOutputDir.set(layout.projectDirectory.dir(generatedRoot))
        pathToParser.set("$preprocessorPackage/IsPreprocessorParser.java")
        pathToPsiRoot.set("$preprocessorPackage/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateIsPreprocessorLexer") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsPreprocessorLexer.flex"))
        targetOutputDir.set(file("$generatedRoot/$preprocessorPackage"))
        purgeOldFiles.set(true)
    }

    register<GenerateParserTask>("generateIsTemplateParser") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsTemplateGrammar.bnf"))
        targetRootOutputDir.set(layout.projectDirectory.dir(generatedRoot))
        pathToParser.set("$templatePackage/IsTemplateParser.java")
        pathToPsiRoot.set("$templatePackage/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateIsTemplateLexer") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsTemplateLexer.flex"))
        targetOutputDir.set(file("$generatedRoot/$templatePackage"))
        purgeOldFiles.set(true)
    }

    // Aggregator tasks: generate all lexers / all parsers as a single unit, plus an umbrella.
    register("generateLexers") {
        group = "grammar-kit"
        description = "Generate all JFlex lexers (Section + Preprocessor + Template)"
        dependsOn("generateIsSectionLexer", "generateIsPreprocessorLexer", "generateIsTemplateLexer")
    }

    register("generateParsers") {
        group = "grammar-kit"
        description = "Generate all Grammar-Kit parsers/PSI (Section + Preprocessor + Template)"
        dependsOn("generateIsSectionParser", "generateIsPreprocessorParser", "generateIsTemplateParser")
    }

    // Wipes the whole generated tree before a full regeneration. Catches stale files left
    // behind by renamed/removed grammar packages (purgeOldFiles only cleans a task's *current*
    // output package, not folders that are no longer generated at all).
    register<Delete>("cleanGeneratedSources") {
        group = "grammar-kit"
        description = "Delete all generated lexer/parser sources (incl. stale renamed packages)"
        delete(generatedRoot)
    }

    register("generateSources") {
        group = "grammar-kit"
        description = "Generate all lexers and parsers (from a clean slate)"
        dependsOn("cleanGeneratedSources", "generateLexers", "generateParsers")
    }

    // When a full regeneration is requested, every generator must run *after* the wipe.
    // Routine compiles depend on the generators directly (see below), so the wipe is not in
    // their graph and incremental up-to-date checks keep working.
    listOf(
        "generateIsSectionParser",
        "generateIsSectionLexer",
        "generateIsPreprocessorParser",
        "generateIsPreprocessorLexer",
        "generateIsTemplateParser",
        "generateIsTemplateLexer"
    ).forEach { taskName ->
        named(taskName) { mustRunAfter("cleanGeneratedSources") }
    }

    sourceSets.main {
        java.srcDir(generatedRoot)
    }

    compileJava {
        dependsOn("generateLexers", "generateParsers")
    }

    compileKotlin {
        dependsOn("generateLexers", "generateParsers")
    }
//endregion
}
