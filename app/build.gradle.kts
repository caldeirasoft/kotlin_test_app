plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("android.extensions")
    kotlin("kapt")
    kotlin("plugin.serialization") version Versions.kotlin
    //Apply Safe Args Plugin
    id("androidx.navigation.safeargs")
}

android {
    dataBinding { isEnabled = true }

    compileSdkVersion(AndroidSdk.compile)
    defaultConfig {
        applicationId = "com.caldeirasoft.basicapp"
        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)

        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles (getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
    kotlinOptions.jvmTarget = "1.8"
    //viewBinding.isEnabled = true  // https://developer.android.com/topic/libraries/view-binding
    dataBinding.isEnabled = true
}

kapt {
    useBuildCache = true
    arguments {
        correctErrorTypes = true // Epoxy model support
    }
}

// Required since Gradle 4.10+.
repositories {
    jcenter()
    maven("https://jitpack.io")
}

dependencies {
    // Local project
    implementation(project(":roundbutton"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":service"))
    // Android
    implementation(deps.androidx.core)
    implementation(deps.androidx.core_ktx)
    implementation(deps.androidx.appcompat)
    implementation(deps.androidx.constraintlayout)
    implementation(deps.androidx.palette)
    implementation(deps.android.material)
    implementation(deps.androidx.swiperefeshlayout)
    // Kotlin
    implementation(deps.kotlin.stdlib)
    implementation(deps.kotlin.reflect)
    implementation(deps.kotlin.coroutines.core)
    implementation(deps.kotlin.coroutines.android)
    implementation(deps.kotlin.coroutines.guava)
    // Kotlinx serialization
    implementation(deps.kotlinx.serialization)
    // Android-Room
    implementation(deps.androidx.room.runtime)
    implementation(deps.androidx.room.ktx)
    kapt(deps.androidx.room.compiler)
    // Android-Paging
    implementation(deps.androidx.paging.runtime)
    // Android-LiveData
    implementation(deps.androidx.lifecycle.runtime_ktx)
    implementation(deps.androidx.lifecycle.livedata_ktx)
    implementation(deps.androidx.lifecycle.viewmodel_ktx)
    implementation(deps.androidx.lifecycle.savestate)
    kapt(deps.androidx.lifecycle.compiler)
    // Android-Navigation
    implementation(deps.androidx.navigation.fragment_ktx)
    implementation(deps.androidx.navigation.ui_ktx)
    // Chucker
    debugImplementation(deps.chucker.library)
    releaseImplementation(deps.chucker.libraryNoOp)
    // Coin
    implementation(deps.coil)
    // Conscrypt
    implementation(deps.conscrypt)
    // CustomizedTextView
    implementation(deps.customizedTextView)
    // Epoxy
    implementation(deps.epoxy.runtime)
    implementation(deps.epoxy.databinding)
    implementation(deps.epoxy.paging)
    kapt(deps.epoxy.processor)
    // Flipper
    debugImplementation(deps.flipper.flipper)
    debugImplementation(deps.flipper.flipperNetwork)
    debugImplementation(deps.flipper.soloader)
    releaseImplementation(deps.flipper.flipperNoOp)
    // Internationalisation
    implementation(deps.i18n)
    // Koin
    implementation(deps.koin.scope)
    implementation(deps.koin.fragment)
    implementation(deps.koin.viewmodel)
    // Kotlin XML databinding DSL
    implementation(deps.xmlBind)
    // KotPref
    implementation(deps.kotpref.runtime)
    implementation(deps.kotpref.enum)
    // Lapism Search
    implementation(deps.lapismSearch)
    // Material IconLib
    implementation(deps.materialIconLib)
    // Media2
    implementation(deps.androidx.media2.session)
    implementation(deps.androidx.media2.widget)
    implementation(deps.androidx.media2.player)
    // Moshi
    implementation(deps.moshi.moshi)
    implementation(deps.moshi.kotlin)
    // OkHttp
    implementation(deps.okhttp.okhttp)
    implementation(deps.okhttp.dns)
    implementation(deps.okhttp.loggingInterceptor)
    // OkLog3
    implementation(deps.okLog3)
    // Picasso
    implementation(deps.picasso.picasso)
    implementation(deps.picasso.transformations)
    // Retrofit
    implementation(deps.retrofit.retrofit)
    implementation(deps.retrofit.converters.moshi)
    implementation(deps.retrofit.converters.scalars)
    implementation(deps.retrofit.converters.simplexml)
    implementation(deps.retrofit.converters.kotlinxSerialization)
    // RoundedImageView
    implementation(deps.roundedImageView)
    // Stetho
    implementation(deps.stetho.runtime)
    implementation(deps.stetho.okhttp3)
    // Shimmer
    implementation(deps.shimmerLayout)
    // TextDrawable
    implementation(deps.textDrawable)
    // Timber
    implementation(deps.timber)
    // ThreeTenABP (Java Time)
    implementation(deps.threeten)
    // Android debug database
    debugImplementation("com.amitshekhar.android:debug-db:1.0.3")
    // JUnit
    testImplementation(TestDeps.junit4)
    androidTestImplementation(TestDeps.testRunner)
    androidTestImplementation(TestDeps.espresso)
}