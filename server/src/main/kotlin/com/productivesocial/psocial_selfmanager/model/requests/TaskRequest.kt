package com.productivesocial.psocial_selfmanager.model.requests

import com.productivesocial.psocial_selfmanager.constants.Priority
import kotlinx.serialization.Serializable

@Serializable
data class TaskRequest(
    val projectId: Long,
    val name: String,
    val description: String?,
    val priority: Priority,
    val urgency: String? = null,
    val target: String?,
    val recurring: Boolean,
    val sendReminder: Boolean,
    val date: Long?,
    val completed: Boolean,
    val times: List<Long>,
    val subtasks: List<TaskSubtaskRequest>,
    val tags: List<String>
)

@Serializable
data class TaskSubtaskRequest(
    val name: String,
    val completed: Boolean
)
