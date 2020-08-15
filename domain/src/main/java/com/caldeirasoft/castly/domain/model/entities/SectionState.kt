package com.caldeirasoft.castly.domain.model.entities

enum class SectionState(val value: Int) {
    QUEUE(1),
    INBOX(2),
    ARCHIVE(3),
    FAVORITE(4),
    HISTORY(5),
    ALL_EPISODES(0),

    ALL_PODCASTS(10),
    PODCAST(11),
    EPISODE(12),

    IN_DATABASE(20),
    ROOT(-1)
}