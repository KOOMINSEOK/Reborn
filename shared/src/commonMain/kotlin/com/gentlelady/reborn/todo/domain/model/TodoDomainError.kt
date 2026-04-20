package com.gentlelady.reborn.todo.domain.model

sealed class TodoDomainError {
    data class Validation(val message: String) : TodoDomainError()
    data class Unknown(val message: String) : TodoDomainError()
}
