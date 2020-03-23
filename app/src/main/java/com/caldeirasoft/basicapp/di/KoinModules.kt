package com.caldeirasoft.basicapp.di

import android.content.ComponentName
import android.content.Context
import com.caldeirasoft.basicapp.di.adapters.LocalDateJsonAdapter
import com.caldeirasoft.basicapp.di.adapters.LocalDateTimeJsonAdapter
import com.caldeirasoft.basicapp.media.MediaSessionConnection
import com.caldeirasoft.basicapp.presentation.ui.catalog.CatalogViewModel
import com.caldeirasoft.basicapp.presentation.ui.discover.DiscoverViewModel
import com.caldeirasoft.basicapp.presentation.ui.episodeinfo.EpisodeInfoViewModel
import com.caldeirasoft.basicapp.presentation.ui.inbox.InboxViewModel
import com.caldeirasoft.basicapp.presentation.ui.podcast.PodcastViewModel
import com.caldeirasoft.basicapp.presentation.ui.podcastdetail.PodcastDetailViewModel
import com.caldeirasoft.basicapp.presentation.ui.podcastinfo.PodcastInfoViewModel
import com.caldeirasoft.basicapp.presentation.ui.queue.QueueViewModel
import com.caldeirasoft.basicapp.util.Constants.DEFAULT_CACHE_SIZE
import com.caldeirasoft.basicapp.util.DnsProviders
import com.caldeirasoft.basicapp.util.HttpLogger
import com.caldeirasoft.castly.data.datasources.local.DatabaseApi
import com.caldeirasoft.castly.data.datasources.remote.FeedlyApi
import com.caldeirasoft.castly.data.datasources.remote.ITunesApi
import com.caldeirasoft.castly.data.datasources.remote.PodcastsApi
import com.caldeirasoft.castly.data.repository.EpisodeRepositoryImpl
import com.caldeirasoft.castly.data.repository.ItunesRepositoryImpl
import com.caldeirasoft.castly.data.repository.PodcastRepositoryImpl
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.EpisodeRepository
import com.caldeirasoft.castly.domain.repository.ItunesRepository
import com.caldeirasoft.castly.domain.repository.PlayerRepository
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.*
import com.caldeirasoft.castly.service.playback.LibraryService
import com.caldeirasoft.castly.service.repository.PlayerRepositoryImpl
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.Cache
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
    //single("cache" ) { createHttpClient(ctx, true) }
    single { createWebService<FeedlyApi>(context = get(), loggingInterceptor = get()) }
    single { createWebService<ITunesApi>(context = get(), loggingInterceptor = get()) }
    single { createWebService<PodcastsApi>(context = get(), loggingInterceptor = get()) }
}

val dataModule = module {
    single { DatabaseApi.buildDatabase(androidContext()) }
    single { get<DatabaseApi>().episodeDao() }
    single { get<DatabaseApi>().podcastDao() }
}

val repositoryModule = module {
    single<EpisodeRepository> { EpisodeRepositoryImpl(episodeDao = get()) }
    single<PodcastRepository> { PodcastRepositoryImpl(podcastDao = get()) }
    single<ItunesRepository> { ItunesRepositoryImpl(podcastDao = get(), iTunesAPI = get(), podcastsApi = get()) }
    single<PlayerRepository> {
        PlayerRepositoryImpl(
                episodeDao = get(),
                context = get(),
                serviceComponent = ComponentName(get(), LibraryService::class.java))}
}

val usecaseModule = module {
    single { GetItunesStoreUseCase(podcastRepository = get(), itunesRepository = get()) }
    single { FetchSectionEpisodesUseCase(episodeRepository = get()) }
    single { FetchPodcastEpisodesUseCase(episodeRepository = get()) }
    single { FetchPodcastsFromItunesUseCase(itunesRepository = get(), podcastRepository = get()) }
    single { FetchPodcastsInDbUseCase(podcastRepository = get()) }
    single { FetchEpisodeCountByPodcastUseCase(episodeRepository = get()) }
    single { FetchEpisodeCountBySectionUseCase(episodeRepository = get()) }
    single { GetPodcastInDbUseCase(podcastRepository = get()) }
    single { SubscribeToPodcastUseCase(podcastRepository = get()) }
    single { UpdatePodcastFromItunesUseCase(itunesRepository = get(), podcastRepository = get(), episodeRepository = get()) }
}

val mediaModule = module {
    single { MediaSessionConnection.getInstance(
            context = get(),  serviceComponent = ComponentName(get(), LibraryService::class.java))}
}

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
    viewModel { (podcastId: Long, podcast: Podcast?) -> PodcastInfoViewModel(
            podcastId = podcastId,
            podcast = podcast,
            fetchPodcastEpisodesUseCase = get(),
            getPodcastInDbUseCase = get(),
            fetchEpisodeCountBySectionUseCase = get(),
            subscribeToPodcastUseCase = get(),
            updatePodcastFromItunesUseCase = get())}
    viewModel { (podcastId: Long, podcast: Podcast?) -> PodcastDetailViewModel(
                podcastId = podcastId,
                podcast = podcast,
                podcastRepository = get(),
                episodeRepository = get(),
                mediaSessionConnection = get(),
                fetchPodcastEpisodesUseCase = get(),
                getPodcastInDbUseCase = get(),
                subscribeToPodcastUseCase = get(),
                updatePodcastFromItunesUseCase = get())
    }
    viewModel { (episodeId: Long) -> EpisodeInfoViewModel(
            episodeId = episodeId,
            episodeRepository = get(),
            mediaSessionConnection = get())}
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

fun createRetrofit(baseURL: String) : Retrofit {
    // Add the request headers
    val client = OkHttpClient.Builder()
            .addInterceptor( HttpLoggingInterceptor(HttpLogger()).apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

    val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(client)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(createMoshi()))
            //.addConverterFactory(Json(JsonConfiguration(ignoreUnknownKeys = true)).asConverterFactory("application/json".toMediaType()))
            .build()
    return retrofit
}

inline fun <reified T> createWebService(
        context: Context,
        loggingInterceptor: HttpLoggingInterceptor,
        time: Long? = null,
        unit: TimeUnit? = null) : T {

    // Add the request headers
    val client = OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)

    //set dns
    val customDns = DnsProviders.buildCloudflare(OkHttpClient())
    client.dns(customDns)

    // Add the cache support
    if (time != null && unit != null) {
        client.cache(Cache(context.cacheDir, DEFAULT_CACHE_SIZE))
                .addInterceptor(cachePolicyInterceptor(time, unit))
    }

    val baseURL = T::class.java.getField("baseUrl").get(null) as String
    val retrofit = Retrofit.Builder()
            .baseUrl(baseURL)
            .client(client.build())
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(createMoshi()))
            //.addConverterFactory(Json(JsonConfiguration(ignoreUnknownKeys = true)).asConverterFactory("application/json".toMediaType()))
            .build()

    return retrofit.create(T::class.java)
}

fun cachePolicyInterceptor(period: Long, unit: TimeUnit): Interceptor
{
    val seconds = unit.toSeconds(period)

    return Interceptor {
        val request = it.request().newBuilder()
                .header("Cache-Control", "public, max-stale=$seconds")
                .build()

        it.proceed(request)
    }
}