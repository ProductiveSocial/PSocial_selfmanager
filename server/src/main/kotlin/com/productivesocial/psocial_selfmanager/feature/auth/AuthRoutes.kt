package com.productivesocial.psocial_selfmanager.feature.auth

import com.productivesocial.psocial_selfmanager.model.requests.JwtTokenRequest
import com.productivesocial.psocial_selfmanager.model.requests.RefreshTokenRequest
import com.productivesocial.psocial_selfmanager.model.requests.RegistrationRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.authRoutes(authService: AuthService) {

    route("/auth") {

        /**
         * POST /api/v1/auth/identify
         * Body: { email }
         * Creates the user if not exists, otherwise retrieves them.
         * Returns: { accessToken, refreshToken, tokenType, expiresIn }
         * The same email across devices yields the same user_id.
         */
        post("/identify") {
            val request = call.receive<RegistrationRequest>()
            request.validation()
            val tokens = authService.identify(request.email)
            call.respond(HttpStatusCode.OK, tokens)
        }

        /**
         * POST /api/v1/auth/refresh
         * Body: { refreshToken }
         * Returns new token pair (old refresh token is revoked — rotation).
         */
        post("/refresh") {
            val request = call.receive<RefreshTokenRequest>()
            request.validate()
            val tokens = authService.refresh(request.refreshToken)
            call.respond(HttpStatusCode.OK, tokens)
        }

        authenticate("auth-jwt") {

            /**
             * POST /api/v1/auth/logout
             * Requires: Bearer access token
             * Body: { refreshToken }
             * Revokes the given refresh token (this device only).
             */
            post("/logout") {
                val request = call.receive<RefreshTokenRequest>()
                authService.logout(request.refreshToken)
                call.respond(HttpStatusCode.OK, mapOf("message" to "Logged out successfully"))
            }

            /**
             * POST /api/v1/auth/logout-all
             * Requires: Bearer access token
             * Revokes all sessions for this user across all devices.
             */
            post("/logout-all") {
                val principal = call.principal<JwtTokenRequest>()
                    ?: return@post call.respond(HttpStatusCode.Unauthorized)
                authService.logoutAll(principal.userId)
                call.respond(HttpStatusCode.OK, mapOf("message" to "All sessions revoked"))
            }
        }
    }
}
