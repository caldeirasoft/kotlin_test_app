package com.caldeirasoft.basicapp.ui.podcastdetail

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.App
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.ui.adapter.IItemDataBindingAdapter
import com.caldeirasoft.basicapp.ui.adapter.viewholder.ClickAwareViewHolder
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.decorations.StickyHeaderAdapter
import com.caldeirasoft.basicapp.ui.adapter.defaultItemDiffCallback
import com.caldeirasoft.basicapp.util.RelativeTimestampGenerator
import net.cachapa.expandablelayout.ExpandableLayout
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

class PodcastDetailEpisodesAdapter<B : ViewDataBinding>(
        @LayoutRes val layoutId: Int,
        private val variableId: Int,
        val lifecycleOwner: LifecycleOwner?,
        @IdRes private val expandButtonId: Int,
        @IdRes private val expandLayoutId: Int,
        private val itemViewClickListener: ItemViewClickListener<Episode>? = null,
        private vararg val clickAwareViewIds: Int = intArrayOf()
) : PagedListAdapter<Episode, PodcastDetailEpisodesAdapter.ViewHolder<B>>(defaultItemDiffCallback<Episode>())
        , IItemDataBindingAdapter<Episode>
        , StickyHeaderAdapter<PodcastDetailEpisodesAdapter.DateHeaderViewHolder>
{
    private var selectedItem = UNSELECTED
    private var mRecyclerView: RecyclerView? = null

    override fun onBindViewHolder(holder: ViewHolder<B>, position: Int) {
        bindData(holder.binding, position, getItem(position))
        val isSelected = (position == selectedItem)
        holder.setExpanded(isSelected)
    }

    fun bindData(binding: B, position: Int, item: Episode?) {
        binding.setVariable(variableId, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder<B>
    {
        val inflater:LayoutInflater = LayoutInflater.from(parent.context)
        val binding:B = DataBindingUtil.inflate(inflater, layoutId, parent, false)
        binding.setLifecycleOwner(lifecycleOwner)
        return ViewHolder(binding, expandButtonId, expandLayoutId, this::onPositionClick, this::onExpansionUpdate, *clickAwareViewIds)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun getItem(position: Int): Episode? {
        return super.getItem(position)
    }

    fun updateItem(newItem: Episode) =
        currentList?.indexOf(newItem)?.let {
            this.notifyItemChanged(it)
        }

    fun updateItem(position:Int, newItem: Episode) {
        currentList?.let {
            it[position] = newItem
            this.notifyItemChanged(position)
        }
    }

    override fun getHeaderId(position: Int): Long {
        return getEpisodePubishedDateTime(position).toEpochSecond(ZoneOffset.UTC)
    }

    private fun onPositionClick(position: Int, @IdRes viewId: Int) {
        when (viewId) {
            expandButtonId -> onExpansionClick(position, viewId)
            else -> itemViewClickListener?.onItemClick(getItem(position), position, viewId)
        }
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): DateHeaderViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.listitem_episodes_date_left, parent, false)
        val holder = DateHeaderViewHolder(view)
        return holder
    }

    override fun onBindHeaderViewHolder(viewholder: DateHeaderViewHolder, position: Int) {
        val dateTime = getEpisodePubishedDateTime(position)
        viewholder.setDate(dateTime)
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

    private fun onExpansionUpdate(position: Int, expansionFraction: Float, state: Int) {
        when(state) {
            //ExpandableLayout.State.EXPANDING -> mRecyclerView?.smoothScrollToPosition(position)
        }
    }

    private fun getEpisodePubishedDateTime(position:Int): LocalDateTime {
        var dateTime  = getItem(position)?.let {
            Instant.ofEpochMilli(it.published).atZone(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS).toLocalDateTime()
        } ?: LocalDateTime.MIN
        return dateTime
    }

    private fun getHeaderName(position: Int): String {
        if (position < 0)
            return ""

        getItem(position)?.let {
            Instant.ofEpochMilli(it.published).let {
                return RelativeTimestampGenerator().generate(it).displayText(App.context!!)
            }
        } ?: return ""
    }

    class ViewHolder<out B:ViewDataBinding>(
            binding: B,
            @IdRes private val expandButtonId: Int,
            @IdRes private val expandLayoutId: Int,
            private val positionClick: (Int,Int) -> Unit,
            private val expansionUpdate: (Int,Float,Int) -> Unit,
            @IdRes vararg viewIds: Int = intArrayOf()
    ) : ClickAwareViewHolder<B>(binding, positionClick, *viewIds),
            ExpandableLayout.OnExpansionUpdateListener
    {
        private lateinit var expandableLayout: ExpandableLayout

        init {
            val root = binding.root
            root.setOnClickListener(this)

            root.findViewById<ExpandableLayout>(expandLayoutId)?.apply {
                expandableLayout = this
                setInterpolator(OvershootInterpolator())
                setOnExpansionUpdateListener(this@ViewHolder)
            }
            root.findViewById<View>(expandButtonId).apply {
                setOnClickListener(this@ViewHolder)
            }
        }

        override fun onClick(v: View?) {
            super.onClick(v)
        }

        override fun onExpansionUpdate(expansionFraction: Float, state: Int) {
            expansionUpdate(adapterPosition, expansionFraction, state)
        }

        fun setExpanded(expand: Boolean) {
            expandableLayout.setExpanded(expand)
        }
    }

    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private var textViewDayOfMonth: TextView
        private var textViewMonth: TextView
        init {
            textViewDayOfMonth = itemView.findViewById(R.id.textView_dayOfMonth)
            textViewMonth = itemView.findViewById(R.id.textView_month)
        }

        fun setDate(dateTime: LocalDateTime) {
            textViewDayOfMonth.text = dateTime.dayOfMonth.toString()
            textViewMonth.text = dateTime.month.getDisplayName(TextStyle.SHORT, Locale.getDefault())
        }
    }

    companion object{
        val UNSELECTED = -1
    }
}