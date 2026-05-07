package com.productivesocial.com.productivesocial.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class RoutineStepResponse(
    val id: Long,
    val name: String,
    val autoStart: Boolean,
    val duration: Int,
    val description: String?,
    val completed: Boolean
)
