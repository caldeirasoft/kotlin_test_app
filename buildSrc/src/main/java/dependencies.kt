object Versions {
    const val kotlin = "1.3.70"
    const val coroutines = "1.3.3"
    const val serialization = "0.20.0"
    const val appcompat = "1.1.0"
    const val support = "1.2.0"
    const val palette = "1.0.0"
    const val material = "1.1.0-rc02"
    const val annotation = "1.1.0"
    const val constraintlayout = "2.0.0-beta4"
    const val room = "2.2.4"
    const val paging = "2.1.0"
    const val lifecycle = "2.2.0"
    const val navigation = "2.2.2"
    const val media2 = "1.0.3"
    const val swiperefreshlayout = "1.0.0"

    const val chucker = "3.2.0"
    const val coil = "0.9.5"
    const val conscrypt = "2.2.1"
    const val customizedTextView = "2.2"
    const val epoxy = "3.9.0"
    const val flipper = "0.38.0"
    const val i18n = "1.27"
    const val koin = "2.1.3"
    const val kotpref = "2.10.0"
    const val materialIconLib = "1.1.5"
    const val moshi = "1.9.2"
    const val okhttp = "4.4.0"
    const val oklog3 = "2.3.0"
    const val pandora = "2.1.0"
    const val pandoraNoOp = "2.0.3"
    const val pandoraPlugin = "1.0.0"
    const val paperparcel = "2.0.8"
    const val picasso = "2.71828"
    const val picasso_blur = "2.2.1"
    const val roundedImageView = "2.3.0"
    const val retrofit = "2.6.4"
    const val retrofitKotlinxSerialization = "0.5.0"
    const val lapismSearch = "2.0.0"
    const val shimmerLayout = "2.1.0"
    const val soloader = "0.9.0"
    const val stetho = "1.5.1"
    const val superBottomSheet = "1.2.3"
    const val textDrawable = "1.0.1"
    const val timber = "4.7.1"
    const val threeten = "1.2.2"
    const val xmlBind = "0.2.0"

    const val androidGradlePlugin = "3.6.1"

    const val junit = "4.12"
    const val espressocore = "3.1.0"
    const val runner = "1.1.0"
    const val truth = "0.42"
}

object TestVer {
    const val junit4 = "4.12"
    const val testRunner = "1.1.1"
    const val espresso = "3.1.1"
    const val mockitoKotlin = "2.1.0"
    const val mockito = "3.0.0"
    const val threeTen = "1.4.0"
}

object AndroidSdk {
    const val min = 23
    const val compile = 29
    const val target = compile
}

object deps {
    const val coil = "io.coil-kt:coil:${Versions.coil}"
    const val conscrypt = "org.conscrypt:conscrypt-android:${Versions.conscrypt}"
    const val customizedTextView = "com.libRG:customtextview:${Versions.customizedTextView}"
    const val i18n = "com.neovisionaries:nv-i18n:${Versions.i18n}"
    const val lapismSearch = "com.lapism:search:${Versions.lapismSearch}@aar"
    const val materialIconLib = "net.steamcrafted:materialiconlib:${Versions.materialIconLib}"
    const val okLog3 = "com.github.simonpercic:oklog3:${Versions.oklog3}"
    const val roundedImageView = "com.makeramen:roundedimageview:${Versions.roundedImageView}"
    const val shimmerLayout = "io.supercharge:shimmerlayout:${Versions.shimmerLayout}"
    const val superBottomSheet = "com.github.andrefrsousa:SuperBottomSheet:${Versions.superBottomSheet}"
    const val textDrawable = "com.amulyakhare:com.amulyakhare.textdrawable:${Versions.textDrawable}"
    const val threeten = "com.jakewharton.threetenabp:threetenabp:${Versions.threeten}"
    const val xmlBind = "org.jonnyzzz.kotlin.xml.bind:jdom:${Versions.xmlBind}"
    const val timber = "com.jakewharton.timber:timber:${Versions.timber}"

    object gradle {
        object plugins {
            const val safeArgs = "android.arch.navigation:navigation-safe-args-gradle-plugin:${Versions.navigation}"
            const val kotlin = "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlin}"
            const val androidPlugin = "com.android.tools.build:gradle:${Versions.androidGradlePlugin}"
        }
    }

    object kotlin {
        const val stdlib = "org.jetbrains.kotlin:kotlin-stdlib-jdk7:${Versions.kotlin}"
        const val reflect = "org.jetbrains.kotlin:kotlin-reflect:${Versions.kotlin}"

        object coroutines {
            const val core = "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutines}"
            const val android = "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutines}"
            const val guava = "org.jetbrains.kotlinx:kotlinx-coroutines-guava:${Versions.coroutines}"
        }
    }

    object kotlinx {
        const val serialization = "org.jetbrains.kotlinx:kotlinx-serialization-runtime:${Versions.serialization}"
    }

    object android {
        const val material = "com.google.android.material:material:${Versions.material}"
    }

    object androidx {
        const val appcompat = "androidx.appcompat:appcompat:${Versions.appcompat}"
        const val core_ktx = "androidx.core:core-ktx:${Versions.support}"
        const val core = "androidx.core:core:${Versions.support}"
        const val palette = "androidx.palette:palette:${Versions.palette}"
        const val constraintlayout = "androidx.constraintlayout:constraintlayout:${Versions.constraintlayout}"
        const val annotation = "androidx.annotation:annotation:${Versions.annotation}"
        const val swiperefeshlayout = "androidx.swiperefreshlayout:swiperefreshlayout:${Versions.swiperefreshlayout}"

