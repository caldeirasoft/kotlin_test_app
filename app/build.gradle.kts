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
    implementation(deps.androidx.lifecycle.extensions)
    kapt(deps.androidx.lifecycle.compiler)
    // Android-Navigation
    implementation(deps.arch.navigation.fragment)
    implementation(deps.arch.navigation.ui)
    // Circular progressbar
    // implementation(deps.circularprogressbar)
    // Conscrypt
    implementation(deps.conscrypt)
    // Epoxy
    implementation(deps.epoxy.runtime)
    implementation(deps.epoxy.databinding)
    implementation(deps.epoxy.paging)
    kapt(deps.epoxy.processor)
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