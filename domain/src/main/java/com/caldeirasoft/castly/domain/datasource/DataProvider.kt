package com.caldeirasoft.castly.domain.datasource


/**
 * Created by Edmond on 15/02/2018.
 */
abstract class DataProvider<Item>(
        var dataItems: MutableList<Item> = mutableListOf())
{
    // Media items
    fun addItems(items: List<Item>) {
        dataItems.addAll(items)
    }

    /**
     * Get episodes info from DB
     */
    fun updateItems(items: List<Item>, predicateEquals: (Item, Item) -> Boolean) {
        // get episode in db
        dataItems.forEach { item ->
            items.firstOrNull { ep -> predicateEquals.invoke(ep, item) }
                    ?.let { episode ->
                        updateItems(item, episode)
                    }
        }
    }

    /**
     * Update item
     */
    abstract fun updateItems(currentItem: Item, newItem: Item)
}