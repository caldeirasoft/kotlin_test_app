package com.caldeirasoft.basicapp.presentation.views.bindingadapter

import androidx.databinding.BindingAdapter
import com.caldeirasoft.basicapp.presentation.views.LoadingButton

@BindingAdapter("loading")
fun LoadingButton.loading(value: Boolean?) {
    value?.let { this.isLoading = it }
}

@BindingAdapter("isEnabled")
fun LoadingButton.isEnabled(value: Boolean?) {
    value?.let { this.isEnabled = it }
}
