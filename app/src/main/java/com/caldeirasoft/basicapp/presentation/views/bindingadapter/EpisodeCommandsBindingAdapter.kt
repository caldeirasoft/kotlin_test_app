package com.caldeirasoft.basicapp.presentation.views.bindingadapter

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.view.View
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.media2.common.SessionPlayer
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.castly.domain.model.Episode
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder

@BindingAdapter(value = [
    "episode", "isPlaying", "playerState", "bufferingState",
    "onPlayClick", "onQueueNextClick", "onQueueEndClick", "onArchiveClick"], requireAll = false)
fun LinearLayout.setCommands(
        episode: Episode,
        isPlaying: Boolean?,
        playerState: Int?,
        bufferingState: Int?,
        onPlayClick: View.OnClickListener?,
        onQueueNextClick: View.OnClickListener?,
        onQueueEndClick: View.OnClickListener?,
        onArchiveClick: View.OnClickListener?) {
    val chip: Chip = createPlayChip(this, episode, isPlaying, playerState, bufferingState, onPlayClick)
    this.removeAllViewsInLayout()
    this.addView(chip)
    addUpNextChip(this, episode)
    addUpLastChip(this, episode)
}

/**
 * Create the "play" chip
 */
fun createPlayChip(
        layout: LinearLayout,
        episode: Episode,
        isPlaying: Boolean?,
        playerState: Int?,
        bufferingState: Int?,
        onPlayClick: View.OnClickListener?): Chip {
    val context = layout.context
    val chip = Chip(context)
    val chipDrawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.ChipActionStyle)

    chipDrawable.apply {
        chipIconSize = 36f
        isChipIconVisible = true

        if ((isPlaying != true) || (playerState == SessionPlayer.PLAYER_STATE_PAUSED))
        {
            val progression = 0f;
            var startValueElapsed = 0f
            var endValueElapsed = 0f
            var startValueRemaining = 0f
            var endValueRemaining = 0f
            when (progression) {
                0f -> {
                    startValueElapsed = 0f
                    endValueElapsed = 0f
                    startValueRemaining = 0f
                    endValueRemaining = 1f
                }
                1f -> {
                    startValueElapsed = 0f
                    endValueElapsed = 1f
                    startValueRemaining = 0f
                    endValueRemaining = 0f
                }
                in 0f..0.5f -> {
                    startValueElapsed = 0f
                    endValueElapsed = 0f
                    startValueRemaining = progression + 0.06f
                    endValueRemaining = 1f
                }
                in 0.95f..1f -> {
                    startValueElapsed = 0.03f
                    endValueElapsed = progression
                    startValueRemaining = 1f
                    endValueRemaining = 1f
                }
                else -> {
                    startValueElapsed = 0.03f
                    endValueElapsed = progression
                    startValueRemaining = Math.min(progression + 0.06f, 1f)
                    endValueRemaining = 1f
                }
            }

            val circularProgressDrawable = CircularProgressDrawable(context).apply {
                strokeWidth = 4f
                centerRadius = 16f
                setColorSchemeColors(Color.BLUE)
                setStartEndTrim(0.60f, 1f)
                progressRotation = 0.75f
                //start() // starts the progress spinner
            }

            val circularProgressDrawableElapsed = CircularProgressDrawable(context).apply {
                strokeWidth = 4f
                centerRadius = 16f
                setColorSchemeColors(Color.GRAY)
                setStartEndTrim(0.06f, 0.54f)
                progressRotation = 0.75f
                //start() // starts the progress spinner
            }

            //val playIcon = context.resources.getDrawable(R.drawable.ic_play_16dp, null)
            val playIcon = MaterialDrawableBuilder.with(context)
                    .setIcon(MaterialDrawableBuilder.IconValue.PLAY)
                    .setColor(Color.BLACK)
                    .setToActionbarSize()
                    .build()
                    .apply {
                        callback = object : Drawable.Callback {
                            override fun invalidateDrawable(who: Drawable) {
                                //who.invalidateSelf()
                            }
                            override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) { }
                            override fun unscheduleDrawable(who: Drawable, what: Runnable) { }
                        }
                    }

            val combineDrawable = LayerDrawable(
                    arrayOf(circularProgressDrawable, circularProgressDrawableElapsed, playIcon)
            ).apply {
                setLayerInset(0, 0, 0, 0, circularProgressDrawable.intrinsicHeight)
                setLayerInset(1, 0, 0, 0, circularProgressDrawable.intrinsicHeight)
                setLayerInset(2, 5, 5, 5, 5)
            }

            // set as icon
            chipIcon = combineDrawable
            chip.setChipDrawable(this)
        }
        else {
            if (bufferingState == SessionPlayer.BUFFERING_STATE_BUFFERING_AND_STARVED) {
                // indeterminate progressbar
                val progressDrawable = CircularProgressDrawable(context).apply {
                    strokeWidth = 4f
                    centerRadius = 16f
                    setColorSchemeColors(Color.BLUE)
                    callback = object : Drawable.Callback {
                        override fun invalidateDrawable(who: Drawable) {
                            //who.invalidateSelf()
                        }
                        override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) { }
                        override fun unscheduleDrawable(who: Drawable, what: Runnable) { }
                    }
                }
                // set as icon
                chipIcon = progressDrawable
                chip.setChipDrawable(this)
                progressDrawable.start()
            }
            else {
                // equalizer
                val equalizerIcon = ContextCompat.getDrawable(context, R.drawable.avd_equalizer) as AnimatedVectorDrawable?
                // set as icon
                chipIcon = equalizerIcon
                chip.setChipDrawable(this)
                equalizerIcon?.start()
            }
        }
    }
    chip.text = "Play"
    chip.setOnClickListener(onPlayClick)
    return chip
}

/**
 * Create and add the upNext chip
 */
fun addUpNextChip(layout: LinearLayout, episode: Episode) {
    val upNextChip = createIconChip(layout.context, episode) {
        val context = layout.context
        val upNextIcon = MaterialDrawableBuilder.with(context)
                .setIcon(MaterialDrawableBuilder.IconValue.PLAYLIST_PLUS)
                .setColor(Color.BLACK)
                .setToActionbarSize()
                .build()
        upNextIcon
    }

    layout.addView(upNextChip)
}

/**
 * Create and add the upLast chip
 */
fun addUpLastChip(layout: LinearLayout,
                     episode: Episode) {

    val upLastChip = createIconChip(layout.context, episode) {
        val context = layout.context
        val upLastIcon = context.resources.getDrawable(R.drawable.ic_add_to_bottom_24dp, null)
        upLastIcon.setTint(Color.BLUE)
        upLastIcon
    }

    layout.addView(upLastChip)
}

/**
 * Create a custom icon chip without text
 */
inline fun createIconChip(context: Context, episode: Episode, crossinline getDrawable: () -> Drawable): Chip {
    val chip = Chip(context)
    val chipDrawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.ChipActionStyle)

    chipDrawable.apply {
        chipIconSize = 36f
        isChipIconVisible = true
        textStartPadding = 0f
        textEndPadding = 0f
        closeIconStartPadding = 0f
        closeIconEndPadding = 0f

        // set as icon
        chipIcon = getDrawable()
        chip.setChipDrawable(this)
    }
    return chip
}