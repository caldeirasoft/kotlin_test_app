package com.caldeirasoft.basicapp.presentation.views.bindingadapter

import android.content.Context
import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.style.TextAppearanceSpan
import android.view.View
import androidx.databinding.BindingAdapter
import coil.Coil
import coil.api.load
import coil.transform.CircleCropTransformation
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.presentation.utils.extensions.addChip
import com.caldeirasoft.basicapp.presentation.utils.extensions.applyCustomColorToChoiceChip
import com.caldeirasoft.basicapp.presentation.utils.extensions.mtrl_choice_chip_text_color
import com.caldeirasoft.castly.domain.model.entities.PodcastWithCount
import com.caldeirasoft.castly.domain.model.entities.SectionState
import com.caldeirasoft.castly.domain.model.entities.SectionWithCount
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder

@BindingAdapter("podcasts")
fun setFilters(chipGroup: ChipGroup, podcastsWithCount: List<PodcastWithCount>?) {
    // Clear all Chip in Chip Group before append new Chip
    chipGroup.removeAllViews()

    // get total count
    val totalCount: Int = podcastsWithCount?.sumBy { it.episodeCount } ?: 0

    // add main button
    chipGroup.addChip { createAllFiltersChip(context = it, totalCount = totalCount) }

    // Append all Category Chip in Chip Group
    podcastsWithCount?.forEach { podcast ->
        chipGroup.addChip { createPodcastChip(context = it, podcast = podcast) }
    }
}

/**
 * Create all filter chip
 */
private fun createAllFiltersChip(context: Context, totalCount: Int? = null): Chip =
        Chip(context).apply {
            ChipDrawable
                    .createFromAttributes(context, null, 0, R.style.ChipChoiceTextStyle)
                    .let {
                        setChipDrawable(it)
                    }
            text = getTextAndCount(this, "All", totalCount)
            applyCustomColorToChoiceChip()
        }

/**
 * Create podcast filter chip
 */
private fun createPodcastChip(context: Context, podcast: PodcastWithCount): Chip =
        Chip(context).apply {
            ChipDrawable
                    .createFromAttributes(context, null, 0, R.style.ChipChoiceIconStyle)
                    .let { setChipDrawable(it) }

            text = getTextAndCount(this, null, podcast.episodeCount)//pod.name
            tag = podcast.id
            setOnCheckedChangeListener { _, _ ->
                //viewModel.onCategoryFilterChanged(button.text as String, isChecked)
            }
            loadImageFromWebURL(this, podcast.getArtwork(100))
            applyCustomColorToChoiceChip()
        }

@BindingAdapter("sectionsFilters")
fun setSectionsFilters(chipGroup: ChipGroup, sectionWithCount: SectionWithCount?) {
    // Clear all Chip in Chip Group before append new Chip
    chipGroup.removeAllViews()

    // Add All button
    chipGroup.addChip { createAllFiltersChip(context = it) }

    // Append all section with count
    sectionWithCount?.let {
        if (sectionWithCount.InboxCount != 0)
            chipGroup.addChip { createSectionChip(it, SectionState.INBOX.value, sectionWithCount.InboxCount) }
        if (sectionWithCount.QueueCount != 0)
            chipGroup.addChip { createSectionChip(it, SectionState.QUEUE.value, sectionWithCount.QueueCount) }
        if (sectionWithCount.FavoritesCount != 0)
            chipGroup.addChip { createSectionChip(it, SectionState.FAVORITE.value, sectionWithCount.FavoritesCount) }
        if (sectionWithCount.HistoryCount != 0)
            chipGroup.addChip { createSectionChip(it, SectionState.HISTORY.value, sectionWithCount.HistoryCount) }
    }
}

/**
 * Create section filter chip
 */
private fun createSectionChip(context: Context, section: Int, count: Int): Chip =
        Chip(context).apply {
            ChipDrawable
                    .createFromAttributes(context, null, 0, R.style.ChipChoiceIconStyle)
                    .let {
                        setChipDrawable(it)
                    }
            text = ""//pod.name
            applyCustomColorToChoiceChip()
            setOnCheckedChangeListener { _, _ ->
                //viewModel.onCategoryFilterChanged(button.text as String, isChecked)
            }
            chipIcon = MaterialDrawableBuilder.with(context)
                    .setIcon(getSectionIcon(section))
                    .setColor(Color.BLACK)
                    .setToActionbarSize()
                    .build()
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
 * Set podcast image for drawable chip
 */
private fun loadImageFromWebURL(chip: Chip, url: String) {
    Coil.load(chip.context, url) {
        placeholder(R.drawable.gradient_background)
        transformations(CircleCropTransformation())
        target { drawable ->
            chip.chipIcon = drawable
        }
    }
    /*Picasso.get()
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
            })*/
}

private fun getTextAndCount(view: View, text: String?, count: Int?): CharSequence =
        SpannableString("${text ?: ""} ${count ?: ""}").apply {
            setSpan(
                    TextAppearanceSpan(null,
                            Typeface.DEFAULT.style,
                            view.context.resources.getDimensionPixelSize(R.dimen.mtrl_chip_text_size),
                            view.mtrl_choice_chip_text_color(),
                            null),
                    0,
                    text?.length ?: 0,
                    Spannable.SPAN_INCLUSIVE_INCLUSIVE
            )
            count?.let {
                setSpan(
                        //mtrl_badge_text_size
                        TextAppearanceSpan(null,
                                Typeface.create(Typeface.DEFAULT, Typeface.ITALIC).style,
                                view.context.resources.getDimensionPixelSize(R.dimen.mtrl_badge_text_size),
                                view.mtrl_choice_chip_text_color(),
                                null),
                        (text?.length ?: 0) + 1,
                        length,
                        Spannable.SPAN_INCLUSIVE_INCLUSIVE
                )
            }
        }
