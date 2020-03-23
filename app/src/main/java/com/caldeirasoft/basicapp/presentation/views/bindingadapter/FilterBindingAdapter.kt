package com.caldeirasoft.basicapp.presentation.views.bindingadapter

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import androidx.annotation.Dimension
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.databinding.BindingAdapter
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.presentation.views.TextDrawable
import com.caldeirasoft.castly.domain.model.PodcastWithCount
import com.caldeirasoft.castly.domain.model.SectionState
import com.caldeirasoft.castly.domain.model.SectionWithCount
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.google.android.material.resources.MaterialResources
import com.google.android.material.resources.TextAppearance
import com.squareup.picasso.Picasso
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder

@BindingAdapter("podcasts")
fun setFilters(chipGroup: ChipGroup, podcastsWithCount: List<PodcastWithCount>?) {
    // Clear all Chip in Chip Group before append new Chip
    chipGroup.removeAllViews()

    // get total count
    val totalCount: Int = podcastsWithCount?.sumBy { it.episodeCount } ?: 0

    // add main button
    createAllFiltersChip(chipGroup.context, totalCount).let {
        chipGroup.addView(it)
    }

    // Append all Category Chip in Chip Group
    podcastsWithCount?.let { list ->
        for (podcast in list) {
            createPodcastChip(chipGroup.context, podcast).let {
                chipGroup.addView(it)
            }
        }
    }
}

/**
 * Create all filter chip
 */
private fun createAllFiltersChip(context: Context, totalCount: Int? = null): Chip =
        Chip(context).apply {
            ChipDrawable
                    .createFromAttributes(context, null, 0, R.style.ChipChoiceAllStyle)
                    .let {
                        it.isCloseIconVisible = true
                        it.chipBackgroundColor = getChipBackgroundColorState(context, Color.BLUE)
                        setChipDrawable(it)
                    }
            text = "All"
            setTextColor(ContextCompat.getColorStateList(context, R.color.choice_chip_text_color))
            totalCount?.let { count ->
                setBadgeDrawable(this, count)
            }
        }

/**
 * Create podcast filter chip
 */
private fun createPodcastChip(context: Context, podcast: PodcastWithCount): Chip {
    val chipDrawable = ChipDrawable
            .createFromAttributes(context, null, 0, R.style.ChipChoiceIconStyle)
            .apply {
                isChipIconVisible = true
                isCheckable = true
                isCloseIconVisible = true
                textStartPadding = 0f
                textEndPadding = 0f
                chipBackgroundColor = getChipBackgroundColorState(context, Color.GREEN)
            }
    val chip = Chip(context).apply {
        setChipDrawable(chipDrawable)
        text = ""//pod.name
        tag = podcast.id
        setOnCheckedChangeListener { _, _ ->
            //viewModel.onCategoryFilterChanged(button.text as String, isChecked)
        }
        loadImageFromWebURL(this, podcast.getArtwork(100))
        setBadgeDrawable(this, podcast.episodeCount)
        setOnCloseIconClickListener {
            isChecked = true
        }
    }
    return chip
}

@BindingAdapter("sectionsFilters")
fun setSectionsFilters(chipGroup: ChipGroup, sectionWithCount: SectionWithCount?) {
    // Clear all Chip in Chip Group before append new Chip
    chipGroup.removeAllViews()

    // Add All button
    createAllFiltersChip(chipGroup.context).let {
        chipGroup.addView(it)
    }

    // Append all section with count
    sectionWithCount?.let {
        if (sectionWithCount.InboxCount != 0)
            createSectionChip(chipGroup.context, SectionState.INBOX.value, sectionWithCount.InboxCount)
        if (sectionWithCount.QueueCount != 0)
            createSectionChip(chipGroup.context, SectionState.QUEUE.value, sectionWithCount.QueueCount)
        if (sectionWithCount.FavoritesCount != 0)
            createSectionChip(chipGroup.context, SectionState.FAVORITE.value, sectionWithCount.FavoritesCount)
        if (sectionWithCount.HistoryCount != 0)
            createSectionChip(chipGroup.context, SectionState.HISTORY.value, sectionWithCount.HistoryCount)
    }
}

