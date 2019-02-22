package com.caldeirasoft.castly.domain.usecase.base

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import java.lang.Exception

abstract class BaseDeferredUseCase<in Input, out Output> {
    var beforeExecute: (() -> Unit)? = null
    var afterExecute: (() -> Unit)? = null
    var failed: ((Throwable) -> Unit)? = null
    var terminated: (() -> Unit)? = null


    suspend fun execute(params: Input): Deferred<UseCaseResponse<out Output>> {
        beforeExecute?.invoke()

        var succeed = false
        try {
            val result = run(params).await()
            succeed = true
            return GlobalScope.async {
                UseCaseResponse.success(result)
            }
        } catch (e: Exception) {
            succeed = false
            failed?.invoke(e)
            return GlobalScope.async {
                UseCaseResponse.error<Output>(e)
            }
        } finally {
            if (succeed) {
                afterExecute?.invoke()
            }

            terminated?.invoke()
        }

    }

    protected abstract suspend fun run(params: Input): Deferred<Output>

}