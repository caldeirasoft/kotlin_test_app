package com.caldeirasoft.basicapp.domain.usecase.base

import kotlinx.coroutines.Deferred
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.lang.Exception
import kotlin.coroutines.CoroutineContext

enum class UsecaseStatus {
    Loading, Done, Error
}