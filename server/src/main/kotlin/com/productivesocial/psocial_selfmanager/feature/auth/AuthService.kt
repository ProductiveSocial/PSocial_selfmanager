package com.productivesocial.psocial_selfmanager.feature.auth

import com.productivesocial.psocial_selfmanager.constants.Messages
import com.productivesocial.psocial_selfmanager.constants.Priority
import com.productivesocial.psocial_selfmanager.database.UserRegistry
import com.productivesocial.psocial_selfmanager.database.entities.ProjectDAO
import com.productivesocial.psocial_selfmanager.database.entities.UserDAO
import com.productivesocial.psocial_selfmanager.database.entities.UserTable
import com.productivesocial.psocial_selfmanager.model.requests.JwtTokenRequest
import com.productivesocial.psocial_selfmanager.model.requests.TokenData
import com.productivesocial.psocial_selfmanager.utils.query
import io.ktor.server.plugins.*
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import java.security.MessageDigest
import java.time.Instant
import java.time.temporal.ChronoUnit
import java.util.UUID

class AuthService : AuthRepository {

    private val refreshTokenRepo = RefreshTokenRepositoryImpl()
    private val REFRESH_TOKEN_VALIDITY_DAYS = 30L

    /**
     * Find or create a user by email.
     * No password required — email is the identity.
     * Same email across devices = same user_id = shared state.
     */
    override suspend fun identify(email: String): TokenData {
        // 1. Get canonical user ID from central psocial_user registry
        val canonicalId = UserRegistry.findOrCreate(email)

        // 2. Mirror into selfmanager's own users table using the same ID
        val isNewUser = query {
            val existing = UserDAO.findById(canonicalId)
            if (existing == null) {
                UserDAO.new(canonicalId) {
                    this.deviceId = UUID.randomUUID().toString()
                    this.email = email
                }
                true
            } else {
                false
            }
        }

        // 3. Create a default project for brand-new users
        if (isNewUser) {
            query {
                ProjectDAO.new {
                    this.userId = EntityID(canonicalId, UserTable)
                    this.name = "Default"
                    this.description = null
                    this.iconName = "home"
                    this.colorHex = "#4A90E2"
                    this.priority = Priority.Medium
                    this.syncId = null
                }
            }
        }

        return issueTokens(canonicalId, email)
    }

    override suspend fun refresh(refreshToken: String): TokenData {
        val tokenHash = sha256(refreshToken)
        val stored = refreshTokenRepo.getRefreshTokenByHash(tokenHash)
            ?: throw BadRequestException(Messages.Auth.INVALID_REFRESH_TOKEN)

        if (!stored.isValid) throw BadRequestException(Messages.Auth.TOKEN_EXPIRED)

        refreshTokenRepo.revokeRefreshToken(tokenHash) // token rotation

        val user = query { UserDAO.findById(stored.userId.value) }
            ?: throw BadRequestException(Messages.Auth.INVALID_REFRESH_TOKEN)

        return issueTokens(user.id.value, user.email ?: "")
    }

    override suspend fun logout(refreshToken: String): Boolean =
        refreshTokenRepo.revokeRefreshToken(sha256(refreshToken))

    override suspend fun logoutAll(userId: Long): Boolean =
        refreshTokenRepo.revokeAllUserTokens(userId.toString())

    // ── Helpers ──────────────────────────────────────────────────────────────

    private suspend fun issueTokens(userId: Long, email: String): TokenData {
        val accessToken = JwtConfig.tokenProvider(JwtTokenRequest(userId, email))

        val rawRefreshToken = UUID.randomUUID().toString() + UUID.randomUUID().toString()
        val expiresAt = Instant.now().plus(REFRESH_TOKEN_VALIDITY_DAYS, ChronoUnit.DAYS)
        refreshTokenRepo.createRefreshToken(userId.toString(), sha256(rawRefreshToken), expiresAt)

        return TokenData(userId = userId, accessToken = accessToken, refreshToken = rawRefreshToken, expiresIn = 86400L)
    }

    private fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}