package com.sessionzero.sessionzero

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform