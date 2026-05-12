package com.productivesocial.psocial_selfmanager.model.requests

import io.ktor.server.auth.Principal
import io.ktor.server.auth.jwt.JWTPrincipal

data class JwtTokenRequest(
    val userId: Long, val email: String
): Principal {


}