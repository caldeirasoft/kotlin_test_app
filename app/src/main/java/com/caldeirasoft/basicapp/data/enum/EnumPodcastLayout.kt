package com.caldeirasoft.basicapp.data.enum

enum class EnumPodcastLayout(val value: Int) {
    LIST(1),
    GRID(2),
    GRID_SMALL(3);

    /*
    companion object {
        fun from(findValue: Int): EnumPodcastLayout =
                values().first { it.value == findValue }
    }
    */
}