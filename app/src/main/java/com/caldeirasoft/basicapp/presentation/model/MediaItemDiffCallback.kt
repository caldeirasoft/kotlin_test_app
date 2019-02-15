package com.caldeirasoft.basicapp.presentation.model

import android.support.v4.media.MediaBrowserCompat
import androidx.recyclerview.widget.DiffUtil

/**
 * Created by Edmond on 15/02/2018.
 */
class MediaItemDiffCallback : DiffUtil.ItemCallback<MediaBrowserCompat.MediaItem>() {

    override fun areItemsTheSame(oldItem: MediaBrowserCompat.MediaItem,
                                 newItem: MediaBrowserCompat.MediaItem): Boolean =
            (oldItem.mediaId == newItem.mediaId)

    override fun areContentsTheSame(oldItem: MediaBrowserCompat.MediaItem, newItem: MediaBrowserCompat.MediaItem) =
            (oldItem.mediaId == newItem.mediaId)
}