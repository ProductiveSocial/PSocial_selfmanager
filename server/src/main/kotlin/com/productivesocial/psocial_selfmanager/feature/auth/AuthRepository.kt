package com.productivesocial.psocial_selfmanager.feature.auth

import com.productivesocial.psocial_selfmanager.model.requests.RegistrationRequest
import com.productivesocial.psocial_selfmanager.model.requests.TokenData

interface AuthRepository {
    /** Find or create a user by email, then issue tokens. */
    suspend fun identify(email: String): TokenData
    suspend fun refresh(refreshToken: String): TokenData
    suspend fun logout(refreshToken: String): Boolean
    suspend fun logoutAll(userId: Long): Boolean
}