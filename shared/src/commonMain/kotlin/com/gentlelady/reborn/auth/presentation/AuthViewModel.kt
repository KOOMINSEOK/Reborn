package com.gentlelady.reborn.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gentlelady.reborn.core.auth.AuthRepository
import com.gentlelady.reborn.core.auth.SessionStore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class AuthViewModel(
    private val repository: AuthRepository,
    sessionStore: SessionStore,
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    init {
        viewModelScope.launch {
            sessionStore.session.collect { session ->
                _state.update { it.copy(loggedIn = session != null) }
            }
        }
    }

    fun handleIntent(intent: LoginIntent) {
        when (intent) {
            is LoginIntent.EmailChanged -> _state.update { it.copy(email = intent.value, error = null) }
            is LoginIntent.PasswordChanged -> _state.update { it.copy(password = intent.value, error = null) }
            LoginIntent.Submit -> submit()
            LoginIntent.SignOut -> repository.signOut()
        }
    }

    private fun submit() {
        val current = _state.value
        if (current.isLoading) return
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }
            val result = repository.signIn(current.email, current.password)
            _state.update {
                it.copy(
                    isLoading = false,
                    error = if (result.isFailure) "로그인에 실패했습니다. 이메일과 비밀번호를 확인해 주세요." else null,
                )
            }
        }
    }
}
