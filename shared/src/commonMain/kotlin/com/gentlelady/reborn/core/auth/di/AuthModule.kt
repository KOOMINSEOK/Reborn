package com.gentlelady.reborn.core.auth.di

import com.gentlelady.reborn.auth.presentation.AuthViewModel
import com.gentlelady.reborn.core.auth.AuthRepository
import com.gentlelady.reborn.core.auth.SessionStore
import com.gentlelady.reborn.core.auth.SessionTokenProvider
import com.gentlelady.reborn.core.auth.SupabaseAuthApi
import com.gentlelady.reborn.core.network.TokenProvider
import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import org.koin.core.qualifier.named
import org.koin.dsl.module

val authModule = module {
    single(named("authHttpClient")) {
        HttpClient {
            install(ContentNegotiation) { json(Json { ignoreUnknownKeys = true }) }
        }
    }
    single { SupabaseAuthApi(get(named("authHttpClient"))) }
    single { SessionStore() }
    single { AuthRepository(get(), get()) }
    single<TokenProvider> { SessionTokenProvider(get(), get()) }
    factory { AuthViewModel(get(), get()) }
}
