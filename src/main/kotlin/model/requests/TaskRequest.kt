package com.productivesocial.model.requests

import com.productivesocial.constants.Priority
import com.productivesocial.constants.TaskSelectionTypes
import kotlinx.serialization.Serializable

@Serializable
data class TaskRequest(
    val projectId: Long,
    val name: String,
    val description: String?,
    val priority: Priority,
    val target: String?,
    val recurring: Boolean,
    val sendReminder: Boolean,
    val date: String?,
    val completed: Boolean,
    val times: List<String>,
    val subtasks: List<TaskSubtaskRequest>,
    val tags: List<String>
)

@Serializable
data class TaskSubtaskRequest(
    val name: String,
    val completed: Boolean,
    val type: TaskSelectionTypes
)
