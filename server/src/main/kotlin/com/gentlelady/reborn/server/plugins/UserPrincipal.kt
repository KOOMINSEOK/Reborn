package com.gentlelady.reborn.server.plugins

import io.ktor.server.application.ApplicationCall
import io.ktor.server.auth.principal
import java.util.UUID

/** 검증된 Supabase 액세스 토큰에서 뽑아낸 호출자 신원. */
data class UserPrincipal(val id: String, val email: String?)

/** 인증 라우트 안에서 호출자의 uuid. Ktor 가 principal 을 보장하므로 없으면 버그다. */
fun ApplicationCall.userId(): UUID =
    UUID.fromString(
        principal<UserPrincipal>()?.id ?: error("no authenticated principal in an authenticated route"),
    )