        object room {
            const val runtime = "androidx.room:room-runtime:${Versions.room}"
            const val compiler = "androidx.room:room-compiler:${Versions.room}"
            const val ktx = "androidx.room:room-ktx:${Versions.room}"
        }

        object lifecycle {
            const val compiler = "androidx.lifecycle:lifecycle-compiler:${Versions.lifecycle}"
            const val runtime_ktx = "androidx.lifecycle:lifecycle-runtime-ktx:${Versions.lifecycle}"
            const val livedata_ktx = "androidx.lifecycle:lifecycle-livedata-ktx:${Versions.lifecycle}"
            const val viewmodel_ktx = "androidx.lifecycle:lifecycle-viewmodel-ktx:${Versions.lifecycle}"
            const val savestate = "androidx.lifecycle:lifecycle-viewmodel-savedstate:${Versions.lifecycle}"
        }

        object navigation {
            const val fragment_ktx = "androidx.navigation:navigation-fragment-ktx:${Versions.navigation}"
            const val ui_ktx = "androidx.navigation:navigation-ui-ktx:${Versions.navigation}"
        }

        object paging {
            const val common = "androidx.paging:paging-common:${Versions.paging}"
            const val runtime = "androidx.paging:paging-runtime-ktx:${Versions.paging}"
        }

        object media2 {
            const val session = "androidx.media2:media2-session:${Versions.media2}"
            const val widget = "androidx.media2:media2-widget:${Versions.media2}"
            const val player = "androidx.media2:media2-player:${Versions.media2}"
        }
    }

    object chucker {
        const val library = "com.github.ChuckerTeam.Chucker:library:${Versions.chucker}"
        const val libraryNoOp = "com.github.ChuckerTeam.Chucker:library-no-op:${Versions.chucker}"
    }

    object epoxy {
        const val runtime = "com.airbnb.android:epoxy:${Versions.epoxy}"
        const val databinding = "com.airbnb.android:epoxy-databinding:${Versions.epoxy}"
        const val paging = "com.airbnb.android:epoxy-paging:${Versions.epoxy}"
        const val processor = "com.airbnb.android:epoxy-processor:${Versions.epoxy}"
    }

    object flipper {
        const val flipper = "com.facebook.flipper:flipper:${Versions.flipper}"
        const val flipperNetwork = "com.facebook.flipper:flipper-network-plugin:${Versions.flipper}"
        const val flipperNoOp = "com.facebook.flipper:flipper-noop:${Versions.flipper}"
        const val soloader = "com.facebook.soloader:soloader:${Versions.soloader}"
    }

    object koin {
        const val scope = "org.koin:koin-androidx-scope:${Versions.koin}"
        const val fragment = "org.koin:koin-androidx-fragment:${Versions.koin}"
        const val viewmodel = "org.koin:koin-androidx-viewmodel:${Versions.koin}"
    }

    object kotpref {
        const val runtime = "com.chibatching.kotpref:kotpref:${Versions.kotpref}"
        const val enum = "com.chibatching.kotpref:enum-support:${Versions.kotpref}"
    }

    object moshi {
        const val moshi = "com.squareup.moshi:moshi:${Versions.moshi}"
        const val kotlin = "com.squareup.moshi:moshi-kotlin:${Versions.moshi}"
    }

    object okhttp {
        const val okhttp = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
        const val dns = "com.squareup.okhttp3:okhttp-dnsoverhttps:${Versions.okhttp}"
        const val loggingInterceptor = "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
    }

    object picasso {
        const val picasso = "com.squareup.picasso:picasso:${Versions.picasso}"
        const val transformations = "jp.wasabeef:picasso-transformations:${Versions.picasso_blur}"
    }

    object retrofit {
        const val retrofit = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"

        object converters {
            const val moshi = "com.squareup.retrofit2:converter-moshi:${Versions.retrofit}"
            const val scalars = "com.squareup.retrofit2:converter-scalars:${Versions.retrofit}"
            const val simplexml = "com.squareup.retrofit2:converter-simplexml:${Versions.retrofit}"

            const val kotlinxSerialization = "com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:${Versions.retrofitKotlinxSerialization}"
        }
    }

    object stetho {
        const val runtime = "com.facebook.stetho:stetho:${Versions.stetho}"
        const val okhttp3 = "com.facebook.stetho:stetho-okhttp3:${Versions.stetho}"
    }
}

object TestDeps {
    const val junit4 = "junit:junit:${TestVer.junit4}"
    const val testRunner = "androidx.test:runner:${TestVer.testRunner}"
    const val espresso = "androidx.test.espresso:espresso-core:${TestVer.espresso}"
    const val mockitoKotlin = "com.nhaarman.mockitokotlin2:mockito-kotlin:${TestVer.mockitoKotlin}"
    const val mockitoInline = "org.mockito:mockito-inline:${TestVer.mockito}"
    const val mockito = "org.mockito:mockito-core:${TestVer.mockito}"
    const val threeTen= "org.threeten:threetenbp:${TestVer.threeTen}"
}