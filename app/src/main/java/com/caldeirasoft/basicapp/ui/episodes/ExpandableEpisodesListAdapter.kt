package com.caldeirasoft.basicapp.ui.episodes

import android.animation.ObjectAnimator
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.cardview.widget.CardView
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.App
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.ui.adapter.IItemDataBindingAdapter
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.PagedDataBindingAdapter
import com.caldeirasoft.basicapp.ui.adapter.decorations.StickyHeaderAdapter
import com.caldeirasoft.basicapp.ui.adapter.defaultItemDiffCallback
import com.caldeirasoft.basicapp.ui.adapter.viewholder.BindingViewHolder
import com.caldeirasoft.basicapp.ui.adapter.viewholder.ClickAwareViewHolder
import com.caldeirasoft.basicapp.util.RelativeTimestampGenerator
import net.cachapa.expandablelayout.ExpandableLayout
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

abstract class ExpandableEpisodesListAdapter<B : ViewDataBinding>(
        @LayoutRes val layoutId: Int,
        private val variableId: Int,
        lifecycleOwner: LifecycleOwner?,
        @IdRes private val expandTriggerLayoutId: Int,
        @IdRes private val expandLayoutId: Int,
        @IdRes private val cardViewLayoutId: Int,
        private val itemViewClickListener: ItemViewClickListener<Episode>? = null,
        private vararg val clickAwareViewIds: Int = intArrayOf()
) : PagedDataBindingAdapter<Episode, B>(layout = layoutId, itemDiffCallback = defaultItemDiffCallback(), lifecycleOwner = lifecycleOwner, itemViewClickListener = itemViewClickListener)
        , IItemDataBindingAdapter<Episode>
{
    private var selectedItem = UNSELECTED
    private var mRecyclerView: RecyclerView? = null

    override fun onBindViewHolder(viewHolder: BindingViewHolder<B>, position: Int) {
        super.onBindViewHolder(viewHolder, position)
        if (viewHolder is ViewHolder) {
            viewHolder.apply{
                val isSelected = (position == selectedItem)
                setExpanded(isSelected)
            }
        }
    }

    override fun bindData(binding: B, position: Int, item: Episode?) {
        binding.setVariable(variableId, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BindingViewHolder<B> =
            ViewHolder(layoutRes = layout, parent = parent, positionClick = this::onPositionClick,
                    lifecycleOwner = lifecycleOwner, expandTriggerLayoutId = expandTriggerLayoutId, expandLayoutId = expandLayoutId,
                    cardViewLayoutId = cardViewLayoutId)

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    private fun onPositionClick(position: Int, @IdRes viewId: Int) {
        when (viewId) {
            expandTriggerLayoutId -> onExpansionClick(position, viewId)
            else -> itemViewClickListener?.onItemClick(getItem(position), position, viewId)
        }
    }

    private fun onExpansionClick(position: Int, @IdRes viewId: Int) {
        mRecyclerView?.let {
            it.findViewHolderForAdapterPosition(selectedItem)?.let {
                val holder = it as (ViewHolder<*>)
                holder.setExpanded(false)
            }

            when (position) {
                selectedItem -> selectedItem = UNSELECTED
                else -> {
                    it.findViewHolderForAdapterPosition(position)?.let {
                        val holder = it as (ViewHolder<*>)
                        holder.setExpanded(true)
                    }
                    selectedItem = position
                }
            }

        }
    }

    class ViewHolder<out B:ViewDataBinding>(
            binding: B,
            @IdRes private val expandTriggerLayoutId: Int,
            @IdRes private val expandLayoutId: Int,
            @IdRes private val cardViewLayoutId: Int,
            private val positionClick: (Int,Int) -> Unit,
            @IdRes vararg viewIds: Int = intArrayOf()
    ) : ClickAwareViewHolder<B>(binding, positionClick, *viewIds) {
        private lateinit var cardViewLayout: CardView
        private lateinit var expandableLayout: ExpandableLayout

        init {
            val root = binding.root
            root.setOnClickListener(this)

            root.findViewById<ExpandableLayout>(expandLayoutId)?.apply {
                expandableLayout = this
                setInterpolator(OvershootInterpolator())
                //setOnExpansionUpdateListener(this@ExpandableEpisodeViewHolder)
            }
            root.findViewById<CardView>(cardViewLayoutId)?.apply {
                cardViewLayout = this
            }
            root.findViewById<View>(expandTriggerLayoutId).apply {
                setOnClickListener(this@ViewHolder)
            }
        }

        constructor(@LayoutRes layoutRes: Int, parent: ViewGroup, lifecycleOwner: LifecycleOwner?, positionClick: (Int, Int) -> Unit, @IdRes expandTriggerLayoutId: Int, @IdRes expandLayoutId: Int, @IdRes cardViewLayoutId: Int)
                : this(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutRes, parent, false), expandTriggerLayoutId, expandLayoutId, cardViewLayoutId, positionClick) {
            binding.setLifecycleOwner(lifecycleOwner)
        }

        fun setExpanded(expand: Boolean) {
            expandableLayout.setExpanded(expand)
            val animator = ObjectAnimator.ofFloat(
                    cardViewLayout,
                    "cardElevation",
                    cardViewLayout.cardElevation,
                    if (expand) 5f else 0f
            )
            animator.start()
        }
    }


    companion object{
        val UNSELECTED = -1
    }
}