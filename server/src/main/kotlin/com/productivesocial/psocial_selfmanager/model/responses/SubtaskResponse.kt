package com.productivesocial.psocial_selfmanager.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class SubtaskResponse(
    val id: Long,
    val name: String,
    val completed: Boolean
)
