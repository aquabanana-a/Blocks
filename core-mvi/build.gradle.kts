plugins {
    id("common-android")
    id("kotlin-kapt")
}

dependencies {

    implementation(project(":core"))
    implementation(project(":core-navigation"))

    implementation(Deps.navigationLibraries)

    implementation(Deps.AndroidX.fragment)
    implementation(Deps.AndroidX.fragmentKtx)
    implementation(Deps.AndroidX.lifecycle)
    implementation(Deps.AndroidX.lifecycleCommon)

    implementation(Deps.Dagger.dagger)

    // Unit tests
    testImplementation(Deps.testLibraries)
}