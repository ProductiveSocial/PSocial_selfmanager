package com.productivesocial.model.responses

import com.productivesocial.constants.HabitType
import com.productivesocial.constants.Recurrency
import kotlinx.serialization.Serializable

@Serializable
data class HabitResponse(
    val id: Long,
    val userId: Long,
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
    val subtasks: List<HabitSubtaskResponse>,
    val tags: List<TagResponse>
)
