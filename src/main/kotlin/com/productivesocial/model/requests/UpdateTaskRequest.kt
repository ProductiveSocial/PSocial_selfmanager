package com.productivesocial.com.productivesocial.model.requests

import com.productivesocial.com.productivesocial.constants.Priority
import kotlinx.serialization.Serializable

@Serializable
data class UpdateTaskRequest(
    val projectId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val priority: Priority? = null,
    val target: String? = null,
    val recurring: Boolean? = null,
    val sendReminder: Boolean? = null,
    val date: String? = null,
    val completed: Boolean? = null,
    val times: List<String>? = null,
    val subtasks: List<TaskSubtaskRequest>? = null,
    val tags: List<String>? = null
)
