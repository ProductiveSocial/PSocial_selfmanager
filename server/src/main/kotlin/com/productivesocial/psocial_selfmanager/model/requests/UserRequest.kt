package com.productivesocial.psocial_selfmanager.model.requests

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val deviceId: String
)
