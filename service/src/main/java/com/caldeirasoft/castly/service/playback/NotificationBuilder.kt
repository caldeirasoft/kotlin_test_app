/*
 * Copyright 2018 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.caldeirasoft.castly.service.playback

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.media.session.PlaybackState.*
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.media.session.MediaButtonReceiver
import androidx.media2.MediaSession
import androidx.media2.SessionToken
import com.caldeirasoft.castly.service.R
import com.caldeirasoft.castly.service.playback.extensions.isPlayEnabled
import com.caldeirasoft.castly.service.playback.extensions.isPlaying
import com.caldeirasoft.castly.service.playback.extensions.isSkipToNextEnabled
import com.caldeirasoft.castly.service.playback.extensions.isSkipToPreviousEnabled

const val NOW_PLAYING_CHANNEL: String = "com.example.android.uamp.media.NOW_PLAYING"
const val NOW_PLAYING_NOTIFICATION: Int = 0xb339

/**
 * Helper class to encapsulate code for building notifications.
 */
/*
class NotificationBuilder(private val context: Context) {
    private val platformNotificationManager: NotificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    private val skipToPreviousAction = Notification.Action.Builder(
            R.drawable.exo_controls_previous,
            context.getString(R.string.notification_skip_to_previous),
            MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_SKIP_TO_PREVIOUS))
    private val playAction = NotificationCompat.Action(
            R.drawable.exo_controls_play,
            context.getString(R.string.notification_play),
            MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_PLAY))
    private val pauseAction = NotificationCompat.Action(
            R.drawable.exo_controls_pause,
            context.getString(R.string.notification_pause),
            MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_PAUSE))
    private val skipToNextAction = NotificationCompat.Action(
            R.drawable.exo_controls_next,
            context.getString(R.string.notification_skip_to_next),
            MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_SKIP_TO_NEXT))
    private val stopPendingIntent =
            MediaButtonReceiver.buildMediaButtonPendingIntent(context, ACTION_STOP)

    fun buildNotification(sessionToken: SessionToken): Notification {
        if (shouldCreateNowPlayingChannel()) {
            createNowPlayingChannel()
        }

        val controller = MediaControllerCompat(context, sessionToken)
        val description = controller.metadata.description
        val playbackState = controller.playbackState

        val builder = NotificationCompat.Builder(context, NOW_PLAYING_CHANNEL)

        // Only add actions for skip back, play/pause, skip forward, based on what's enabled.
        var playPauseIndex = 0
        if (playbackState.isSkipToPreviousEnabled) {
            builder.addAction(skipToPreviousAction)
            ++playPauseIndex
        }
        if (playbackState.isPlaying) {
            builder.addAction(pauseAction)
        } else if (playbackState.isPlayEnabled) {
            builder.addAction(playAction)
        }
        if (playbackState.isSkipToNextEnabled) {
            builder.addAction(skipToNextAction)
        }

        val mediaStyle = MediaStyle()
                .setCancelButtonIntent(stopPendingIntent)
                .setMediaSession(sessionToken)
                .setShowActionsInCompactView(playPauseIndex)
                .setShowCancelButton(true)

        return builder.setContentIntent(controller.sessionActivity)
                .setContentText(description.subtitle)
                .setContentTitle(description.title)
                .setDeleteIntent(stopPendingIntent)
                .setLargeIcon(description.iconBitmap)
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_audiotrack_24dp)
                .setStyle(mediaStyle)
                .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
                .build()
    }

    private fun shouldCreateNowPlayingChannel() =
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !nowPlayingChannelExists()

    @RequiresApi(Build.VERSION_CODES.O)
    private fun nowPlayingChannelExists() =
            platformNotificationManager.getNotificationChannel(NOW_PLAYING_CHANNEL) != null

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNowPlayingChannel() {
        val notificationChannel = NotificationChannel(NOW_PLAYING_CHANNEL,
                context.getString(R.string.notification_channel),
                NotificationManager.IMPORTANCE_LOW)
                .apply {
                    description = context.getString(R.string.notification_channel_description)
                }

        platformNotificationManager.createNotificationChannel(notificationChannel)
    }
}
*/
