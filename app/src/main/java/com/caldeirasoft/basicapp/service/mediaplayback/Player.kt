package com.caldeirasoft.basicapp.service.mediaplayback

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.Build
import android.os.CountDownTimer
import android.os.PowerManager
import android.support.v4.media.MediaMetadataCompat
import android.support.v4.media.session.PlaybackStateCompat
import android.util.Log


class Player(private val appContext: Context,
             private val listener: PlayerListener,
             private val audioManager: AudioManager)
    : AudioManager.OnAudioFocusChangeListener, BroadcastReceiver() {

    private val TAG = "Player"
    private val mediaPlayer: MediaPlayer = MediaPlayer()
    private var countDownTimer: CountDownTimer? = null
    private var timerMilliseconds: Long = 0L
    private var timerLabel: String? = null
    private val intentFilter = IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY)


    init {
        mediaPlayer.setWakeMode(appContext, PowerManager.PARTIAL_WAKE_LOCK)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val audioAttributes = AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SPEECH)
                    .build()

            mediaPlayer.setAudioAttributes(audioAttributes)
        } else {
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC)

        mediaPlayer.setVolume(1.0f, 1.0f)
        mediaPlayer.setOnCompletionListener { mediaPlayer ->
            mediaPlayer.release()
        }
        appContext.registerReceiver(this, intentFilter)
    }

    fun isPlaying() = mediaPlayer.isPlaying

    fun play(currentMedia: MediaMetadataCompat? = null) {
        @Suppress("DEPRECATION")
        val result = audioManager
                .requestAudioFocus(this, AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN)

        if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
            if (currentMedia != null) {
               playMediaId(currentMedia)
                //eventLogger.logPlayedPodcast(currentMedia)
            } else {
                mediaPlayer.start()
                listener.onPlaybackStatusChanged(PlaybackStateCompat.STATE_PLAYING)
            }
        }
    }

    private fun playMediaId(currentMedia: MediaMetadataCompat) {
        mediaPlayer.reset()
        val mediaUri = currentMedia.getString(MediaMetadataCompat.METADATA_KEY_MEDIA_URI)
        Log.d(TAG, mediaUri)
        mediaPlayer.setDataSource(mediaUri)
        mediaPlayer.setOnPreparedListener {
            play()
        }
        mediaPlayer.setOnCompletionListener {
            listener.onCompletion()
        }
        mediaPlayer.prepareAsync()
        listener.onPlaybackStatusChanged(PlaybackStateCompat.STATE_BUFFERING)
    }

    fun pause() {
        mediaPlayer.pause()
        listener.onPlaybackStatusChanged(PlaybackStateCompat.STATE_PAUSED)
        @Suppress("DEPRECATION")
        audioManager.abandonAudioFocus(this)
    }

    fun getCurrentPosition(): Long = mediaPlayer.currentPosition.toLong()

    fun release() {
        mediaPlayer.stop()
        mediaPlayer.reset()
        mediaPlayer.release()

        listener.onPlaybackStatusChanged(PlaybackStateCompat.STATE_STOPPED)

        @Suppress("DEPRECATION")
        audioManager.abandonAudioFocus(this)
        countDownTimer?.cancel()
        appContext.unregisterReceiver(this)
    }

    fun seekTo(pos: Long) {
        mediaPlayer.seekTo((pos).toInt())
        notifyListener()
    }

    fun rewind() {
        mediaPlayer.seekTo(mediaPlayer.currentPosition - 30000)
        notifyListener()
    }

    fun forward() {
        mediaPlayer.seekTo(mediaPlayer.currentPosition + 30000)
        notifyListener()
    }

    override fun onAudioFocusChange(focusChange: Int) {
        when (focusChange) {
            AudioManager.AUDIOFOCUS_GAIN -> {
                Log.d(TAG, "resume playback")
                mediaPlayer.setVolume(1.0f, 1.0f)
            }
            AudioManager.AUDIOFOCUS_LOSS, AudioManager.AUDIOFOCUS_LOSS_TRANSIENT -> {
                Log.d(TAG, "Stop playback but don't release media player")
                pause()
            }
            AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK -> {
                Log.d(TAG, "keep playing at an attenuated level")
                mediaPlayer.setVolume(0.1f, 0.1f)
            }
        }
    }

    private fun notifyListener() {
        if (mediaPlayer.isPlaying) {
            listener.onPlaybackStatusChanged(PlaybackStateCompat.STATE_PLAYING)
        } else {
            listener.onPlaybackStatusChanged(PlaybackStateCompat.STATE_PAUSED)
        }
    }

    fun setSleepTimer(milliseconds: Long?, label: String?) {
        milliseconds?.let {
            timerMilliseconds = milliseconds
            timerLabel = label
            if (milliseconds == 0L) {
                countDownTimer?.cancel()
            } else {
                countDownTimer = object: CountDownTimer(milliseconds, 1000) {
                    override fun onTick(millisUntilFinished: Long) {}

                    override fun onFinish() {
                        timerMilliseconds = 0
                        pause()
                    }
                }.start()
            }
            notifyListener()
        }
    }

    fun getTimerMilliseconds() = timerMilliseconds

    fun getTimerLabel() = timerLabel

    override fun onReceive(context: Context, intent: Intent?) {
        Log.d(TAG, "onReceive()")
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY == intent?.action) {
            pause()
        }
    }
}