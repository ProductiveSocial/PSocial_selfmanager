package com.productivesocial.model.requests

import com.productivesocial.constants.HabitType
import com.productivesocial.constants.Recurrency
import kotlinx.serialization.Serializable

@Serializable
data class HabitRequest(
    val projectId: Long,
    val name: String,
    val description: String?,
    val habitType: HabitType,
    val recurrency: Recurrency,
    val target: String,
    val sendReminder: Boolean,
    val completed: Boolean,
    val times: List<String>,
    val reminderTimes: List<String>,
    val subtasks: List<HabitSubtaskRequest>,
    val tags: List<String>
)

@Serializable
data class HabitSubtaskRequest(
    val name: String,
    val completed: Boolean
)
