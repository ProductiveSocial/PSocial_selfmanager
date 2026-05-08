package com.productivesocial.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class TagResponse(
    val id: Long,
    val name: String
)
