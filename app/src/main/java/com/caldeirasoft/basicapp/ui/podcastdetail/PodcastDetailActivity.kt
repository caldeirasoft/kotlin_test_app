package com.caldeirasoft.basicapp.ui.podcastdetail

import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.data.entity.Episode
import com.caldeirasoft.basicapp.databinding.ActivityPodcastdetailBinding
import com.caldeirasoft.basicapp.ui.common.MediaPlayerBaseActivity
import com.caldeirasoft.basicapp.ui.episodedetail.EpisodeDetailDialog
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_podcastdetail.*
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.widget.ImageView
import com.caldeirasoft.basicapp.data.entity.Podcast
import com.caldeirasoft.basicapp.databinding.ListitemEpisodespodcastBinding
import com.caldeirasoft.basicapp.ui.adapter.decorations.HeaderViewDecoration
import com.caldeirasoft.basicapp.ui.adapter.ItemViewClickListener
import com.caldeirasoft.basicapp.ui.common.RecyclerHeaderHelper
import com.squareup.picasso.Callback
import android.widget.Toast
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Observer
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.databinding.library.baseAdapters.BR
import com.caldeirasoft.basicapp.data.enum.SubscribeAction
import com.caldeirasoft.basicapp.ui.adapters.setContentBinding
import com.caldeirasoft.basicapp.ui.extensions.viewModelProviders


class PodcastDetailActivity :
        MediaPlayerBaseActivity(), ItemViewClickListener<Episode> {
    private lateinit var mBinding: ActivityPodcastdetailBinding
    private val viewModel by lazy { viewModelProviders<PodcastDetailViewModel>() }
    private val podcastDetailAdapter by lazy {
        PodcastDetailEpisodesAdapter<ListitemEpisodespodcastBinding>(
                layoutId = R.layout.listitem_episodespodcast,
                variableId = BR.episode,
                lifecycleOwner = this,
                itemViewClickListener = this,
                expandButtonId = R.id.itemEpisodeConstraintLayout,
                expandLayoutId = R.id.expandable_layout,
                clickAwareViewIds =  *intArrayOf(R.id.buttonInfo)
        )
    }
    private var mHeaderHelper: RecyclerHeaderHelper = RecyclerHeaderHelper()


    protected override fun getLayout(): Int = R.layout.activity_podcastdetail

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setSupportActionBar(toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }

        // get full podcast serialized
        val podcast = intent.getParcelableExtra<Podcast>(EXTRA_FEED_ID)
        viewModel.setDataSource(podcast)
        setupThumbnail()
        setupRecyclerView()
        setupSwipeRefreshLayout()
        observePodcast()
    }

    override fun setContentView() {
        mBinding = this.setContentBinding(getLayout())
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.podcastdetail_menu, menu)
        return true
    }

    private fun setupRecyclerView() =
            with(activity_podcastdetail_recyclerView) {
                layoutManager = LinearLayoutManager(this@PodcastDetailActivity)
                addItemDecoration(DividerItemDecoration(this@PodcastDetailActivity, LinearLayoutManager.VERTICAL))
                addItemDecoration(HeaderViewDecoration(R.layout.header_podcastdetail))
                adapter = podcastDetailAdapter
            }

    private fun setupThumbnail() {
        Picasso.with(applicationContext)
                .load(viewModel.podcast.value?.imageUrl)
                .into(thumbnail, collapeCallback(thumbnail))
    }

    private fun setupSwipeRefreshLayout() =
            with (activitiy_podcastdetail_swipeRefreshLayout) {
                setOnRefreshListener {
                    viewModel.refresh()
                    this.isRefreshing = false
                }
            }

    private fun collapeCallback(imageView: ImageView): Callback {
        return object : Callback {
            override fun onSuccess() {
                val bitmap = (imageView.getDrawable() as BitmapDrawable).bitmap
                Palette.from(bitmap).generate(object : Palette.PaletteAsyncListener {
                    override fun onGenerated(palette: Palette?) {
                        val defaultColor = resources.getColor(R.color.black)
                        val vibrantColor = palette?.getDarkVibrantColor(defaultColor)?.let {

                            collapsing_toolbar.setBackgroundColor(it)
                            collapsing_toolbar.setCollapsedTitleTextColor(resources.getColor(R.color.white))
                            collapsing_toolbar.setContentScrimColor(it)
                            collapsing_toolbar.setStatusBarScrimColor(it)
                        }

                        window.apply {
                            //clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
                            //addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
                            //statusBarColor = vibrantColor
                        }
                    }
                })
            }

            override fun onError() {
            }
        }
    }

    private fun observePodcast()
    {
        viewModel.apply {

            mBinding.let {
                it.viewModel = this
                it.setLifecycleOwner(this@PodcastDetailActivity)
            }

            // setup thumbnail

            // subscribe event
            subscribePodcastEvent.observe(this@PodcastDetailActivity, Observer { podcast ->
                podcast?.let {
                    when (isInDatabase.value) {
                        false -> showSubscribeDialog(it)
                        true -> unsubscribeFromPodcast(it)
                    }
                }
            })

            // add episode to inbox
            addEpisodeToInboxEvent.observe(this@PodcastDetailActivity, Observer { _ ->
                podcastDetailAdapter.apply {
                    currentList?.firstOrNull()?.let {
                        viewModel.addFirstEpisodeToInbox(it)
                        updateItem(it)
                    }
                }
            })

            // collection
            episodes.observe(this@PodcastDetailActivity, Observer { episodes ->
                podcastDetailAdapter.submitList(episodes)
                mHeaderHelper.refreshItemDecoration()
            })
        }
    }

    override fun onItemClick(item: Episode?, position: Int, viewId: Int) {
        item?.let {
            when (viewId) {
                R.id.buttonInfo -> openEpisodeDetail(it)
                R.id.button_play -> {}
                R.id.button_queuenext -> {}
                R.id.button_queuelast -> {}
                R.id.button_archive -> viewModel.archiveEpisode(it)
                R.id.button_favorite -> {}
            }
        }
    }

    private fun openEpisodeDetail(episode: Episode)
    {
        val dialog = EpisodeDetailDialog()
        dialog.episode = episode
        dialog.podcast = viewModel.podcast.value!!
        dialog.show(this.supportFragmentManager, "episode")
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when (item!!.itemId) {
            android.R.id.home -> {
                super.onBackPressed()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    fun showSubscribeDialog(podcast: Podcast) {
        val options = applicationContext.resources?.getStringArray(R.array.subscribe_options)
        val builder = AlertDialog.Builder(this)
        //alt_bld.setIcon(R.drawable.icon);
            .setTitle("Select a Group Name")
            .setSingleChoiceItems(
                    R.array.subscribe_options,
                    -1
            ) { dialog, item ->
                Toast.makeText(applicationContext,
                        "Group Name = " + options!![item], Toast.LENGTH_SHORT).show()
                when (item) {
                    0 -> viewModel.subscribeToPodcast(podcast, SubscribeAction.INBOX)
                    1 -> viewModel.subscribeToPodcast(podcast, SubscribeAction.QUEUE_NEXT)
                    2 -> viewModel.subscribeToPodcast(podcast, SubscribeAction.QUEUE_LAST)
                    3 -> viewModel.subscribeToPodcast(podcast, SubscribeAction.ARCHIVE)
                }
                dialog.dismiss()// dismiss the alertbox after chose option
            }
        val alert = builder.create()
        alert.show()
    }

    companion object {
        const val EXTRA_FEED_ID = "FEED_ID"
    }
}
