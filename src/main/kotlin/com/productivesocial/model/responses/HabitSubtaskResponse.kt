package com.productivesocial.com.productivesocial.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class HabitSubtaskResponse(
    val id: Long,
    val name: String,
    val completed: Boolean
)
