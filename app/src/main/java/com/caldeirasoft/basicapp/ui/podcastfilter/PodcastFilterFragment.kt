package com.caldeirasoft.basicapp.ui.podcastfilter

import android.app.Dialog
import android.view.*
import com.allattentionhere.fabulousfilter.AAH_FabulousFragment
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.extensions.lazyArg
import kotlinx.android.synthetic.main.fragment_podcastfilter.*

class PodcastFilterFragment : AAH_FabulousFragment() {

    val podcast by lazyArg<Podcast>(EXTRA_FEED_ID)

    //private val mViewModel by lazy { viewModelProviders<PodcastDetailViewModel>() }
    //private lateinit var podcastDetailFragmentAdapter : PodcastDetailFragmentAdapter

    fun getLayout() = R.layout.fragment_podcastfilter

    override fun setupDialog(dialog: Dialog, style: Int) {
        val contentView = View.inflate(context, getLayout(), null)

        val content = contentView.findViewById<View>(R.id.rl_content)
        setViewMain(content)
        setViewgroupStatic(ll_buttons)
        setMainContentView(contentView)
        super.setupDialog(dialog, style)
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"

        fun newInstance(): PodcastFilterFragment {
            val fragment = PodcastFilterFragment()
            return fragment
        }
    }
}