package com.gentlelady.reborn

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform