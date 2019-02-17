package com.caldeirasoft.basicapp.service_old.mediaplayback

import android.net.Uri
import android.os.Bundle
import android.support.v4.media.MediaBrowserCompat
import android.support.v4.media.MediaDescriptionCompat
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.MediaSessionCompat
import com.caldeirasoft.castly.data.datasources.local.dao.EpisodeDao
import com.caldeirasoft.castly.domain.model.Episode
import com.caldeirasoft.castly.domain.model.Podcast
import org.jetbrains.anko.doAsyncResult
import java.util.ArrayList

/**
 * Created by Edmond on 15/03/2018.
 */
class QueueManager(private val episodeDao: EpisodeDao)
{
    companion object {
        //val MEDIA_ID_PLAY_ALL = -10
        val MEDIA_ID_PLAY_ALL = "play_all_podcasts_playlist"
        val METADATA_HAS_NEXT_OR_PREVIOUS = "android.media.metadata.NEXT_or_PREVIOUS"
        val METADATA_PROGRAM_ID = "android.media.metadata.PROGRAM_ID"
    }

    //var potentialPlaylist: List<MediaSessionCompat.QueueItem> = listOf()
    internal var currentPlaylist: List<MediaSessionCompat.QueueItem> = listOf()
    private lateinit var listener: MetaDataUpdateListener
    private var currentQueueId: Long = -1
    private var isSinglePodcast = false

    fun initListener(metadataUpdateListener: MetaDataUpdateListener) {
        listener = metadataUpdateListener
    }

    fun getMediaItem(mediaId: String?): MediaMetadataCompat? {
        return if (mediaId != null) {
            /*val item = if (mediaId == MEDIA_ID_PLAY_ALL) {
                currentPlaylist[0]
            } else {
                currentPlaylist.filter { it -> it.description.mediaId == mediaId }[0]
            }*/
            val item = currentPlaylist.filter { it -> it.description.mediaId == mediaId }[0]
            val itemMediaData = item.description
            //currentQueueId = item.queueId

            val extras = itemMediaData.extras
            val downloadPath: String? = extras?.getString(Podcast.PODCAST_FILE_PATH)
            val programId: String? = extras?.getString(Podcast.PODCAST_PROGRAM_ID)
            val mediaUri: String = downloadPath ?: itemMediaData.mediaUri.toString()
            val duration: Long = if (extras?.getLong(Podcast.PODCAST_DURATION) != null) {
                extras.getLong(Podcast.PODCAST_DURATION) * 1000
            } else {
                0L
            }

            MediaMetadataCompat.Builder()
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_ID, itemMediaData.mediaId)
                    .putText(MediaMetadataCompat.METADATA_KEY_TITLE, itemMediaData.title)
                    .putString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI, mediaUri)
                    .putString(MediaMetadataCompat.METADATA_KEY_ALBUM_ART_URI,
                            itemMediaData.iconUri.toString())
                    .putLong(MediaMetadataCompat.METADATA_KEY_TRACK_NUMBER, item.queueId + 1)
                    .putLong(MediaMetadataCompat.METADATA_KEY_NUM_TRACKS,
                            currentPlaylist.size.toLong())
                    .putLong(MediaMetadataCompat.METADATA_KEY_DURATION, duration)
                    .putLong(METADATA_HAS_NEXT_OR_PREVIOUS, hasNextOrPrevious())
                    .putString(METADATA_PROGRAM_ID, programId)
                    .build()
        } else {
            null
        }
    }

    internal fun hasNextOrPrevious(): Long {
        if (isSinglePodcast) {
            return 0
        }
        return 1
    }

    fun setQueue(mediaId: String?) {
        setCurrentQueue(mediaId)
        updateMetadata(mediaId)
        if (mediaId == MEDIA_ID_PLAY_ALL) {
            //eventLogger.logPlayAllPodcasts()
        } else {
            //eventLogger.logPlaySinglePodcast()
        }
    }

    private fun setCurrentQueue(mediaId: String?) {
        var episodes = arrayListOf<Episode>().apply {
            mediaId?.let {
                doAsyncResult { episodeDao.getSync(it) }.let {
                    it.get()?.let { ep ->
                        this.add(ep)
                    }
                }
            }
        }

        val mediaItems = episodes.mapTo(ArrayList()) { createBrowsableMediaItemForEpisode(it) }
        currentPlaylist = mediaItemsToQueueItems(mediaItems)
        listener.updateQueue("Play Queue", currentPlaylist)
    }

    fun updateMetadata(mediaId: String?) {
        val metadata = getMediaItem(mediaId)
        listener.updateMetadata(metadata)
    }

    fun getNextMediaId(): String? {
        if (currentPlaylist.size <= 1) {
            return null
        }
        if ((currentQueueId + 1) < currentPlaylist.size.toLong()) {
            return currentPlaylist[(currentQueueId + 1).toInt()].description.mediaId
        }
        return null
    }

    fun getPreviousMediaId(): String? {
        if (currentPlaylist.size <= 1) {
            return null
        }
        if ((currentQueueId - 1) >= 0) {
            return currentPlaylist[(currentQueueId - 1).toInt()].description.mediaId
        }
        return null
    }

    fun getCurrentMediaId(): Long {
        return currentQueueId
    }

    fun updateDownloadsPlaylist(items: List<MediaSessionCompat.QueueItem>) {
        currentPlaylist = if (isSinglePodcast) {
            currentPlaylist
        } else {
            items
        }
    }

    private fun createBrowsableMediaItemForEpisode(episode: Episode): MediaBrowserCompat.MediaItem {
        val extras = Bundle()
        //extras.putSerializable(Podcast.PODCAST_STATE, episode.state)
        extras.putString(Podcast.PODCAST_PROGRAM_ID, episode.feedUrl)
        extras.putString(Podcast.PODCAST_BIG_IMAGE_URL, episode.bigImageUrl)
        extras.putLong(Podcast.PODCAST_DURATION, episode.duration?.toLong() ?: 0)
        extras.putLong(Podcast.PODCAST_DATE, episode.published)

        val description = MediaDescriptionCompat.Builder()
                .setMediaId(episode.mediaUrl)
                .setTitle(episode.title)
                .setMediaUri(Uri.parse(episode.mediaUrl))
                .setIconUri(Uri.parse(episode.imageUrl))
                .setExtras(extras)
                .build()
        return MediaBrowserCompat.MediaItem(description, MediaBrowserCompat.MediaItem.FLAG_PLAYABLE)
    }

    private fun mediaItemsToQueueItems(mediaItems: ArrayList<MediaBrowserCompat.MediaItem>)
            : List<MediaSessionCompat.QueueItem> {

        return mediaItems.mapTo(ArrayList()) {
            it -> MediaSessionCompat.QueueItem(it.description, mediaItems.indexOf(it).toLong())
        }
    }
}