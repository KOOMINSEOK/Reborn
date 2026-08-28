package com.gentlelady.reborn.core.network

object ApiConfig {
    /**
     * Android 에뮬레이터에서 호스트 PC 의 localhost. 실기기/배포 시 교체.
     * ponytail: 상수. 환경별로 갈리기 시작하면 BuildKonfig.
     */
    const val BASE_URL = "http://10.0.2.2:8080"

    /** Supabase Auth (로그인/토큰 갱신). URL·publishable 키는 공개돼도 안전. */
    const val SUPABASE_URL = "https://nrvtiiujwjseddfhgdmv.supabase.co"
    const val SUPABASE_ANON_KEY = "sb_publishable_fGZPHXTXJOHk5l20jdWwbQ_s5cICiMu"
}
