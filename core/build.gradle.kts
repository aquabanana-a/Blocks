plugins {
    id("common-android")
    id("kotlin-kapt")
}

dependencies {

    implementation(Deps.Dagger.dagger)
    implementation(Deps.AndroidX.fragment)
    kapt(Deps.Dagger.daggerCompiler)
}