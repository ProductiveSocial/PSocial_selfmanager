package com.productivesocial.psocial_selfmanager.model.responses

import com.productivesocial.psocial_selfmanager.constants.Recurrency
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
    val times: List<Long>,
    val reminderTimes: List<Long>,
    val steps: List<RoutineStepResponse>,
    val tags: List<TagResponse>
)
