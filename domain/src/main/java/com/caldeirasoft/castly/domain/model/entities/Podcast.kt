package com.caldeirasoft.castly.domain.model.entities

import android.os.Parcelable
import kotlinx.serialization.Serializable
import org.threeten.bp.LocalDate
import org.threeten.bp.LocalDateTime

/**
 * Created by Edmond on 09/02/2018.
 */
@Serializable
abstract class Podcast : Parcelable {
    abstract var id: Long
    abstract var name: String
    abstract var artistName: String
    abstract var artistId: Long?
    abstract var description: String?
    abstract var feedUrl: String
    abstract var releaseDate: LocalDate
    abstract var releaseDateTime: LocalDateTime
    abstract var artwork: Artwork?
    abstract var trackCount: Int
    abstract var podcastWebsiteUrl: String
    abstract var copyright: String
    abstract var contentAdvisoryRating: String //🅴
    abstract var userRating: Float

    // collections
    abstract var episodes: List<Episode>
    abstract var genres: List<Genre>
    abstract var podcastsByArtist: List<Long>
    abstract var podcastsListenersAlsoFollow: List<Long>


    abstract var isSubscribed: Boolean
    abstract var subscribeAction: Int
    abstract var localStatus: Int

    val transitionName: String
        get() = "transitionPodcast_$feedUrl"

    fun getArtwork(width: Int): String = getArtwork(artwork?.url.orEmpty(), width)

    companion object {
        val PODCAST_STATE = "podcast_state"
        val PODCAST_FILE_PATH = "podcast_downloaded_file_path"
        val PODCAST_DOWNLOAD_REFERENCE = "podcast_download_reference"
        val PODCAST_PROGRAM_ID = "podcast_program_id"
        val PODCAST_BIG_IMAGE_URL = "podcast_big_image_url"
        val PODCAST_DURATION = "podcast_duration"
        val PODCAST_DATE = "podcast_date"

        fun getArtwork(artwork: String, width: Int): String {
            return artwork.replace("{w}", width.toString())
                    .replace("{h}", width.toString())
                    .replace("{c}", "fa") // or bb
                    .replace("{f}", "jpg")
        }
    }
}