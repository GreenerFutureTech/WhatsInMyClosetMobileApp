package org.greenthread.whatsinmycloset

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform