package com.caldeirasoft.castly.domain.usecase.base

import kotlinx.coroutines.flow.Flow

abstract class BaseUseCase<in Input, out Output> {
    abstract fun execute(params: Input): Flow<Output>
}

abstract class Base2UseCase<in Input1, in Input2, out Output> {
    abstract fun execute(param1: Input1, param2: Input2): Flow<Output>
}