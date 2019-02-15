package com.caldeirasoft.castly.domain.usecase.base

class UseCaseResponse<R> private constructor(
        val status: UsecaseStatus
) {

    var data: R? = null
        private set
    var error: Throwable? = null
        private set

    companion object {
        fun <R > error(error: Throwable) =
                UseCaseResponse<R>(UsecaseStatus.Error).apply { this.error = error }

        fun <R> success(result: R) =
                UseCaseResponse<R>(UsecaseStatus.Done).apply {this.data = result }
    }
}