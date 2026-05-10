package com.productivesocial.psocial_selfmanager.model.requests

import com.productivesocial.psocial_selfmanager.constants.Priority
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
    val completed: Boolean? = null,
    val date: Long? = null,
    val times: List<Long>? = null,
    val tags: List<String>? = null,
    val subtasks: List<TaskSubtaskRequest>? = null,
)
