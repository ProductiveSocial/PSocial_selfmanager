package com.productivesocial.psocial_selfmanager.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class RegistrationResponse(
    val id: Long,
    val email: String,
    val message: String
)
