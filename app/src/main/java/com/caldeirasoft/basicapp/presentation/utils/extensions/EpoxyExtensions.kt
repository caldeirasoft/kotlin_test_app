package com.caldeirasoft.basicapp.presentation.utils.extensions

import com.airbnb.epoxy.*

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
 * Easily add models to an EpoxyRecyclerView, the same way you would in a buildModels method of EpoxyController
 */
fun EpoxyRecyclerView.withModels(buildModelsCallback: EpoxyController.() -> Unit) {
    setControllerAndBuildModels(object : EpoxyController() {
        override fun buildModels() {
            buildModelsCallback()
        }
    })
}

