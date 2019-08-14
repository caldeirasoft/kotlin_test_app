package com.caldeirasoft.basicapp.di

import android.content.ComponentName
import android.content.Context
import android.support.v4.media.MediaBrowserCompat
import com.caldeirasoft.basicapp.BuildConfig
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
import com.caldeirasoft.basicapp.util.Constants
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
import com.caldeirasoft.castly.domain.repository.PodcastRepository
import com.caldeirasoft.castly.domain.usecase.*
import com.caldeirasoft.castly.service.playback.MediaService
import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.converter.scalars.ScalarsConverterFactory
import java.util.concurrent.Executor
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

val appModule = module {
    //single<Executor>(name = "mainExecutor") { MainThreadExecutor(get()) }
    factory<Executor>(name = "background") { Executors.newSingleThreadExecutor() }
    factory<Executor>(name = "network") { Executors.newFixedThreadPool(5) }
}

val networkModule = module {
    val ctx by lazy { androidApplication() }
    single { createHttpClient(ctx) }
    //single("cache" ) { createHttpClient(ctx, true) }
    single (name = "feedly") { createRetrofit(baseURL = Constants.FEEDLY_BASE_URL, okHttpClient = get())}
    single (name = "itunes") { createRetrofit(baseURL = Constants.ITUNES_BASE_URL, okHttpClient = get())}
    single (name = "podcasts") { createRetrofit(baseURL = Constants.PODCASTS_BASE_URL, okHttpClient = get())}
    single { createWebService<FeedlyApi>(get("feedly")) }
    single { createWebService<ITunesApi>(get("itunes")) }
    single { createWebService<PodcastsApi>(get("podcasts")) }
}

val dataModule = module {
    val ctx by lazy { androidApplication() }
    single { DatabaseApi.buildDatabase(ctx) }
    single { get<DatabaseApi>().episodeDao() }
    single { get<DatabaseApi>().podcastDao() }
}

val repositoryModule = module {
    single<EpisodeRepository> { EpisodeRepositoryImpl(episodeDao = get()) }
    single<PodcastRepository> { PodcastRepositoryImpl(podcastDao = get()) }
    single<ItunesRepository> { ItunesRepositoryImpl(podcastDao = get(), iTunesAPI = get(), podcastsApi = get()) }
}

val usecaseModule = module {
    single { GetItunesStoreUseCase(podcastRepository = get(), itunesRepository = get()) }
    //single { GetEpisodeFromDbUseCase(episodeRepository = get()) }
    //single { GetPodcastFromFeedlyUseCase(feedlyRepository = get()) }
    single { FetchSectionEpisodesUseCase(episodeRepository = get(), networkExecutor = get("network")) }
    single { FetchPodcastEpisodesUseCase(episodeRepository = get()) }
    single { FetchPodcastsFromItunesUseCase(itunesRepository = get(), podcastRepository = get()) }
    single { FetchPodcastsInDbUseCase(podcastRepository = get()) }
    single { GetPodcastInDbUseCase(podcastRepository = get()) }
    single { SubscribeToPodcastUseCase(podcastRepository = get()) }
    single { UpdatePodcastFromItunesUseCase(itunesRepository = get(), podcastRepository = get(), episodeRepository = get()) }
}

val mediaModule = module {
    single { MediaSessionConnection.getInstance(
            context = get(),  serviceComponent = ComponentName(get(), MediaService::class.java))}
}

val presentationModule = module {
    viewModel { QueueViewModel(episodeRepository = get(), mediaSessionConnection = get()) }
    viewModel { InboxViewModel(fetchSectionEpisodesUseCase = get(), mediaSessionConnection = get()) }
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
                podcastRepository = get(),
                episodeRepository = get(),
                mediaSessionConnection = get(),
                fetchPodcastEpisodesUseCase = get(),
                getPodcastInDbUseCase = get(),
                subscribeToPodcastUseCase = get(),
                updatePodcastFromItunesUseCase = get())}
    viewModel { (mediaItem: MediaBrowserCompat.MediaItem) -> EpisodeInfoViewModel(
            mediaItem = mediaItem,
            episodeRepository = get(),
            mediaSessionConnection = get())}
}


fun createHttpClient(context: Context, enableCache:Boolean = false) : OkHttpClient =
    OkHttpClient.Builder().apply {

        //set time out
        connectTimeout(Constants.DEFAULT_CONN_TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
        readTimeout(Constants.DEFAULT_READ_TIME_OUT.toLong(), TimeUnit.MILLISECONDS)
        writeTimeout(Constants.DEFAULT_WRITE_TIME_OUT.toLong(), TimeUnit.MILLISECONDS)

        //set dns
        val customDns = DnsProviders.buildCloudflare(OkHttpClient())
        dns(customDns)

        /*
        //set cache
        if (enableCache) {
            App.context?.apply {
                val cache = Cache(cacheDir, DEFAULT_CACHE_SIZE)
                cache(cache)
            }
        }
        */

        if (BuildConfig.DEBUG) {
            //set logging interceptor
            HttpLoggingInterceptor(HttpLogger()).let {
                it.level = HttpLoggingInterceptor.Level.BODY
                addInterceptor(it)
            }
        }
    }.build()

fun createMoshi(): Moshi = Moshi.Builder()
        .add(LocalDateTime::class.java, LocalDateTimeJsonAdapter())
        .add(LocalDate::class.java, LocalDateJsonAdapter())
        .build()

fun createRetrofit(baseURL: String, okHttpClient: OkHttpClient) : Retrofit =
    Retrofit.Builder()
            .baseUrl(baseURL)
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(MoshiConverterFactory.create(createMoshi()))
            .build()

inline fun <reified T> createWebService(retrofit: Retrofit) : T =
    retrofit.create(T::class.java)

