package com.gentlelady.reborn.model

sealed class OperationResult<out T> {
    data class Success<T>(val data: T) : OperationResult<T>()
    data class Failure(val error: UiError) : OperationResult<Nothing>()
}
