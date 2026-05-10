package com.productivesocial.psocial_selfmanager.plugin

import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.ratelimit.RateLimit
import io.ktor.server.plugins.ratelimit.RateLimitConfig
import io.ktor.server.plugins.ratelimit.RateLimitName
import kotlin.time.Duration
import kotlin.time.Duration.Companion.minutes

fun Application.configureRateLimit() {
    install(RateLimit) {
        registerRateLimitZone(name = "GENERAL", limit = 100, refillPeriod = 1.minutes)
    }
}

private fun RateLimitConfig.registerRateLimitZone(name: String, limit: Int, refillPeriod: Duration) {
    register(RateLimitName(name)) {
        rateLimiter(limit = limit, refillPeriod = refillPeriod)
        requestKey { call -> call.request.local.remoteHost }
    }
}