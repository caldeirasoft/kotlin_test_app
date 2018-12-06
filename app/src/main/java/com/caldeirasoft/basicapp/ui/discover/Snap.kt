package com.caldeirasoft.basicapp.ui.discover

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
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.ui.adapter.IItemDataBindingAdapter
import com.caldeirasoft.basicapp.ui.adapter.viewholder.ClickAwareViewHolder
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
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

class Snap(var gravity:Int,
           var text:String,
           var padding: Boolean,
           var podcasts: List<Podcast>)
{
}