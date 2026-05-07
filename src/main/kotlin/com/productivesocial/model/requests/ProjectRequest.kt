package com.productivesocial.com.productivesocial.model.requests

import com.productivesocial.com.productivesocial.constants.Priority
import kotlinx.serialization.Serializable

@Serializable
data class ProjectRequest(
    val name: String,
    val description: String?,
    val iconName: String,
    val colorHex: String,
    val priority: Priority
)
