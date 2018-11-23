package com.caldeirasoft.basicapp.ui.adapter.viewholder

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.LayoutRes
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.caldeirasoft.basicapp.R
import com.caldeirasoft.basicapp.util.LoadingState


open class FooterViewHolder<out B: ViewDataBinding>(binding: B)
    : BindingViewHolder<B>(binding) {

    lateinit var pbLoading : ProgressBar
    lateinit var tvLoading: TextView
    lateinit var tvLoadingMore: TextView

    init {
        binding.root.let {
            pbLoading = it.findViewById(R.id.progress_loading)
            tvLoading = it.findViewById(R.id.tv_loading)
            tvLoadingMore = it.findViewById(R.id.tv_loading_more)
        }
    }

    constructor(@LayoutRes layoutRes: Int, parent: ViewGroup)
            : this(DataBindingUtil.inflate(LayoutInflater.from(parent.context), layoutRes, parent, false))

    fun setLoadingState(state: LoadingState) {
        when(state) {
            LoadingState.OK,
            LoadingState.LOAD_MORE_COMPLETE -> {
                pbLoading.visibility = View.GONE
                tvLoading.visibility = View.GONE
                tvLoadingMore.visibility = View.GONE
            }
            LoadingState.EMPTY -> {
                pbLoading.visibility = View.GONE
                tvLoading.visibility = View.GONE
                tvLoadingMore.visibility = View.GONE
            }
            LoadingState.LOADING -> {
                pbLoading.visibility = View.VISIBLE
                tvLoading.visibility = View.GONE
                tvLoadingMore.visibility = View.GONE
            }
            LoadingState.LOADING_MORE -> {
                pbLoading.visibility = View.GONE
                tvLoading.visibility = View.GONE
                tvLoadingMore.visibility = View.VISIBLE
            }
            LoadingState.LOAD_ERR -> {
                pbLoading.visibility = View.GONE
                tvLoading.visibility = View.GONE
                tvLoadingMore.visibility = View.GONE
            }
            LoadingState.LOAD_MORE_ERR -> {
                pbLoading.visibility = View.GONE
                tvLoading.visibility = View.GONE
                tvLoadingMore.visibility = View.GONE
            }
        }
    }
}