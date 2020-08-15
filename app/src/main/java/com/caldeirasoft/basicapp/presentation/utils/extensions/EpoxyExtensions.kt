package com.caldeirasoft.basicapp.presentation.utils.extensions

import androidx.viewpager2.widget.ViewPager2
import com.airbnb.epoxy.*
import com.caldeirasoft.basicapp.presentation.utils.epoxy.ContentCarouselModelBuilder
import com.caldeirasoft.basicapp.presentation.utils.epoxy.GridContentCarouselModelBuilder
import com.caldeirasoft.basicapp.presentation.utils.epoxy.GridContentCarouselModel_

/**
 * For use in the buildModels method of EpoxyController.
 * A shortcut for creating a Carousel model, initializing it, and adding it to the controller.
 *
 */
inline fun EpoxyController.carousel(modelInitializer: CarouselModelBuilder.() -> Unit) {
    CarouselModel_().apply {
        modelInitializer()
    }.addTo(this)
}

/** Add models to a CarouselModel_ by transforming a list of items into EpoxyModels.
 *
 * @param items The items to transform to models
 * @param modelBuilder A function that take an item and returns a new EpoxyModel for that item.
 */
inline fun <T> CarouselModelBuilder.withModelsFrom(
        items: List<T>,
        modelBuilder: (T) -> EpoxyModel<*>
) {
    models(items.map { modelBuilder(it) })
}

/**
 * For use in the buildModels method of EpoxyController.
 * A shortcut for creating a Grid Carousel model, initializing it, and adding it to the controller.
 *
 */
inline fun EpoxyController.gridCarousel(modelInitializer: GridContentCarouselModelBuilder.() -> Unit) {
    GridContentCarouselModel_().apply {
        modelInitializer()
    }.addTo(this)
}

/** Add models to a ContentCarouselModel_ by transforming a list of items into EpoxyModels.
 *
 * @param items The items to transform to models
 * @param modelBuilder A function that take an item and returns a new EpoxyModel for that item.
 */
inline fun <T> ContentCarouselModelBuilder.withModelsFrom(
        items: List<T>,
        modelBuilder: (T) -> EpoxyModel<*>
) {
    models(items.map { modelBuilder(it) })
}

/** Add models to a GridContentCarouselModel_ by transforming a list of items into EpoxyModels.
 *
 * @param items The items to transform to models
 * @param modelBuilder A function that take an item and returns a new EpoxyModel for that item.
 */
inline fun <T> GridContentCarouselModelBuilder.withModelsFrom(
        items: List<T>,
        modelBuilder: (T) -> EpoxyModel<*>
) {
    models(items.map { modelBuilder(it) })
}

/**
 * Set epoxy controller to a ViewPager
 */
fun ViewPager2.setController(controller: EpoxyController) {
    adapter = controller.adapter
}

/**
 * Set epoxy controller to a ViewPager
 */
fun ViewPager2.setControllerAndBuildModels(controller: EpoxyController) {
    setController(controller)
    controller.requestModelBuild()
}
