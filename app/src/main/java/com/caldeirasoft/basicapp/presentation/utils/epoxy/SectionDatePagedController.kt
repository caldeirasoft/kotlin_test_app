package com.caldeirasoft.basicapp.presentation.utils.epoxy

import androidx.recyclerview.widget.DiffUtil
import com.airbnb.epoxy.EpoxyModel
import com.caldeirasoft.basicapp.presentation.utils.defaultItemDiffCallback

abstract class SectionDatePagedController<T>(
        itemDiffCallback: DiffUtil.ItemCallback<T> = defaultItemDiffCallback())
    : BasePagedController<T>( itemDiffCallback = itemDiffCallback )
{
    override fun addModels(models: List<EpoxyModel<*>>) {
        super.addModels(models)
    }
}