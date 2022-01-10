import org.gradle.api.artifacts.dsl.DependencyHandler

object Deps {

    object Versions {
        const val detekt = "1.19.0"
        const val buildToolsVersion = "7.0.4"
        const val crashlyticsGradle = "2.4.1"
        const val googleServices = "4.3.4"
    }

    object Kotlin {

        object Versions {
            const val kotlin = "1.6.0"
            const val kotlinCoroutines = "1.5.2"
            const val kotlinSerialization = "1.3.1"
        }

        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlin}"
        const val stdlibCommon = "org.jetbrains.kotlin:kotlin-stdlib-common:$${Versions.kotlin}"
        const val stdlib_jdk = "org.jetbrains.kotlin:kotlin-stdlib-jdk8:$${Versions.kotlin}"
        const val coroutines_core =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.kotlinCoroutines}"
        const val coroutines_android =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.kotlinCoroutines}"
        const val serializationCore =
            "org.jetbrains.kotlinx:kotlinx-serialization-core:${Versions.kotlinSerialization}"
        const val serializationJson =
            "org.jetbrains.kotlinx:kotlinx-serialization-json:${Versions.kotlinSerialization}"
    }

    object AndroidX {
        object Versions {
            const val appcompat = "1.4.0"
            const val material = "1.3.0"
            const val annotations = "1.2.0"
            const val coreKtx = "1.5.0"
            const val recyclerView = "1.2.1"
            const val constraintLayout = "2.1.2"
            const val fragment = "1.4.0"
            const val swipeRefreshLayout = "1.1.0"
            const val dataStore = "1.0.0"
            const val lifecycle = "2.2.0"
        }

        const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
        const val dataStore = "androidx.datastore:datastore-preferences:${Versions.dataStore}"
        const val annotations = "androidx.annotation:annotation:${Versions.annotations}"
        const val coreKtx = "androidx.core:core-ktx:${Versions.coreKtx}"
        const val recyclerView = "androidx.recyclerview:recyclerview:${Versions.recyclerView}"
        const val constraintLayout =
            "androidx.constraintlayout:constraintlayout:${Versions.constraintLayout}"
        const val material = "com.google.android.material:material:${Versions.material}"
        const val fragment = "androidx.fragment:fragment:${Versions.fragment}"
        const val fragmentKtx = "androidx.fragment:fragment-ktx:${Versions.fragment}"
        const val swipeRefreshLayout =
            "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swipeRefreshLayout}"
        const val lifecycle = "androidx.lifecycle:lifecycle-extensions:${Versions.lifecycle}"
        const val lifecycleCommon = "androidx.lifecycle:lifecycle-common-java8:${Versions.lifecycle}"
    }

    object Retrofit {
        object Versions {
            const val retrofit = "2.9.0"
            const val kotlinSerializationConverter = "0.8.0"
        }

        val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        val kotlinSerializationConverter =
            "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.kotlinSerializationConverter}"
    }

    object Navigation {
        object Versions {
            const val navigation = "2.3.5"
        }

        const val navigationFragment =
            "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
        const val navigationUi = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
        const val navigationFeatures =
            "androidx.navigation:navigation-dynamic-features-fragment:${Versions.navigation}"
    }

    object Dagger {
        object Versions {
            const val dagger = "2.40.5"
        }

        const val dagger = "com.google.dagger:dagger:${Versions.dagger}"
        const val daggerSupport = "com.google.dagger:dagger-android-support:${Versions.dagger}"
        const val daggerCompiler = "com.google.dagger:dagger-compiler:${Versions.dagger}"
        const val daggerAndroidProcessor = "com.google.dagger:dagger-android-processor:${Versions.dagger}"
    }

    object Firebase {
        object Versions {
            const val firebaseBom = "29.0.3"
        }

        const val firebaseBom = "com.google.firebase:firebase-bom:29.0.3:${Versions.firebaseBom}"
        const val crashlytics = "com.google.firebase:firebase-crashlytics-ktx"
        const val messaging = "com.google.firebase:firebase-messaging-ktx"
    }

    object Test {
        object Versions {
            const val junit = "4.13"
            const val junitExt = "1.1.1"
        }

        const val junit = "junit:junit:${Versions.junit}"
        const val junitExt = "androidx.test.ext:junit:${Versions.junitExt}"
    }

    object OkHttp {
        object Versions {
            const val okhttp = "4.9.1"
        }

        const val runtime = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
    }

    object RxJava {
        object Versions {
            const val runtime = "3.1.3"
            const val rxandroid = "3.0.0"
        }

        const val runtime = "io.reactivex.rxjava3:rxjava:${Versions.runtime}"
        const val rxandroid = "io.reactivex.rxjava3:rxandroid:${Versions.rxandroid}"
    }

    object Other {
        object Versions {
            const val coil = "1.1.0"
            const val lokalise = "2.0.1-lite"
            const val timber = "5.0.1"
        }

        const val coil = "io.coil-kt:coil:${Versions.coil}"
        const val lokalise = "com.lokalise.android:sdk:${Versions.lokalise}"
        const val timber = "com.jakewharton.timber:timber:${Versions.timber}"
    }

    val kotlinLibraries = arrayListOf<String>().apply {
        add(Kotlin.stdlib)
        add(Kotlin.stdlib_jdk)
        add(Kotlin.coroutines_core)
        add(Kotlin.coroutines_android)
        add(Kotlin.serializationJson)
    }

    val navigationLibraries = arrayListOf<String>().apply {
        add(Navigation.navigationFragment)
        add(Navigation.navigationUi)
        add(Navigation.navigationFeatures)
    }

    val appLibraries = arrayListOf<String>().apply {
        add(AndroidX.appcompat)
        add(AndroidX.annotations)
        add(AndroidX.coreKtx)
        add(AndroidX.recyclerView)
        add(AndroidX.constraintLayout)
        add(AndroidX.material)
        add(AndroidX.fragment)
        add(AndroidX.fragmentKtx)
        add(AndroidX.swipeRefreshLayout)
        add(Retrofit.retrofit)
        add(Retrofit.kotlinSerializationConverter)
        add(Other.coil)
        add(Other.timber)
        add(AndroidX.dataStore)
        add(Navigation.navigationFragment)
        add(Navigation.navigationUi)
        add(Navigation.navigationFeatures)
        add(OkHttp.runtime)
    }

    val testLibraries = arrayListOf<String>().apply {
        add(Test.junit)
        add(Test.junitExt)
    }
}

fun DependencyHandler.kapt(list: List<String>) {
    list.forEach { dependency ->
        add("kapt", dependency)
    }
}

fun DependencyHandler.kapt(library: String) {
    add("kapt", library)
}

fun DependencyHandler.implementation(list: List<String>) {
    list.forEach { dependency ->
        add("implementation", dependency)
    }
}

fun DependencyHandler.androidTestImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("androidTestImplementation", dependency)
    }
}

fun DependencyHandler.testImplementation(list: List<String>) {
    list.forEach { dependency ->
        add("testImplementation", dependency)
    }
}