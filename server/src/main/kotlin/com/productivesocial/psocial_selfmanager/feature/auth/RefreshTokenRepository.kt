package com.productivesocial.psocial_selfmanager.feature.auth

import com.productivesocial.psocial_selfmanager.database.entities.RefreshTokenDAO
import java.time.Instant


interface RefreshTokenRepository {
    suspend fun createRefreshToken(userId: String, tokenHash: String, expiresAt: Instant): Boolean
    suspend fun getRefreshTokenByHash(tokenHash: String): RefreshTokenDAO?
    suspend fun revokeRefreshToken(tokenHash: String): Boolean
    suspend fun revokeAllUserTokens(userId: String): Boolean
    suspend fun cleanupExpiredTokens(): Int
}