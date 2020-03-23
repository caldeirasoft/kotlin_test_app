plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.3.70"
}

androidExtensions { isExperimental = true }
android {
    compileSdkVersion(AndroidSdk.compile)
    defaultConfig {
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)
    }
}
/*
    experimental = true // Parcelize support
}
*/
dependencies {
    api(project(":domain"))
    // Android
    implementation(deps.androidx.appcompat)
    // Kotlin
    implementation(deps.kotlin.stdlib)
    implementation(deps.kotlin.reflect)
    implementation(deps.kotlin.coroutines.core)
    // Kotlinx serialization
    implementation(deps.kotlinx.serialization)
    // Android-LiveData
    implementation(deps.androidx.lifecycle.extensions)
    kapt(deps.androidx.lifecycle.compiler)
    // Android-Paging
    implementation(deps.androidx.paging.common)
    // Android-Room
    implementation(deps.androidx.room.runtime)
    implementation(deps.androidx.room.ktx)
    kapt(deps.androidx.room.compiler)
    // Kotlin XML databinding DSL
    implementation(deps.xmlBind)
    // ThreeTenABP (Java Time)
    implementation(deps.threeten)
    // Moshi
    implementation(deps.moshi.moshi)
    implementation(deps.moshi.kotlin)
    // Retrofit
    implementation(deps.retrofit.retrofit)
    implementation(deps.retrofit.converters.moshi)
    implementation(deps.retrofit.converters.scalars)
    implementation(deps.retrofit.converters.simplexml)
    implementation(deps.retrofit.converters.kotlinxSerialization)
    // Test
    testImplementation(TestDeps.junit4)
    androidTestImplementation(TestDeps.testRunner)
    androidTestImplementation(TestDeps.espresso)
}
