plugins {
    id("common-android")
}

dependencies {

    // Support
    implementation(Deps.AndroidX.appcompat)
    implementation(Deps.AndroidX.annotations)

    // Lokalise
    implementation(Deps.Other.lokalise)

    // Unit tests
    testImplementation(Deps.testLibraries)
}

android {
    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }
}