package com.productivesocial.model.responses

import com.productivesocial.constants.Priority
import kotlinx.serialization.Serializable
import java.sql.Timestamp

@Serializable
data class TaskResponse(
    val id: Long,
    val userId: Long,
    val projectId: Long,
    val name: String,
    val description: String?,
    val priority: Priority,
    val completed: Boolean,
    val target: String?,
    val recurring: Boolean,
    val sendReminder: Boolean,
    val date: Long?,
    val times: List<Long>,
    val tags: List<TagResponse>,
    val subtasks: List<SubtaskResponse>,
)