/**
 * Create section filter chip
 */
private fun createSectionChip(context: Context, section: Int, count: Int): Chip {
    val chipDrawable = ChipDrawable
            .createFromAttributes(context, null, 0, R.style.ChipChoiceIconStyle)
            .apply {
                isChipIconVisible = true
                isCheckable = true
                isCloseIconVisible = true
                textStartPadding = 0f
                textEndPadding = 0f
                chipIcon = MaterialDrawableBuilder.with(context)
                    .setIcon(getSectionIcon(section))
                    .setColor(Color.BLACK)
                    .setToActionbarSize()
                    .build()
                chipBackgroundColor = getChipBackgroundColorState(context, Color.BLUE)
            }

    val chip = Chip(context).apply {
        setChipDrawable(chipDrawable)
        text = ""//pod.name
        setOnCheckedChangeListener { _, _ ->
            //viewModel.onCategoryFilterChanged(button.text as String, isChecked)
        }
        setBadgeDrawable(this, count)
        setOnCloseIconClickListener {
            isChecked = true
        }
    }
    return chip
}

/**
 * Get section icon
 */
private fun getSectionIcon(section: Int): MaterialDrawableBuilder.IconValue =
        when (section) {
            SectionState.INBOX.value ->
                MaterialDrawableBuilder.IconValue.INBOX
            SectionState.QUEUE.value ->
                MaterialDrawableBuilder.IconValue.PLAYLIST_PLAY
            SectionState.FAVORITE.value ->
                MaterialDrawableBuilder.IconValue.STAR
            SectionState.HISTORY.value ->
                MaterialDrawableBuilder.IconValue.HISTORY
            else ->
                MaterialDrawableBuilder.IconValue.ALL_INCLUSIVE
        }

/**
 * Set unplayed number as drawable for a chip
 */
private fun setBadgeDrawable(chip: Chip, number: Int) {
    val context = chip.context
    val ta: TypedArray =
            context.obtainStyledAttributes(R.style.TextAppearance_MaterialComponents_Body1, androidx.appcompat.R.styleable.TextAppearance);

    val drawable = TextDrawable(chip.context).apply {
                text = number.toString()
                setTextSize(Dimension.SP, 10f)
                ta.getColorStateList(androidx.appcompat.R.styleable.TextAppearance_android_textColor)?.let {
                    setTextColor(it)
                }
            }
    chip.closeIcon = drawable
}

/**
 * Set podcast image for drawable chip
 */
private fun loadImageFromWebURL(chip: Chip, url: String) {
    Picasso.get()
            .load(url)
            .placeholder(R.drawable.gradient_background)
            .into(object : com.squareup.picasso.Target {
                override fun onBitmapLoaded(bitmap: Bitmap, from: Picasso.LoadedFrom) {
                    //view.background = BitmapDrawable(view?.context?.resources, bitmap)
                    val drawable = RoundedBitmapDrawableFactory.create(chip.context.resources, bitmap)
                    drawable.isCircular = true
                    chip.chipIcon = drawable
                }

                override fun onPrepareLoad(placeHolderDrawable: Drawable?) {
                    //TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                    chip.chipIcon = placeHolderDrawable
                }

                override fun onBitmapFailed(e: Exception?, errorDrawable: Drawable?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }
            })
}

private fun getChipBackgroundColorState(context: Context, color: Int): ColorStateList {
    val states = arrayOf(
            intArrayOf(android.R.attr.state_selected),
            intArrayOf(-android.R.attr.state_selected),
            intArrayOf(-android.R.attr.state_enabled)
    )
    val colors = intArrayOf(color, android.R.attr.colorBackground, android.R.attr.colorBackground)
    return ColorStateList(states, colors)
}
