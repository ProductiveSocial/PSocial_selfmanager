package com.productivesocial.com.productivesocial.model.responses

import com.productivesocial.com.productivesocial.constants.Priority
import kotlinx.serialization.Serializable

@Serializable
data class ProjectResponse(
    val id: Long,
    val userId: Long,
    val name: String,
    val description: String?,
    val iconName: String,
    val colorHex: String,
    val priority: Priority,
    val habits: List<HabitResponse> = emptyList(),
    val routines: List<RoutineResponse> = emptyList(),
    val tasks: List<TaskResponse> = emptyList()
)
