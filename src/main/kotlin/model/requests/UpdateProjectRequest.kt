package com.productivesocial.model.requests

import com.productivesocial.constants.Priority
import kotlinx.serialization.Serializable

@Serializable
data class UpdateProjectRequest(
    val name: String? = null,
    val description: String? = null,
    val iconName: String? = null,
    val colorHex: String? = null,
    val priority: Priority? = null
)
