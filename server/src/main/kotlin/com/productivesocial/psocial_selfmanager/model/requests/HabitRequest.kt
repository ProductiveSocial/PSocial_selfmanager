package com.productivesocial.psocial_selfmanager.model.requests

import com.productivesocial.psocial_selfmanager.constants.HabitType
import com.productivesocial.psocial_selfmanager.constants.Recurrency
import kotlinx.serialization.Serializable

@kotlinx.serialization.Serializable
data class HabitRequest(
    val projectId: Long,
    val name: String,
    val description: String?,
    val habitType: HabitType,
    val recurrency: Recurrency,
    val target: String,
    val sendReminder: Boolean,
    val completed: Boolean,
    val times: List<Long>,
    val reminderTimes: List<Long>,
    val subtasks: List<HabitSubtaskRequest>,
    val tags: List<String>
)

@Serializable
data class HabitSubtaskRequest(
    val name: String,
    val completed: Boolean
)
