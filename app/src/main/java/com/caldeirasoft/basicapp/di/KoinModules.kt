package com.caldeirasoft.basicapp.di

import android.content.ComponentName
import android.content.Context
import com.caldeirasoft.basicapp.BuildConfig
import com.caldeirasoft.basicapp.data.model.file.FileManagerImpl
import com.caldeirasoft.basicapp.di.adapters.LocalDateJsonAdapter
import com.caldeirasoft.basicapp.di.adapters.LocalDateTimeJsonAdapter
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.basicapp.presentation.ui.catalog.CatalogViewModel
import com.caldeirasoft.basicapp.presentation.ui.discover.DiscoverViewModel
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoViewModel
import com.caldeirasoft.basicapp.presentation.ui.inbox.InboxViewModel
import com.caldeirasoft.basicapp.presentation.ui.podcast.PodcastViewModel
import com.caldeirasoft.basicapp.presentation.ui.podcastinfo.PodcastInfoViewModel
import com.caldeirasoft.basicapp.presentation.ui.queue.QueueViewModel
import com.caldeirasoft.basicapp.util.interceptors.*
import com.caldeirasoft.basicapp.util.network.DnsProviders
import com.caldeirasoft.castly.data.datasources.local.DatabaseApi
import com.caldeirasoft.castly.data.datasources.remote.FeedlyApi
import com.caldeirasoft.castly.data.datasources.remote.ITunesApi
import com.caldeirasoft.castly.data.datasources.remote.PodcastsApi
import com.caldeirasoft.castly.data.features.serializers.JsonSerializerProvider
import com.caldeirasoft.castly.data.repository.EpisodeRepositoryImpl
import com.caldeirasoft.castly.data.repository.ItunesRepositoryImpl
import com.caldeirasoft.castly.data.repository.PodcastRepositoryImpl
import com.caldeirasoft.castly.domain.model.entities.Podcast
import com.caldeirasoft.castly.domain.model.file.FileManager
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PlayerRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.*
import com.caldeirasoft.castly.service.playback.LibraryService
import com.caldeirasoft.castly.service.repository.PlayerRepositoryImpl
import com.chuckerteam.chucker.api.ChuckerInterceptor
import com.facebook.flipper.android.AndroidFlipperClient
import com.facebook.flipper.plugins.network.FlipperOkhttpInterceptor
import com.facebook.flipper.plugins.network.NetworkFlipperPlugin
import com.facebook.stetho.okhttp3.StethoInterceptor
import com.github.simonpercic.oklog3.OkLogInterceptor
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.serialization.UnstableDefault
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.core.qualifier.named
import org.koin.dsl.module
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import timber.log.Timber
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit


val appModule = module {
    //single<Executor>(name = "mainExecutor") { MainThreadExecutor(get()) }
    factory<Executor>(named("background")) { Executors.newSingleThreadExecutor() }
    factory<Executor>(named("network")) { Executors.newFixedThreadPool(5) }
}

val networkModule = module {
    single { createLoggingInterceptor() }
    single {
        okHttp().withChucker(context = androidContext())
                .build()
    }
    single(named("cacheControl")) {
        okHttp().withChucker(context = androidContext())
                .withCacheControl(context = androidContext())
                .build()
    }
    single { createWebService<FeedlyApi>(okHttpClient = get()) }
    single { createWebService<ITunesApi>(okHttpClient = get(named("cacheControl"))) }
    single { createWebService<PodcastsApi>(okHttpClient = get(named("cacheControl"))) }
}

val dataModule = module {
    single { DatabaseApi.buildDatabase(androidContext()) }
    single { get<DatabaseApi>().episodeDao() }
    single { get<DatabaseApi>().podcastDao() }
    single<FileManager> { FileManagerImpl(context = androidContext()) }
}

@FlowPreview
@ExperimentalCoroutinesApi
@UnstableDefault
val repositoryModule = module {
    single<EpisodeRepository> { EpisodeRepositoryImpl(episodeDao = get()) }
    single<PodcastRepository> { PodcastRepositoryImpl(podcastDao = get(), episodeDao = get(), podcastsApi = get()) }
    single<ItunesRepository> {
        ItunesRepositoryImpl(
                podcastDao = get(),
                iTunesAPI = get(),
                podcastsApi = get(),
                fileManager = get(),
                marshaller = JsonSerializerProvider.provide())
    }
    single<PlayerRepository> {
        PlayerRepositoryImpl(
                episodeDao = get(),
                context = get(),
                serviceComponent = ComponentName(get(), LibraryService::class.java))
    }
}

