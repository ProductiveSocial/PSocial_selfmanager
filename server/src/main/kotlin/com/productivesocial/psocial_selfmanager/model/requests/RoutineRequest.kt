package com.productivesocial.psocial_selfmanager.model.requests

import com.productivesocial.psocial_selfmanager.constants.Recurrency
import kotlinx.serialization.Serializable

@Serializable
data class RoutineRequest(
    val projectId: Long,
    val name: String,
    val description: String?,
    val recurrency: Recurrency,
    val target: String,
    val sendReminder: Boolean,
    val completed: Boolean,
    val times: List<Long>,
    val reminderTimes: List<Long>,
    val steps: List<RoutineStepRequest>,
    val tags: List<String>
)

@Serializable
data class RoutineStepRequest(
    val name: String,
    val autoStart: Boolean,
    val duration: Int,
    val description: String?,
    val completed: Boolean
)
