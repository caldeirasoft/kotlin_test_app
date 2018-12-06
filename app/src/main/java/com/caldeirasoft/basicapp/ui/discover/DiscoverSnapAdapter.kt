package com.caldeirasoft.basicapp.ui.discover

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.App
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.databinding.ListitemPodcastDiscoverBinding
import com.caldeirasoft.basicapp.ui.adapter.IItemDataBindingAdapter
import com.caldeirasoft.basicapp.ui.adapter.viewholder.ClickAwareViewHolder
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.SimpleDataBindingAdapter
import com.caldeirasoft.basicapp.ui.adapter.decorations.StickyHeaderAdapter
import com.caldeirasoft.basicapp.ui.adapter.defaultItemDiffCallback
import com.caldeirasoft.basicapp.util.RelativeTimestampGenerator
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import com.google.android.material.button.MaterialButton
import net.cachapa.expandablelayout.ExpandableLayout
import org.threeten.bp.Instant
import org.threeten.bp.LocalDateTime
import org.threeten.bp.ZoneOffset
import org.threeten.bp.format.TextStyle
import org.threeten.bp.temporal.ChronoUnit
import java.util.*

class DiscoverSnapAdapter(var lifecycleOwner: LifecycleOwner)
    : RecyclerView.Adapter<DiscoverSnapAdapter.ViewHolder>()
        , GravitySnapHelper.SnapListener
        , ItemViewClickListener<Podcast>
{
    private var snaps = ArrayList<Snap>()

    fun addSnap(snap: Snap) {
        snaps.add(snap)
        this.notifyDataSetChanged()
    }

    override fun onSnap(position:Int) {
        Log.d("Snapped: ", position.toString() + "");
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val snap = snaps[position]
        holder.bind(snap)
        /*val padding = holder.recyclerView.resources.getDimensionPixelOffset(R.dimen.extra_padding)
        if (snap.padding) {

        }*/
        val snapAdapter = SimpleDataBindingAdapter<Podcast, ListitemPodcastDiscoverBinding>(
                layoutId = R.layout.listitem_podcast_discover,
                variableId = BR.podcast,
                itemViewClickListener = this@DiscoverSnapAdapter,
                lifecycleOwner = lifecycleOwner,
                clickAwareViewIds = *intArrayOf(R.id.itemEpisodeConstraintLayout))
        snapAdapter.submitList(snap.podcasts)

        holder.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            adapter = snapAdapter
            GravitySnapHelper(Gravity.START).attachToRecyclerView(this)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val inflater:LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.listitem_snap_discover, parent, false)
        return ViewHolder(view)
    }

    override fun onItemClick(item: Podcast?, position: Int, viewId: Int) {
        item?.let {
            //openEpisodeDetail(item)
        }
    }

    override fun getItemCount(): Int {
        return snaps.size
    }

    class ViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView)
    {
        var snapTextView: TextView
        var recyclerView: RecyclerView
        private lateinit var snap:Snap

        init {
            snapTextView = itemView.findViewById(R.id.snapTextView)
            recyclerView = itemView.findViewById(R.id.recyclerView)
        }

        fun bind(snap: Snap) {
            this.snap = snap
            snapTextView.text = snap.text
        }
    }

    companion object{
        val UNSELECTED = -1
    }
}