@FlowPreview
@ExperimentalCoroutinesApi
val usecaseModule = module {
    single { GetItunesGroupingDataUseCase(itunesRepository = get()) }
    single { FetchSectionEpisodesUseCase(episodeRepository = get()) }
    single { FetchPodcastEpisodesUseCase(episodeRepository = get()) }
    single { FetchPodcastsFromItunesUseCase(itunesRepository = get()) }
    single { FetchPodcastsInDbUseCase(podcastRepository = get()) }
    single { FetchEpisodeCountByPodcastUseCase(episodeRepository = get()) }
    single { FetchEpisodeCountBySectionUseCase(episodeRepository = get()) }
    single { GetPodcastUseCase(podcastRepository = get()) }
    single { SubscribeToPodcastUseCase(podcastRepository = get()) }
    single { UnsubscribeFromPodcastUseCase(podcastRepository = get()) }
}

val mediaModule = module {
    single { MediaSessionConnection.getInstance(
            context = get(),  serviceComponent = ComponentName(get(), LibraryService::class.java))}
}

@FlowPreview
@ExperimentalCoroutinesApi
val presentationModule = module {
    viewModel { QueueViewModel(get(), get()) }
    viewModel { InboxViewModel(
            fetchSectionEpisodesUseCase = get(),
            fetchEpisodeCountByPodcastUseCase = get()) }
    viewModel { PodcastViewModel(
            mediaSessionConnection = get(),
            podcastRepository = get()) }
    viewModel { DiscoverViewModel(
            fetchPodcastsFromItunesUseCase = get(),
            getItunesStoreUseCase = get())}
    viewModel { (category: Int) -> CatalogViewModel(itunesRepository = get(), podcastRepository = get(), category = category) }
    viewModel { (podcast: Podcast) ->
        PodcastInfoViewModel(
                podcast = podcast,
                fetchPodcastEpisodesUseCase = get(),
                fetchEpisodeCountBySectionUseCase = get(),
                getPodcastUseCase = get(),
                subscribeToPodcastUseCase = get(),
                unsubscribeFromPodcastUseCase = get())
    }
    viewModel { (episodeId: Long) ->
        EpisodeInfoViewModel(
                episodeId = episodeId,
                episodeRepository = get(),
                mediaSessionConnection = get())
    }
}


/**
 * Create Http Logging Interceptor
 */
fun createLoggingInterceptor(): HttpLoggingInterceptor {
    val logger = object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) = Timber.d(message)
    }

    val interceptor = HttpLoggingInterceptor(logger).apply {
        level = HttpLoggingInterceptor.Level.BASIC
    }
    return interceptor
}

fun createMoshi(): Moshi = Moshi.Builder()
        .add(LocalDateTime::class.java, LocalDateTimeJsonAdapter())
        .add(LocalDate::class.java, LocalDateJsonAdapter())
        .add(KotlinJsonAdapterFactory())
        .build()

inline fun <reified T> createWebService(okHttpClient: OkHttpClient): T {
    val baseURL = T::class.java.getField("baseUrl").get(null) as String
    val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(createMoshi()))
            .build()

    return retrofit.create(T::class.java)
}

fun okHttp() = OkHttpClient.Builder().apply {
    connectTimeout(20, TimeUnit.SECONDS)
    readTimeout(60, TimeUnit.SECONDS)
    writeTimeout(20, TimeUnit.SECONDS)
    addInterceptor(GzipRequestInterceptor)
    addNetworkInterceptor(StethoInterceptor())
    if (BuildConfig.DEBUG) {
        // create an instance of OkLogInterceptor using a builder()
        val okLogInterceptor = OkLogInterceptor.builder().build()
        addInterceptor(okLogInterceptor)
    }
    val customDns = DnsProviders.buildCloudflare(OkHttpClient())
    dns(customDns)
}

fun OkHttpClient.Builder.withCacheControl(context: Context) = this.apply {
    addNetworkInterceptor(RewriteResponseInterceptor())
    addInterceptor(RewriteOfflineRequestInterceptor(context))
}

fun OkHttpClient.Builder.withChucker(context: Context) = this.apply {
    addInterceptor(ChuckerInterceptor(context))
}