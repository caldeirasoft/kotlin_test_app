package com.caldeirasoft.basicapp.presentation.views

import android.content.Context
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.AnimatedVectorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.content.ContextCompat
import androidx.databinding.BindingAdapter
import androidx.swiperefreshlayout.widget.CircularProgressDrawable
import com.caldeirasoft.basicapp.R
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder
import kotlin.properties.Delegates

class PlayButton
    : FrameLayout {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet?) : this(context, attrs, 0)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)


    private var playChip: Chip? = null

    private var playState: Int by Delegates.observable(PlayState.IDLE.index) { _, oldValue, newValue ->
        if (oldValue == newValue) return@observable
        drawPlayChip()
    }

    private var progress: Float by Delegates.observable(0f) { _, oldValue, newValue ->
        if (oldValue == newValue) return@observable

        if (playState == PlayState.IDLE.index)
            drawPlayChip()
    }

    companion object {
        @JvmStatic
        @BindingAdapter("app:playState")
        fun setPlayState(view: PlayButton, playState: Int?) {
            playState?.let {
                view.playState = it
            }
        }

        @JvmStatic
        @BindingAdapter("app:progress")
        fun setProgress(view: PlayButton, progress: Float?) {
            progress?.let {
                view.progress = it
            }
        }

        @JvmStatic
        @BindingAdapter("app:onButtonClick")
        fun setOnClickListener(view: PlayButton, onClick: View.OnClickListener?) {
            view.setChipOnClickListener(onClick)
        }

    }

    init {
        val typedArray = context.obtainStyledAttributes(0, R.styleable.PlayButton)

        playState = typedArray.getInt(R.styleable.PlayButton_playStateValue, PlayState.IDLE.index)
        progress = typedArray.getFloat(R.styleable.PlayButton_progressValue, 0f)
        drawPlayChip()
        typedArray.recycle()
    }

    /**
     * Create the "play" chip
     */
    fun drawPlayChip() {
        val context = getContext()
        val chip = Chip(context)
        val chipDrawable = ChipDrawable.createFromAttributes(context, null, 0, R.style.ChipActionStyle)

        chipDrawable.apply {
            chipIconSize = 36f
            isChipIconVisible = true

            when (playState) {
                PlayState.IDLE.index -> {
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

                                    override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {}
                                    override fun unscheduleDrawable(who: Drawable, what: Runnable) {}
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
                PlayState.BUFFERING.index -> {
                    // indeterminate progressbar
                    val progressDrawable = CircularProgressDrawable(context).apply {
                        strokeWidth = 4f
                        centerRadius = 16f
                        setColorSchemeColors(Color.BLUE)
                        callback = object : Drawable.Callback {
                            override fun invalidateDrawable(who: Drawable) {
                                //who.invalidateSelf()
                            }

                            override fun scheduleDrawable(who: Drawable, what: Runnable, `when`: Long) {}
                            override fun unscheduleDrawable(who: Drawable, what: Runnable) {}
                        }
                    }
                    // set as icon
                    chipIcon = progressDrawable
                    chip.setChipDrawable(this)
                    progressDrawable.start()
                }
                else -> {
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
        this.removeAllViews()
        this.addView(chip)
        playChip = chip
    }

    fun setChipOnClickListener(onClick: View.OnClickListener?) {
        playChip?.setOnClickListener(onClick)
    }

    internal enum class PlayState(internal val index: Int) {
        IDLE(0),
        BUFFERING(1),
        PLAYING(2),
        PLAYED(3);

        companion object {
            fun from(attrs: TypedArray): PlayState {
                val index = attrs.getInt(R.styleable.PlayButton_playStateValue, IDLE.index)
                return values().first { it.index == index }
            }
        }
    }
}