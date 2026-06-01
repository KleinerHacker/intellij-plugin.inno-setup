import com.github.jk1.license.render.ReportRenderer
import org.jetbrains.intellij.platform.gradle.TestFrameworkType

plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.changelog)
    id("org.jetbrains.intellij.platform")  // version managed by settings plugin
    id("org.jetbrains.dokka") version "2.2.0"
    id("com.github.jk1.dependency-license-report") version "2.5"
    id("app.cash.licensee") version "1.14.1" apply false
}

// Read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html
dependencies {
    testImplementation(libs.junit)

    compileOnly(libs.jackson.yaml)
    compileOnly(libs.jackson.kotlin)
    testImplementation(libs.jackson.yaml)
    testImplementation(libs.jackson.kotlin)

    // IntelliJ Platform Gradle Plugin Dependencies Extension - read more: https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin-dependencies-extension.html
    intellijPlatform {
        intellijIdea(libs.versions.idea.get())
        testFramework(TestFrameworkType.Platform)

        // Add plugin dependencies for compilation here:
        bundledPlugin("org.jetbrains.kotlin")
        bundledPlugin("com.intellij.modules.json")
        bundledPlugin("org.jetbrains.plugins.yaml")
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
                    // permissive
                    "Apache-2.0",
                    "MIT",
                    "BSD-2-Clause",
                    "BSD-3-Clause",
                    "ISC",

                    // weitere permissive
                    "Unlicense",
                    "Zlib",
                    "0BSD",

                    // bewusst erlaubte weak copyleft
                    "MPL-2.0",
                    "LGPL-2.1",
                    "LGPL-3.0",

                    // ecosystem
                    "CDDL-1.0",
                    "CDDL-1.1",
                    "EPL-1.0",
                    "EPL-2.0",

                    "Unlicense",
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
        description = "copy licence report to MK Docs"
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

    //region MK Docs
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
        description = "Install mkdocs-material"
        workingDir = file("docs")
        commandLine("python", "-m", "pip", "install", "ghp-import")
    }

    register("installDocs") {
        group = "MKDocs"
        description = "Install mkdocs"

        dependsOn("installMkDocs")
        dependsOn("installMkDocsMaterial")
        dependsOn("installGitHubPages")
    }

    register<Exec>("runDocs") {
        group = "MKDocs"
        description = "Run mkdocs serve and open browser"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "serve", "-o", "-w", ".", "-w", "./docs")

        dependsOn("installDocs")
        dependsOn("copyDokka")
        dependsOn("copyLicenceReport")

        finalizedBy("deleteDokka")
        finalizedBy("deleteLicenceReport")
    }

    register<Exec>("deployDocs") {
        group = "MKDocs"
        description = "Deploy mkdocs to gh-pages"
        workingDir = file("docs")
        commandLine("python", "-m", "mkdocs", "gh-deploy", "--force")

        dependsOn("installDocs")
        dependsOn("copyDokka")
        dependsOn("copyLicenceReport")

        finalizedBy("deleteDokka")
        finalizedBy("deleteLicenceReport")
    }
    //endregion
}
