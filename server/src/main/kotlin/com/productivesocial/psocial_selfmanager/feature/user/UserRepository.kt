package com.productivesocial.psocial_selfmanager.feature.user

import com.productivesocial.psocial_selfmanager.model.responses.UserResponse

interface UserRepository {
    suspend fun registerUser(deviceId: String): UserResponse
}
