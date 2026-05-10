package com.productivesocial.psocial_selfmanager.model

import kotlinx.serialization.Serializable

@Serializable
data class PaginatedResponse<T> (
    val data: List<T>,
    val metadata: PaginationMetadata
)

@Serializable
data class PaginationMetadata (
    val totalItems: Int,
    val currentPage: Int,
    val itemsPerPage: Int,
    val totalPages: Int
)