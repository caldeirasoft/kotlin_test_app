package com.caldeirasoft.castly.domain.usecase.base

abstract class BaseUseCase<in Input, out Output> {
    var beforeExecute: (() -> Unit)? = null
    var afterExecute: (() -> Unit)? = null
    var failed: ((Throwable) -> Unit)? = null
    var terminated: (() -> Unit)? = null


    fun execute(params: Input): UseCaseResponse<out Output> {
        beforeExecute?.invoke()

        var succeed = false
        try {
            val result = run(params)
            succeed = true
            return UseCaseResponse.success(result)
        } catch (e: Exception) {
            succeed = false
            failed?.invoke(e)
            return UseCaseResponse.error<Output>(e)
        } finally {
            if (succeed) {
                afterExecute?.invoke()
            }

            terminated?.invoke()
        }

    }

    protected abstract fun run(params: Input): Output

}