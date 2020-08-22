import org.jetbrains.changelog.closure

plugins {
    id("org.jetbrains.intellij")
    id("org.jetbrains.changelog").version("0.4.0")
    kotlin("jvm")
}

intellij {
    setPlugins("android", "java")
    localPath = "/Applications/Android Studio.app/"
    updateSinceUntilBuild = false
}

tasks {
    patchPluginXml {
        changeNotes(closure { changelog.getLatest().toHTML() })
    }
}

changelog {
    version = "${project.version}"
    path = "${project.projectDir}/CHANGELOG.md"
    headerFormat = "[{0}]"
    headerArguments = listOf("${project.version}")
    itemPrefix = "-"
    keepUnreleasedSection = true
    unreleasedTerm = "[Unreleased]"
    groups = listOf("Added", "Changed", "Deprecated", "Removed", "Fixed", "Security")
}

dependencies {

}