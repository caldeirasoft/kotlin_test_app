package com.caldeirasoft.castly.domain.model

enum class SavingState(val value: Int) {
    AVAILABLE(0),
    SAVED(1),
    LOADING(2);

    companion object {
        private val map = SavingState.values().associateBy(SavingState::value)
        fun fromInt(type: Int): SavingState = map[type] ?: AVAILABLE
    }
}