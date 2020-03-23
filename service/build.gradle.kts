plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
}

androidExtensions { isExperimental = true }
android {
    compileSdkVersion(AndroidSdk.compile)
}

dependencies {
    implementation(project( ":domain"))
    implementation(project( ":data"))
    // Android
    implementation(deps.androidx.core)
    implementation(deps.androidx.appcompat)
    implementation(deps.androidx.annotation)
    // Kotlin
    implementation(deps.kotlin.stdlib)
    implementation(deps.kotlin.reflect)
    implementation(deps.kotlin.coroutines.core)
    implementation(deps.kotlin.coroutines.android)
    implementation(deps.kotlin.coroutines.guava)
    // Android-Paging
    implementation(deps.androidx.paging.runtime)
    // Android-LiveData
    implementation(deps.androidx.lifecycle.extensions)
    kapt(deps.androidx.lifecycle.compiler)
    // Koin
    implementation(deps.koin.scope)
    // Media2
    implementation(deps.androidx.media2.session)
    implementation(deps.androidx.media2.widget)
    implementation(deps.androidx.media2.player)
    // ThreeTenABP (Java Time)
    implementation(deps.threeten)
    // Test
    testImplementation(TestDeps.junit4)
    androidTestImplementation(TestDeps.testRunner)
    androidTestImplementation(TestDeps.espresso)

}
