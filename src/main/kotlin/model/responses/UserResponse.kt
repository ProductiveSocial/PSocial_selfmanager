package com.productivesocial.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val deviceId: String,
    val defaultProjectId: Long
)
