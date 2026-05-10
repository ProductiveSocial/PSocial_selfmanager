package com.productivesocial.psocial_selfmanager.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class TagResponse(
    val id: Long,
    val name: String
)
