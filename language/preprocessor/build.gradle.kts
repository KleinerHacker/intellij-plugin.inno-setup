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

import org.jetbrains.grammarkit.tasks.GenerateLexerTask
import org.jetbrains.grammarkit.tasks.GenerateParserTask

// :language:preprocessor — the ISPP preprocessor language (lexer/parser/PSI, highlighter, basic annotator,
// brace matcher, references, expression engine). The lowest layer: it depends on no other plugin module and
// is pulled in by :language:script. Carries its own plugin.xml fragment (META-INF/inno-setup-preprocessor.xml).
plugins {
    id("inno-setup.platform-module")
    id("org.jetbrains.grammarkit") version "2023.3.0.3"
    id("org.jetbrains.dokka") version "2.2.0"
    id("org.jetbrains.kotlinx.kover") version "0.9.8"
}

val parsingRoot = "src/main/resources/parsing"
val preprocessorPackage = "org/pcsoft/intellij/plugin/inno_setup/language/parser/preprocessor"

tasks {
    register<GenerateParserTask>("generateIsPreprocessorParser") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsPreprocessorGrammar.bnf"))
        targetRootOutputDir.set(layout.buildDirectory.dir("generated"))
        pathToParser.set("$preprocessorPackage/IsPreprocessorParser.java")
        pathToPsiRoot.set("$preprocessorPackage/psi")
        purgeOldFiles.set(true)
    }

    register<GenerateLexerTask>("generateIsPreprocessorLexer") {
        sourceFile.set(layout.projectDirectory.file("$parsingRoot/IsPreprocessorLexer.flex"))
        targetOutputDir.set(layout.buildDirectory.dir("generated/$preprocessorPackage"))
        purgeOldFiles.set(true)
    }

    compileJava {
        dependsOn("generateIsPreprocessorLexer", "generateIsPreprocessorParser")
    }

    compileKotlin {
        dependsOn("generateIsPreprocessorLexer", "generateIsPreprocessorParser")
    }
}

sourceSets.main {
    java.srcDir(layout.buildDirectory.dir("generated"))
}
