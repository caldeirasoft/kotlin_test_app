package com.caldeirasoft.basicapp.ui.common

import com.airbnb.epoxy.EpoxyController


abstract class BaseBindingViewModel() {
    abstract fun bindModel(controller: EpoxyController)
}