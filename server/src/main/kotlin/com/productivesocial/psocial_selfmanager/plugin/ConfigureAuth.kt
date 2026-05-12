package com.productivesocial.psocial_selfmanager.plugin

import com.productivesocial.psocial_selfmanager.configureAll
import com.productivesocial.psocial_selfmanager.feature.auth.JwtConfig
import com.productivesocial.psocial_selfmanager.model.requests.JwtTokenRequest
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.auth.Authentication
import io.ktor.server.auth.jwt.JWTPrincipal
import io.ktor.server.auth.jwt.jwt

fun Application.configureAuth() {
    install(Authentication) {
        jwt("auth-jwt") {
            verifier(JwtConfig.verifier)
            validate { call ->
                val userId = call.payload.getClaim("user_id").asLong()
                val email = call.payload.getClaim("email").asString()

                if (userId != null && email != null) {
                    JwtTokenRequest(userId, email)
                } else {
                    null
                }
            }
        }
    }
}