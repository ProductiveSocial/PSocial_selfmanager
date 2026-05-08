package com.productivesocial.model.requests

import com.productivesocial.constants.Recurrency
import kotlinx.serialization.Serializable

@Serializable
data class UpdateRoutineRequest(
    val projectId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val recurrency: Recurrency? = null,
    val target: String? = null,
    val sendReminder: Boolean? = null,
    val completed: Boolean? = null,
    val times: List<String>? = null,
    val reminderTimes: List<String>? = null,
    val steps: List<RoutineStepRequest>? = null,
    val tags: List<String>? = null
)
