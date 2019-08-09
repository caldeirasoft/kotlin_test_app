package com.caldeirasoft.castly.domain.model

class NetworkState(val state: State,
                   val throwable: Throwable? = null) {

    enum class State {
        LOADING,
        SUCCESS,
        EMPTY,
        ERROR
    }

    val isLoading: Boolean
        get() = state == State.LOADING

    val isInErrorState: Boolean
        get() = state == State.ERROR

    override fun toString(): String {
        return state.name
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false

        other as NetworkState
        if (state != other.state) return false
        if (throwable != other.throwable) return false
        return true
    }

    override fun hashCode(): Int {
        return state.hashCode()
    }

    companion object {
        val Loading: NetworkState
            get() = NetworkState(State.LOADING)

        val Success: NetworkState
            get() = NetworkState(State.SUCCESS)

        val Empty: NetworkState
            get() = NetworkState(State.EMPTY)

        fun Error(throwable: Throwable): NetworkState {
            return NetworkState(State.ERROR, throwable)
        }
    }
}