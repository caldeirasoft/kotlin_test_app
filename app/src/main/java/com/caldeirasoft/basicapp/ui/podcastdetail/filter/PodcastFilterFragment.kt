package com.caldeirasoft.basicapp.ui.podcastdetail.filter

import android.os.Bundle
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.BindingAdapter
import androidx.databinding.DataBindingComponent
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.enum.SectionState
import com.caldeirasoft.basicapp.databinding.FragmentPodcastdetailBinding
import com.caldeirasoft.basicapp.databinding.FragmentPodcastdetailFilterBinding
import com.caldeirasoft.basicapp.ui.adapter.ChipAdapter
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.RecyclerArrayAdapter
import com.caldeirasoft.basicapp.ui.filter.MenuFilterFragment
import com.caldeirasoft.basicapp.ui.podcastdetail.PodcastDetailViewModel
import com.caldeirasoft.basicapp.viewModelProviders
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior.Companion.STATE_COLLAPSED
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior.Companion.STATE_HIDDEN
import org.jetbrains.anko.sdk25.coroutines.onClick

class PodcastFilterFragment() : MenuFilterFragment<FragmentPodcastdetailFilterBinding>() {

    lateinit var viewModel : PodcastDetailViewModel

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?): View? {
        DataBindingUtil.setDefaultComponent(object : DataBindingComponent {
            override fun getPodcastFilterFragment(): PodcastFilterFragment {
                return this@PodcastFilterFragment
            }
        })
        mBinding = FragmentPodcastdetailFilterBinding.inflate(inflater, container, false).apply {
            setLifecycleOwner(this@PodcastFilterFragment)
        }
        return mBinding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mBinding.viewModel = viewModel

        behavior = BottomSheetBehavior.from(mBinding.cardviewBottomSheetFilterSection)

        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                when(state) {
                    BottomSheetBehavior.STATE_DRAGGING,
                    BottomSheetBehavior.STATE_SETTLING ->
                        bottomSheet.apply {
                            mBinding.clChipGroupFilterSection.visibility = View.VISIBLE
                            mBinding.clTitleFilterSection.visibility = View.VISIBLE
                        }

                    BottomSheetBehavior.STATE_COLLAPSED ->
                        this@PodcastFilterFragment.view?.apply {
                            mBinding.clChipGroupFilterSection.visibility = View.VISIBLE
                            mBinding.clTitleFilterSection.visibility = View.INVISIBLE
                        }
                    BottomSheetBehavior.STATE_EXPANDED -> {
                        mBinding.clChipGroupFilterSection.visibility = View.INVISIBLE
                        mBinding.clTitleFilterSection.visibility = View.VISIBLE
                    }
                    else -> {}
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
                Log.d("onSlide", slideOffset.toString() + "");
                when (slideOffset) {
                    0f -> bottomSheet.apply {
                        findViewById<ViewGroup>(R.id.cl_chipGroup_filter_section)?.run { visibility = View.VISIBLE }
                        findViewById<ViewGroup>(R.id.cl_title_filter_section)?.run { visibility = View.INVISIBLE }
                    }
                    1f -> bottomSheet.apply {
                        findViewById<ViewGroup>(R.id.cl_chipGroup_filter_section)?.run { visibility = View.INVISIBLE }
                        findViewById<ViewGroup>(R.id.cl_title_filter_section)?.run { visibility = View.VISIBLE }
                    }
                    else -> bottomSheet.apply {
                        findViewById<ViewGroup>(R.id.cl_chipGroup_filter_section)?.run {
                            visibility = View.VISIBLE
                            animate()?.alpha(Math.max(0f, 1 - slideOffset * 2))?.setDuration(0)?.start()
                        }
                        findViewById<ViewGroup>(R.id.cl_title_filter_section)?.run {
                            visibility = View.VISIBLE
                            animate()?.alpha(Math.max(0f, slideOffset * 2 - 1))?.setDuration(0)?.start()
                        }
                    }
                }
            }
        })

        mBinding.chipResetFilter.onClick {
            onResetSection()
        }

        observeViewModel()
    }



    override fun getSpanCount() = SPAN_NUMBER

    override fun getRecyclerViewId() = R.id.recyclerView_filter_section

    override fun getChipId(): Int  = R.id.chip_filter_section

    override fun getChipCloseId() : Int = R.id.chip_reset_filter

    override val menuRes = R.menu.filtersection_menu

    @BindingAdapter("selectedFilter")
    fun selectedFilter(recyclerView: RecyclerView,  section: Int?) {
        var arrayAdapter: ChipAdapter<String>
        if (recyclerView.adapter == null) {
            arrayAdapter = object : ChipAdapter<String>(recyclerView.context) {
                override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                    super.onBindViewHolder(holder, position)

                    val item = getItem(position)
                    val drawable = getDrawable(position)
                    onBindItemViewHolder(item, holder, drawable)
                }
            }
            setChipAdapterItems(arrayAdapter, R.menu.filtersection_menu)
            recyclerView.adapter = arrayAdapter
        }
        else {
            arrayAdapter = recyclerView.adapter as ChipAdapter<String>
        }

        when (section) {
            null -> arrayAdapter.setSelection(0)
            SectionState.QUEUE.value -> arrayAdapter.setSelection(1)
            SectionState.INBOX.value -> arrayAdapter.setSelection(2)
            SectionState.FAVORITE.value -> arrayAdapter.setSelection(3)
            SectionState.HISTORY.value -> arrayAdapter.setSelection(4)
            else -> { }
        }

        mBinding.section = arrayAdapter.getItem(arrayAdapter.selectedIndex)
    }

    @BindingAdapter("viewModel")
    fun setClickListenerForFilter(recyclerView: RecyclerView, viewModel: PodcastDetailViewModel) {
        if (recyclerView.adapter != null) {
            val arrayAdapter = recyclerView.adapter as ChipAdapter<String>
            arrayAdapter.setOnClickListener(listener = object : ItemViewClickListener<String> {
                override fun onItemClick(item: String?, position: Int, viewId: Int) {
                    when (position) {
                        0 -> viewModel.setSection(null)
                        1 -> viewModel.setSection(SectionState.QUEUE.value)
                        2 -> viewModel.setSection(SectionState.INBOX.value)
                        3 -> viewModel.setSection(SectionState.FAVORITE.value)
                        4 -> viewModel.setSection(SectionState.HISTORY.value)
                        else -> {
                        }
                    }
                }
            })
        }
    }

    fun onResetSection() {
        viewModel.setSection(null)
    }

    private fun observeViewModel() {
        viewModel.apply {
            // update section
            section.observe(this@PodcastFilterFragment, Observer { section ->
                updateFilterUI(section)
            })
        }
    }

    private fun updateFilterUI(section: Int?) {
        val hideable = section == null

        if (isBehaviorInitialized()) {
            behavior.isHideable = hideable
            behavior.skipCollapsed = hideable
            behavior.peekHeight = if (hideable) 0 else 100
            when (section) {
                null -> behavior.state = STATE_HIDDEN
                else -> behavior.state = STATE_COLLAPSED
            }
        }
    }

    private companion object {
        const val SPAN_NUMBER = 2
    }
}