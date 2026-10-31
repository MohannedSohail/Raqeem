package org.moh.raqeem.project

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform
