package com.gentlelady.reborn.server.plugins

/** 검증된 Supabase 액세스 토큰에서 뽑아낸 호출자 신원. */
data class UserPrincipal(val id: String, val email: String?)
