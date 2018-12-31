package com.caldeirasoft.basicapp.ui.discover

import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.databinding.library.baseAdapters.BR
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.data.repository.ItunesSection
import com.caldeirasoft.basicapp.databinding.ListitemPodcastDiscoverBinding
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.adapter.SimpleDataBindingAdapter
import com.caldeirasoft.basicapp.ui.adapter.SimplePagedDataBindingAdapter
import com.caldeirasoft.basicapp.ui.adapter.defaultItemDiffCallback
import com.ethanhua.skeleton.Skeleton
import com.github.rubensousa.gravitysnaphelper.GravitySnapHelper
import java.util.*

class DiscoverSnapAdapter(
        var lifecycleOwner: LifecycleOwner,
        val itemViewClickListener: ItemViewClickListener<Podcast>
)
    : ListAdapter<ItunesSection, DiscoverSnapAdapter.ViewHolder>(defaultItemDiffCallback<ItunesSection>())
        , GravitySnapHelper.SnapListener
{
    override fun onSnap(position:Int) {
        Log.d("Snapped: ", position.toString() + "");
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val podcastGroup = getItem(position)
        holder.bind(podcastGroup)
        /*val padding = holder.recyclerView.resources.getDimensionPixelOffset(R.dimen.extra_padding)
        if (snap.padding) {

        }*/

        holder.recyclerView.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.HORIZONTAL, false)
            //layoutManager = GridLayoutManager(context, 3)
            //GravitySnapHelper(Gravity.START).attachToRecyclerView(this)

            adapter = SimpleDataBindingAdapter<Podcast, ListitemPodcastDiscoverBinding>(
                layoutId = R.layout.listitem_podcast_discover,
                variableId = BR.podcast,
                itemViewClickListener = itemViewClickListener,
                lifecycleOwner = lifecycleOwner,
                clickAwareViewIds = *intArrayOf(R.id.itemEpisodeConstraintLayout))
                .apply { submitList(podcastGroup.podcasts) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder
    {
        val inflater:LayoutInflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.listitem_snap_discover, parent, false)
        return ViewHolder(view)
    }

    class ViewHolder(itemView: View)
        : RecyclerView.ViewHolder(itemView)
    {
        var snapTextView: TextView
        var recyclerView: RecyclerView
        private lateinit var itunesSection:ItunesSection

        init {
            snapTextView = itemView.findViewById(R.id.snapTextView)
            recyclerView = itemView.findViewById(R.id.recyclerView)
        }

        fun bind(itunesSection: ItunesSection) {
            this.itunesSection = itunesSection
            snapTextView.text = itunesSection.name
        }
    }

    companion object{
        val UNSELECTED = -1
    }
}