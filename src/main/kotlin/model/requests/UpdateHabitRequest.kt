package com.productivesocial.model.requests

import com.productivesocial.constants.HabitType
import com.productivesocial.constants.Recurrency
import kotlinx.serialization.Serializable

@Serializable
data class UpdateHabitRequest(
    val projectId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val habitType: HabitType? = null,
    val recurrency: Recurrency? = null,
    val target: String? = null,
    val sendReminder: Boolean? = null,
    val completed: Boolean? = null,
    val times: List<Long>? = null,
    val reminderTimes: List<Long>? = null,
    val subtasks: List<HabitSubtaskRequest>? = null,
    val tags: List<String>? = null
)
