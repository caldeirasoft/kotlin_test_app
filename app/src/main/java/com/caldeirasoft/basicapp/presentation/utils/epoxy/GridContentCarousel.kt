package com.caldeirasoft.basicapp.presentation.utils.epoxy

import android.content.Context
import android.util.AttributeSet
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.epoxy.ModelView

@ModelView(autoLayout = ModelView.Size.MATCH_WIDTH_WRAP_HEIGHT)
class GridContentCarousel @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ContentCarousel(context, attrs, defStyleAttr) {

    override fun createLayoutManager(): RecyclerView.LayoutManager {
        return GridLayoutManager(context, 3)
    }
}