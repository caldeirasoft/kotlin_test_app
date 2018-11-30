package com.caldeirasoft.basicapp.ui.filter

import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.ui.adapter.ChipAdapter
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.ItemViewSelectedListener
import com.caldeirasoft.basicapp.ui.adapter.decorations.ItemOffsetDecoration
import com.caldeirasoft.basicapp.ui.common.BindingFragment
import com.caldeirasoft.basicapp.widget.BottomSheetBehavior
import com.google.android.flexbox.FlexboxLayoutManager
import com.google.android.material.chip.Chip

abstract class BaseFilterFragment<T, B:ViewDataBinding>() : BindingFragment<B>() {

    lateinit var behavior: BottomSheetBehavior<*>

    lateinit var arrayAdapter : ChipAdapter<T>
    private var mDrawables: List<Drawable?> = mutableListOf()
    private var itemViewClickListener: ItemViewClickListener<T>? = null
    private var itemViewSelectedListener: ItemViewSelectedListener<T>? = null
    protected var onCloseListener: View.OnClickListener? = null

    fun isBehaviorInitialized() = ::behavior.isInitialized

    abstract fun getRecyclerViewId() : Int

    abstract fun getChipId() : Int

    abstract fun getChipCloseId() : Int

    abstract protected fun getSpanCount(): Int

    abstract protected fun onBindItemViewHolder(item: T?, holder: ChipAdapter.ViewHolder, drawable:Drawable?)

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //setupRecyclerView()
        setupButtonClose()
        setupButtonDismissBottomSheet()
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }

    fun setupRecyclerView() {
        val recyclerView = this.view?.findViewById<RecyclerView>(getRecyclerViewId())
        recyclerView?.let {
            arrayAdapter = object : ChipAdapter<T>(this.context!!) {
                override fun onBindViewHolder(holder: ViewHolder, position: Int) {
                    super.onBindViewHolder(holder, position)

                    val item = getItem(position)
                    val drawable = getDrawable(position)
                    onBindItemViewHolder(item, holder, drawable)
                }
            }.apply {
                setOnClickListener(itemViewClickListener)
                setOnSelectedListener(itemViewSelectedListener)
            }

            //it.layoutManager = GridLayoutManager(activity, getSpanCount())
            it.adapter = arrayAdapter
            it.layoutManager = FlexboxLayoutManager(requireContext())
            it.addItemDecoration(ItemOffsetDecoration(5))
        }
    }

    fun setupButtonClose() {
        val closeButton = this.view?.findViewById<Chip>(getChipCloseId())
        closeButton?.setOnClickListener(onCloseListener)
    }

    fun setupButtonDismissBottomSheet() {
        this.view?.findViewById<ImageButton>(R.id.imageButton_filter_close)?.run {
            setOnClickListener { button ->
                behavior?.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }
    }

    fun setItems(items:List<T>, drawables:List<Drawable?>) {
        mDrawables = drawables
        arrayAdapter.submitList(items.toList())
    }

    fun setChipAdapterItems(chipAdapter: ChipAdapter<T>, items:List<T>) {
        chipAdapter.submitList(items.toList())
    }

    fun setBottomSheetBehavior(behavior: BottomSheetBehavior<View>) {
        this.behavior = behavior
        behavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback {
            override fun onStateChanged(bottomSheet: View, state: Int) {
                when(state) {
                    BottomSheetBehavior.STATE_DRAGGING,
                    BottomSheetBehavior.STATE_SETTLING ->
                        bottomSheet.apply {
                            findViewById<ViewGroup>(R.id.cl_chipGroup_filter_section)?.run { visibility = View.VISIBLE }
                            findViewById<ViewGroup>(R.id.cl_title_filter_section)?.run { visibility = View.VISIBLE }
                        }

                    BottomSheetBehavior.STATE_COLLAPSED ->
                        this@BaseFilterFragment.view?.apply {
                            findViewById<ViewGroup>(R.id.cl_chipGroup_filter_section)?.run { visibility = View.VISIBLE }
                            findViewById<ViewGroup>(R.id.cl_title_filter_section)?.run { visibility = View.INVISIBLE }
                        }
                    BottomSheetBehavior.STATE_EXPANDED ->
                        this@BaseFilterFragment.view?.apply {
                            findViewById<ViewGroup>(R.id.cl_chipGroup_filter_section)?.run { visibility = View.INVISIBLE }
                            findViewById<ViewGroup>(R.id.cl_title_filter_section)?.run { visibility = View.VISIBLE }
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
    }


    fun setOnClickListener(listener: ItemViewClickListener<T>?) {
        itemViewClickListener = listener
    }

    fun setOnSelectedListener(listener: ItemViewSelectedListener<T>?) {
        itemViewSelectedListener = listener
    }

    fun setCloseListener(listener: View.OnClickListener?) {
        onCloseListener = listener
    }

    fun setSelectedIndex(position: Int) {
        arrayAdapter.setSelection(position)
    }

    fun getDrawable(position: Int): Drawable? =
        when (position) {
            in 0..mDrawables.size - 1 -> mDrawables[position]
            else -> null
        }
}