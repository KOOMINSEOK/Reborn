package com.gentlelady.reborn.model

sealed class UiError {
    data class Validation(val message: String) : UiError()
    data class Network(val message: String) : UiError()
    data class Unknown(val message: String) : UiError()
}
