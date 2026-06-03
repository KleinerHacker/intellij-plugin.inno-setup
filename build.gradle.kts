import com.github.jk1.license.render.ReportRenderer
import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    id("org.jetbrains.kotlin.jvm") version "2.2.20"
    id("org.jetbrains.changelog") version "2.5.0"
    id("org.jetbrains.grammarkit") version "2023.3.0.3"
    id("org.jetbrains.intellij.platform")  // version managed by settings plugin
    id("org.jetbrains.dokka") version "2.2.0"
    id("com.github.jk1.dependency-license-report") version "2.5"
    id("app.cash.licensee") version "1.14.1" apply false
}

val generatedRoot = "build/parsing/gen"
val parsingRoot = "src/main/resources/parsing"

val rootPackage = "org/pcsoft/intellij/plugin/inno_setup"
val languagePackage = "$rootPackage/language"

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    testImplementation("junit:junit:4.13.2")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.17.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.17.2")

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        intellijIdea("2025.3.5")
        testFramework(TestFrameworkType.Platform)

        // Kotlin plugin needed for compilation of the optional K2-compatibility extension
        bundledPlugin("org.jetbrains.kotlin")
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

subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        apply(plugin = "app.cash.licensee")
        plugins.withId("app.cash.licensee") {
            extensions.configure<app.cash.licensee.LicenseeExtension> {
                listOf(
                    "Apache-2.0", "MIT", "BSD-2-Clause", "BSD-3-Clause", "ISC",
                    "Unlicense", "Zlib", "0BSD",
                    "MPL-2.0", "LGPL-2.1", "LGPL-3.0",
                    "CDDL-1.0", "CDDL-1.1", "EPL-1.0", "EPL-2.0",
                    "CC0-1.0",
                ).forEach(::allow)

                allowUrl("https://opensource.org/license/mit")
            }
        }
    }
}

tasks {
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
    register<Exec>("installMkDocs") {
        group = null
        description = "Install mkdocs"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "mkdocs")
    }

    register<Exec>("installMkDocsMaterial") {
        group = null
        description = "Install mkdocs-material"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "mkdocs-material")
    }

    register<Exec>("installGitHubPages") {
        group = null
        description = "Install ghp-import"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "ghp-import")
    }

    register("installDocs") {
        group = "MKDocs"
        description = "Install mkdocs and dependencies"
        dependsOn("installMkDocs")
        dependsOn("installMkDocsMaterial")
        dependsOn("installGitHubPages")
    }

    register<Exec>("runDocs") {
        group = "MKDocs"
        description = "Run mkdocs serve and open browser"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "serve", "-o", "-w", ".", "-w", "./docs")
        dependsOn("installDocs", "copyDokka", "copyLicenceReport")
        finalizedBy("deleteDokka", "deleteLicenceReport")
    }

    register<Exec>("deployDocs") {
        group = "MKDocs"
        description = "Deploy mkdocs to gh-pages"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "gh-deploy", "--force")
        dependsOn("installDocs", "copyDokka", "copyLicenceReport")
        finalizedBy("deleteDokka", "deleteLicenceReport")
    }
    //endregion

    //region Grammar-Kit
// Pre-create output directories so Grammar-Kit's lazy Provider<Directory> properties resolve
// during Gradle 9 strict task validation.

    register<GenerateParserTask>("generateIssParser") {
        sourceFile.set(file("$parsingRoot/IssGrammar.bnf"))
        targetRootOutputDir.set(file(generatedRoot))
        pathToParser.set("$languagePackage/parsing/parser/IssParser.java")
        pathToPsiRoot.set("$languagePackage/parsing/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateIssLexer") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IssLexer.flex"))
        targetOutputDir.set(file("$generatedRoot/$languagePackage/parsing"))
        purgeOldFiles.set(true)
    }

    sourceSets.main {
        java.srcDir(generatedRoot)
    }

    compileJava {
        dependsOn("generateIssParser", "generateIssLexer")
    }

    compileKotlin {
        dependsOn("generateIssParser", "generateIssLexer")
    }
//endregion
}
