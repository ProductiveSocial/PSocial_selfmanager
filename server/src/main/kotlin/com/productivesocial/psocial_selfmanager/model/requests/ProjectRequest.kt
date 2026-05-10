package com.productivesocial.psocial_selfmanager.model.requests

import com.productivesocial.psocial_selfmanager.constants.Priority
import kotlinx.serialization.Serializable

@Serializable
data class ProjectRequest(
    val name: String,
    val description: String?,
    val iconName: String,
    val colorHex: String,
    val priority: Priority,
    val tags: List<String>
)
