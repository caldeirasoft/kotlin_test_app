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
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.App
import com.caldeirasoft.basicapp.BR
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.ui.adapter.IItemDataBindingAdapter
import com.caldeirasoft.basicapp.ui.adapter.viewholder.ClickAwareViewHolder
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.decorations.StickyHeaderAdapter
import com.caldeirasoft.basicapp.ui.adapter.viewholder.BindingViewHolder
import com.caldeirasoft.basicapp.ui.episodes.ExpandableEpisodesListAdapter
import com.caldeirasoft.basicapp.util.RelativeTimestampGenerator
import net.cachapa.expandablelayout.ExpandableLayout
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

class PodcastDetailEpisodesListAdapter<B : ViewDataBinding>(
        lifecycleOwner: LifecycleOwner?,
        private val itemViewClickListener: ItemViewClickListener<Episode>? = null,
        private vararg val clickAwareViewIds: Int = intArrayOf()
) : ExpandableEpisodesListAdapter<B>(
        layoutId = R.layout.listitem_episodespodcast,
        variableId = BR.episode,
        lifecycleOwner = lifecycleOwner,
        itemViewClickListener = itemViewClickListener,
        expandTriggerLayoutId =  R.id.itemEpisodeConstraintLayout,
        expandLayoutId = R.id.expandable_layout,
        cardViewLayoutId = R.id.cv_episodespodcast
) , StickyHeaderAdapter<PodcastDetailEpisodesListAdapter.DateHeaderViewHolder>
{
    override fun getHeaderId(position: Int): Long {
        return getEpisodePubishedDateTime(position).toEpochSecond(ZoneOffset.UTC)
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

    private fun getEpisodePubishedDateTime(position:Int): LocalDateTime {
        var dateTime  = getItem(position)?.let {
            Instant.ofEpochMilli(it.published).atZone(ZoneOffset.UTC).truncatedTo(ChronoUnit.DAYS).toLocalDateTime()
        } ?: LocalDateTime.MIN
        return dateTime
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
}