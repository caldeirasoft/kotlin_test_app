package com.caldeirasoft.basicapp.domain.usecase

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

abstract class UseCase<Input, Output>(
        protected val dispatchers: CoroutineContext
) {
    abstract suspend fun createOutput(data: Input): Deferred<Output>
    fun execute(data: Input, callback: (Output?, Exception?) -> Unit = { _, _ -> }) {
        GlobalScope.launch(dispatchers) {
            var output: Output? = null
            var error: Exception? = null
            try {
                output = createOutput(data).await()
            }
            catch(err: Exception) {
                error = err
            }
            callback(output, error)
        }
    }

}