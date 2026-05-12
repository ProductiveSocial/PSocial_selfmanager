package com.productivesocial.psocial_selfmanager.model.requests

import kotlinx.serialization.Serializable

@Serializable
data class LogHabitCompletionRequest(
    val completedAt: Long,       // epoch millis — the moment the user logged it
    val habitTimeId: Long? = null // optional: which scheduled time slot this completion is for
)
