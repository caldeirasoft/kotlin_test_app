package com.caldeirasoft.basicapp.presentation.views.bindingadapter

import android.content.Context
import android.graphics.Color
import android.widget.LinearLayout
import androidx.databinding.BindingAdapter
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.presentation.ui.podcastinfo.PodcastInfoViewModel
import com.caldeirasoft.basicapp.presentation.utils.extensions.addChip
import com.caldeirasoft.basicapp.presentation.utils.extensions.applyCustomColorToChoiceChip
import com.caldeirasoft.castly.domain.model.entities.Genre
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import net.steamcrafted.materialiconlib.MaterialDrawableBuilder

@BindingAdapter("genres")
fun setGenres(chipGroup: ChipGroup, genres: List<Genre>?) {
    // Clear all Chip in Chip Group before append new Chip
    chipGroup.removeAllViews()

    // Append all Category Chip in Chip Group
    genres?.forEach { genre ->
        chipGroup.addChip { createGenreChip(it, genre) }
    }
}

/**
 * Create genre filter chip
 */
private fun createGenreChip(context: Context, genre: Genre): Chip =
        Chip(context).apply {
            ChipDrawable
                    .createFromAttributes(context, null, 0, R.style.ChipChoiceTextStyle)
                    .let {
                        setChipDrawable(it)
                    }
            text = genre.name
            tag = genre.id
            applyCustomColorToChoiceChip()
        }

@BindingAdapter("viewModel", "isSubscribed")
fun LinearLayout.setSubscription(viewModel: PodcastInfoViewModel, isSubscribed: Boolean) {
    if (this.tag is Boolean) {
        val subscribedTag = this.tag as Boolean
        if (subscribedTag == isSubscribed)
            return
    }

    this.removeAllViews()
    this.tag = isSubscribed
    when (isSubscribed) {
        false -> createSubscribeChip(context, viewModel).let { this.addView(it) }
        true -> createIsSubscribedChip(context, viewModel).let { this.addView(it) }
    }
}

private fun createSubscribeChip(context: Context, viewModel: PodcastInfoViewModel): Chip =
        Chip(context).apply {
            ChipDrawable
                    .createFromAttributes(context, null, 0, R.style.ChipChoiceTextStyle)
                    .let {
                        it.isCheckable = false
                        it.isCheckedIconVisible = false
                        it.isChipIconVisible = true
                        it.chipIcon = MaterialDrawableBuilder.with(context)
                                .setIcon(MaterialDrawableBuilder.IconValue.PLUS)
                                //.setColorResource()
                                .setColor(Color.BLACK)
                                .setToActionbarSize()
                                .build()
                        setChipDrawable(it)
                    }
            text = context.getString(R.string.button_subscribe)
            applyCustomColorToChoiceChip()

            setOnClickListener { viewModel.subscribeToPodcast() }
        }

private fun createIsSubscribedChip(context: Context, viewModel: PodcastInfoViewModel): Chip =
        Chip(context).apply {
            ChipDrawable
                    .createFromAttributes(context, null, 0, R.style.ChipChoiceTextStyle)
                    .let {
                        it.isCheckable = true
                        it.isCheckedIconVisible = true
                        it.isChipIconVisible = false
                        it.chipIcon = MaterialDrawableBuilder.with(context)
                                .setIcon(MaterialDrawableBuilder.IconValue.PLUS)
                                //.setColorResource()
                                .setColor(Color.BLACK)
                                .setToActionbarSize()
                                .build()
                        setChipDrawable(it)
                    }
            isChecked = true
            text = context.getString(R.string.button_unsubscribe)
            applyCustomColorToChoiceChip()

            setOnClickListener { viewModel.unsubscribeFromPodcast() }
            setOnCheckedChangeListener { buttonView, isChecked -> buttonView.isChecked = !isChecked }
        }

@BindingAdapter("viewModel", "isSubscribed")
fun Chip.setSubscription(viewModel: PodcastInfoViewModel, isSubscribed: Boolean) {
    if (this.tag is Boolean) {
        val subscribedTag = this.tag as Boolean
        if (subscribedTag == isSubscribed)
            return
    }

    this.tag = isSubscribed
    when (isSubscribed) {
        false -> applySubscribeStyle(viewModel)
        true -> applyIsSubscribedStyle(viewModel)
    }
}

private fun Chip.applySubscribeStyle(viewModel: PodcastInfoViewModel) {
    ChipDrawable
            .createFromAttributes(context, null, 0, R.style.ChipChoiceTextStyle)
            .let {
                it.isCheckable = false
                it.isCheckedIconVisible = false
                it.isChipIconVisible = true
                it.chipIcon = MaterialDrawableBuilder.with(context)
                        .setIcon(MaterialDrawableBuilder.IconValue.PLUS)
                        //.setColorResource()
                        .setColor(Color.BLACK)
                        .setToActionbarSize()
                        .build()
                setChipDrawable(it)
            }
    text = context.getString(R.string.button_subscribe)
    applyCustomColorToChoiceChip()
    setOnClickListener { viewModel.subscribeToPodcast() }
}

private fun Chip.applyIsSubscribedStyle(viewModel: PodcastInfoViewModel) {
    ChipDrawable
            .createFromAttributes(context, null, 0, R.style.ChipChoiceTextStyle)
            .let {
                it.isCheckable = true
                it.isCheckedIconVisible = true
                it.isChipIconVisible = false
                it.chipIcon = MaterialDrawableBuilder.with(context)
                        .setIcon(MaterialDrawableBuilder.IconValue.PLUS)
                        //.setColorResource()
                        .setColor(Color.BLACK)
                        .setToActionbarSize()
                        .build()
                setChipDrawable(it)
            }
    isChecked = true
    text = context.getString(R.string.button_unsubscribe)
    applyCustomColorToChoiceChip()

    setOnClickListener { viewModel.unsubscribeFromPodcast() }
    setOnCheckedChangeListener { buttonView, isChecked -> buttonView.isChecked = !isChecked }
}

