package com.productivesocial.psocial_selfmanager.model.responses

import com.productivesocial.psocial_selfmanager.constants.Priority
import kotlinx.serialization.Serializable

@Serializable
data class TaskResponse(
    val id: Long,
    val userId: Long,
    val projectId: Long,
    val name: String,
    val description: String?,
    val priority: Priority,
    val urgency: String?,
    val completed: Boolean,
    val target: String?,
    val recurring: Boolean,
    val sendReminder: Boolean,
    val date: Long?,
    val times: List<Long>,
    val tags: List<TagResponse>,
    val subtasks: List<SubtaskResponse>,
)
