package com.caldeirasoft.castly.domain.model.entities

enum class SubscribeAction(val value: Int) {
    INBOX(1),
    QUEUE_NEXT(2),
    QUEUE_LAST(3),
    ARCHIVE(4);
}