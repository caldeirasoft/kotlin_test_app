package com.caldeirasoft.castly.domain.model

import android.os.Parcelable
import org.threeten.bp.LocalDateTime
import java.util.*

/**
 * Created by Edmond on 09/02/2018.
 */
interface Podcast : Parcelable {
    var id: Long                // METADATA_KEY_MEDIA_ID
    var name: String            // METADATA_KEY_TITLE or METADATA_KEY_DISPLAY_TITLE
    var artistName: String      // METADATA_KEY_ARTIST or METADATA_KEY_DISPLAY_SUBTITLE
    var description: String?    // METADATA_KEY_DISPLAY_DESCRIPTION

    var feedUrl: String         // METADATA_KEY_MEDIA_URI
    var releaseDate: LocalDateTime       // METADATA_KEY_DATE
    var artwork: String         // METADATA_KEY_ALBUM_ART_URI or METADATA_KEY_DISPLAY_ICON_URI
    var artworkWidth: Int?
    var artworkHeight: Int?
    var trackCount: Int         // METADATA_KEY_NUM_TRACKS
    var podcastWebsiteUrl: String   // METADATA_KEY_WEBSITE_URL
    var copyright: String       // METADATA_KEY_WRITER
    var contentAdvisoryRating: String //🅴

    var isSubscribed: Boolean       // METADATA_KEY_SUBSCRIBED
    var subscribeAction: Int // queue,inbox,addfirst,addlast

    //@get:com.google.firebase.firestore.Exclude
    var localStatus:Int

    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeCreated: Date?
    //@get:com.google.firebase.firestore.ServerTimestamp
    var timeUpdate: Date?

    var episodes: List<Episode>
    var genres: List<Genre>    // METADATA_KEY_GENRE
    var podcastsByArtist: List<Long>   // METADATA_KEY_OTHER_PODCASTS_BY_ARTIST
    var podcastsListenersAlsoFollow: List<Long> // METADATA_KEY_OTHER_PODCASTS_FOLLOWED

    val transitionName: String
            get() = "transitionPodcast_$feedUrl"

    fun getArtwork(width: Int): String = getArtwork(artwork, width)

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