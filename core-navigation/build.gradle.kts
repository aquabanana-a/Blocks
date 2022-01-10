plugins {
    id("common-android")
}

dependencies {

    //implementation(Deps.kotlinLibraries)
    implementation(Deps.navigationLibraries)

    implementation(Deps.AndroidX.fragment)
    implementation(Deps.AndroidX.fragmentKtx)

    // Unit tests
    testImplementation(Deps.testLibraries)
}