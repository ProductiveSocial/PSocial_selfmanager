package com.productivesocial.model.requests

import kotlinx.serialization.Serializable

@Serializable
data class UserRequest(
    val deviceId: String
)
