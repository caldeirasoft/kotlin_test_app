plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    kotlin("plugin.serialization") version "1.3.70"
}

android {
    compileSdkVersion(AndroidSdk.compile)
}

dependencies {
    // Android
    implementation(deps.androidx.appcompat)
    // Kotlin
    implementation(deps.kotlin.stdlib)
    implementation(deps.kotlin.reflect)
    implementation(deps.kotlin.coroutines.core)
    implementation(deps.kotlin.coroutines.android)
    // Kotlinx serialization
    implementation(deps.kotlinx.serialization)
    // Android-Paging
    implementation(deps.androidx.paging.common)
    implementation(deps.androidx.paging.runtime)
    // ThreeTenABP (Java Time)
    implementation(deps.threeten)
    // Test
    testImplementation(TestDeps.junit4)
    androidTestImplementation(TestDeps.testRunner)
    androidTestImplementation(TestDeps.espresso)
}
