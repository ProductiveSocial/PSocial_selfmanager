package com.productivesocial.psocial_selfmanager.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class HabitCompletionResponse(
    val id: Long,
    val habitId: Long,
    val completedAt: Long,       // epoch millis
    val habitTimeId: Long? = null
)
