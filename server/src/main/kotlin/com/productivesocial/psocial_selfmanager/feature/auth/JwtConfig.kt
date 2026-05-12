package com.productivesocial.psocial_selfmanager.feature.auth

import com.auth0.jwt.JWT
import com.auth0.jwt.JWTVerifier
import com.auth0.jwt.algorithms.Algorithm
import com.productivesocial.psocial_selfmanager.config.DotEnvConfig
import com.productivesocial.psocial_selfmanager.model.requests.JwtTokenRequest
import kotlin.time.Clock

object JwtConfig {

    private lateinit var secret: String
    private lateinit var issuer: String
    private lateinit var algorithm: Algorithm
    private const val VALIDITY_MS = 24 * 60 * 60 * 1000L // 24 hours

    lateinit var verifier: JWTVerifier
        private set

    fun init() {
        secret = DotEnvConfig.jwtSecret
        issuer = DotEnvConfig.jwtIssuer
        algorithm = Algorithm.HMAC256(secret)
        verifier = JWT.require(algorithm)
            .withIssuer(issuer).build()
    }

    fun tokenProvider(jwtTokenBody: JwtTokenRequest): String = JWT.create()
        .withSubject("Authentication")
        .withIssuer(issuer)
        .withClaim("user_id", jwtTokenBody.userId)
        .withClaim("email", jwtTokenBody.email)
        .withExpiresAt(getExpiration())
        .sign(algorithm)

    private fun getExpiration() =
        java.util.Date(Clock.System.now().toEpochMilliseconds() + VALIDITY_MS)
}