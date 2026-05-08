package com.productivesocial.feature.user

import com.productivesocial.model.responses.UserResponse

interface UserRepository {
    suspend fun registerUser(deviceId: String): UserResponse
}
