package com.productivesocial.psocial_selfmanager.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    val id: Long,
    val deviceId: String,
    val defaultProjectId: Long
)
