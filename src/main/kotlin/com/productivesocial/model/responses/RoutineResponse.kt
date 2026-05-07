package com.productivesocial.com.productivesocial.model.responses

import com.productivesocial.com.productivesocial.constants.Recurrency
import kotlinx.serialization.Serializable

@Serializable
data class RoutineResponse(
    val id: Long,
    val userId: Long,
    val projectId: Long,
    val name: String,
    val description: String?,
    val recurrency: Recurrency,
    val target: String,
    val sendReminder: Boolean,
    val completed: Boolean,
    val times: List<String>,
    val reminderTimes: List<String>,
    val steps: List<RoutineStepResponse>,
    val tags: List<TagResponse>
)
