package com.caldeirasoft.basicapp.presentation.ui.inbox

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.amulyakhare.textdrawable.TextDrawable
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.databinding.FragmentEpisodelistBinding
import com.caldeirasoft.basicapp.presentation.ui.base.BindingFragment
import com.caldeirasoft.basicapp.presentation.utils.ThemeHelper
import com.caldeirasoft.basicapp.presentation.utils.epoxy.EpisodesGroupByDateController
import com.caldeirasoft.basicapp.presentation.utils.extensions.observeK
import com.caldeirasoft.castly.domain.model.Podcast
import com.caldeirasoft.castly.domain.repository.PlayerRepository
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipDrawable
import com.google.android.material.chip.ChipGroup
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_episodelist.*
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel

class InboxFragment : BindingFragment<FragmentEpisodelistBinding>() {

    // viewmodel
    val mViewModel: InboxViewModel by viewModel()

    // player repository //TODO: replace by viewmodel
    val playerRepository: PlayerRepository by inject()

    // epoxy controller
    private val controller by lazy {
        EpisodesGroupByDateController(
                requireContext(),
                childFragmentManager,
                this,
                playerRepository)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        mBinding = FragmentEpisodelistBinding.inflate(inflater, container, false)
        mBinding.let {
            it.lifecycleOwner = this
            it.title = context?.getString(R.string.inbox)
            it.viewModel = mViewModel
            return it.root
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
        initObservers()
    }

    private fun initObservers() {
        mViewModel.dataItems.observeK(this) { data ->
            controller.submitList(data)
        }

        mViewModel.darkThemeOn.observeK(this) { theme ->
            when (theme) {
                true -> ThemeHelper.applyTheme(ThemeHelper.DARK_MODE)
                else -> ThemeHelper.applyTheme(ThemeHelper.LIGHT_MODE)
            }
        }
    }

    private fun initUi() {
        mBinding.recyclerView.apply {
            setController(controller)
            addItemDecoration(DividerItemDecoration(activity, LinearLayoutManager.VERTICAL))
        }
    }
}
