package com.productivesocial.com.productivesocial.model.responses

import com.productivesocial.com.productivesocial.constants.TaskSelectionTypes
import kotlinx.serialization.Serializable

@Serializable
data class SubtaskResponse(
    val id: Long,
    val name: String,
    val completed: Boolean,
    val type: TaskSelectionTypes
)
