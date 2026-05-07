package com.productivesocial.com.productivesocial.model.responses

import com.productivesocial.com.productivesocial.constants.Priority
import kotlinx.serialization.Serializable

@Serializable
data class TaskResponse(
    val id: Long,
    val userId: Long,
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
    val subtasks: List<SubtaskResponse>,
    val tags: List<TagResponse>
)
