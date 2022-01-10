import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.android.application")
    id("kotlin-android")
    id("kotlin-kapt")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
    id("kotlin-parcelize")
}

val keystorePropertiesFile = rootProject.file("keystore.properties")
val keystoreProperties = Properties()
keystoreProperties.load(FileInputStream(keystorePropertiesFile))

val versionPropertiesFile = rootProject.file("version.properties")
val versionProps = Properties()
versionProps.load(FileInputStream(versionPropertiesFile))

android {
    compileSdk = Config.compileSdk
    buildToolsVersion = Config.buildToolsVersion

    val commitId = getCommitId()
    val code = getCIVersionCode() ?: (versionProps.getProperty("VERSION_CODE")).toInt()
    val major = (versionProps.getProperty("VERSION_MAJOR") ?: "0")
    val minor = (versionProps.getProperty("VERSION_MINOR") ?: "0")
    val patch = (versionProps.getProperty("VERSION_PATCH") ?: "0")
    val featureName = (versionProps.getProperty("FEATURE_NAME") ?: "")

    val name = "${major}.${minor}.${patch}"
    val tail = getBuildType() + featureName

    defaultConfig {
        applicationId = "com.fromfinalform.blocks"
        minSdk = Config.minSdk
        targetSdk = Config.targetSdk
        versionCode = code
        versionName = name

        testInstrumentationRunner = Config.androidTestInstrumentation
        vectorDrawables.useSupportLibrary = true
        testHandleProfiling = true

        val archivesBaseName =
            "Blocks-${Config.versionName}-${Config.versionCode}-${commitId}-${tail}"
        println("APK: $archivesBaseName")
    }

    signingConfigs {
        getByName("debug") {
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
        }
        create("release") {
            keyAlias = keystoreProperties.getProperty("keyAlias")
            keyPassword = keystoreProperties.getProperty("keyPassword")
            storeFile = file(keystoreProperties.getProperty("storeFile"))
            storePassword = keystoreProperties.getProperty("storePassword")
        }
    }

    buildTypes {
        getByName("debug") {
            manifestPlaceholders["appLabel"] = "Blocks-Debug"
            isDebuggable = true
            applicationIdSuffix = ".debug"
        }

        getByName("release") {
            manifestPlaceholders["appLabel"] = "Blocks"
            signingConfig = signingConfigs.findByName("release")
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
            testProguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguardTest-rules.pro"
            )
            androidResources {
                isCrunchPngs = false
            }
        }

        create("debuggable-release") {
            initWith(getByName("release"))
            isDebuggable = true
            isMinifyEnabled = false
            isShrinkResources = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }

    viewBinding {
        android.buildFeatures.viewBinding = true
    }

    lint {
        isAbortOnError = true
        lintConfig = file("${project.rootDir}/lint.xml")
    }

    testOptions {
        unitTests {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
            all {
                it.also { testTask ->
                    testTask.testLogging {
                        events("passed", "skipped", "failed", "standardOut", "standardError")
                    }
                }
            }
        }
    }
}

kapt {
    generateStubs = true
}

dependencies {
    implementation(Deps.appLibraries)

    implementation(project(":core"))
    implementation(project(":core-mvi"))
    implementation(project(":core-locale"))
    implementation(project(":core-navigation"))

    implementation(project(":common-ui"))

    implementation(Deps.Dagger.dagger)
    implementation(Deps.Dagger.daggerSupport)
    kapt(Deps.Dagger.daggerCompiler)
    kapt(Deps.Dagger.daggerAndroidProcessor)

    implementation(Deps.RxJava.runtime)
    implementation(Deps.RxJava.rxandroid)
}

fun getBuildType() =
    if (project.hasProperty("server")) {
        project.properties["server"] as String
    } else {
        "undefined"
    }

fun getCommitId() =
    if (project.hasProperty("commitId")) {
        project.properties["commitId"] as String
    } else {
        "undefined"
    }

fun getCIVersionCode() =
    if (project.hasProperty("buildVersionCode")) {
        project.properties["buildVersionCode"] as Int
    } else {
        null
    }