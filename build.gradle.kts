plugins {
    id("io.gitlab.arturbosch.detekt") version (Deps.Versions.detekt)
}
buildscript {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }

    dependencies {
        classpath("com.android.tools.build:gradle:${Deps.Versions.buildToolsVersion}")
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Deps.Kotlin.Versions.kotlin}")
        classpath("com.google.firebase:firebase-crashlytics-gradle:${Deps.Versions.crashlyticsGradle}")
        classpath("org.jetbrains.kotlin:kotlin-serialization:${Deps.Kotlin.Versions.kotlin}")
        // TODO: Update google-services.json to actual!
        classpath("com.google.gms:google-services:${Deps.Versions.googleServices}")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

detekt {
    toolVersion = Deps.Versions.detekt
    source = files(
        "src/main/kotlin",
        "src/main/java",
    )
    config = files("detekt_config.yml")
    ignoreFailures = false // if `true` build does not fail when the maxIssues count was reached
    buildUponDefaultConfig = true
    parallel = true
    allRules = true
}