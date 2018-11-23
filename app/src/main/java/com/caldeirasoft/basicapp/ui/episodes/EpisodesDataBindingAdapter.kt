package com.caldeirasoft.basicapp.ui.episodes

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.App
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.SimpleDataBindingAdapter
import com.caldeirasoft.basicapp.ui.adapter.decorations.StickyHeaderAdapter
import com.caldeirasoft.basicapp.util.RelativeTimestampGenerator
import org.threeten.bp.Instant
import org.threeten.bp.ZoneOffset


open class EpisodesDataBindingAdapter<B : ViewDataBinding>(
        @LayoutRes layoutId: Int,
        variableId: Int,
        lifecycleOwner: LifecycleOwner,
        itemViewClickListener: ItemViewClickListener<Episode>,
        vararg clickAwareViewIds: Int = intArrayOf()
) : SimpleDataBindingAdapter<Episode, B>(
        layoutId = layoutId,
        variableId = variableId,
        lifecycleOwner = lifecycleOwner,
        itemViewClickListener = itemViewClickListener,
        clickAwareViewIds = *clickAwareViewIds
), StickyHeaderAdapter<EpisodesDataBindingAdapter.DateHeaderViewHolder>
{
    override fun getHeaderId(position: Int): Long {
        return getItem(position)?.let {
            Instant.ofEpochMilli(it.published).let {
                RelativeTimestampGenerator().generateDateTime(it).toEpochSecond(ZoneOffset.UTC)
            }
        } ?: 0L
    }

    override fun onCreateHeaderViewHolder(parent: ViewGroup): DateHeaderViewHolder {
        val view:View = LayoutInflater.from(parent.context).inflate(R.layout.listitem_episodes_header, parent, false)
        val holder = DateHeaderViewHolder(view)
        return holder
    }

    override fun onBindHeaderViewHolder(viewholder: DateHeaderViewHolder, position: Int) {
        viewholder.dateTextView.text = getHeaderName(position)
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

    class DateHeaderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var dateTextView: TextView
        init {
            dateTextView = itemView as TextView
        }
    }
}