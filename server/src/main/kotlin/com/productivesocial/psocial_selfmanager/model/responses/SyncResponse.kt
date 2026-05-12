package com.productivesocial.psocial_selfmanager.model.responses

import kotlinx.serialization.Serializable

@Serializable
data class SyncResponse(
    /** Maps each clientId to the server-assigned ID for every entity created in this batch. */
    val idMappings: SyncIdMappings,
    /** Entities that changed on the server since the client's lastSyncedAt. */
    val serverChanges: ServerChanges,
    /** Per-entity errors. Non-empty does NOT mean the whole sync failed. */
    val errors: List<SyncError> = emptyList(),
    /** Epoch millis the server processed this sync. Client should store this as its new lastSyncedAt. */
    val syncedAt: Long
)

@Serializable
data class SyncIdMappings(
    val projects: Map<String, Long> = emptyMap(),
    val tasks: Map<String, Long> = emptyMap(),
    val habits: Map<String, Long> = emptyMap(),
    val routines: Map<String, Long> = emptyMap(),
    val habitCompletions: Map<String, Long> = emptyMap(),
)

@Serializable
data class ServerChanges(
    val projects: List<ProjectResponse> = emptyList(),
    val tasks: List<TaskResponse> = emptyList(),
    val habits: List<HabitResponse> = emptyList(),
    val routines: List<RoutineResponse> = emptyList(),
    val habitCompletions: List<HabitCompletionResponse> = emptyList(),
    /** Entity IDs deleted on the server since lastSyncedAt. Client should remove these locally. */
    val deletedIds: DeletedEntityIds = DeletedEntityIds()
)

@Serializable
data class DeletedEntityIds(
    val projectIds: List<Long> = emptyList(),
    val taskIds: List<Long> = emptyList(),
    val habitIds: List<Long> = emptyList(),
    val routineIds: List<Long> = emptyList(),
    val habitCompletionIds: List<Long> = emptyList(),
)

@Serializable
data class SyncError(
    val entityType: String,
    val operation: String,        // "create" | "update" | "delete"
    val clientId: String? = null, // set for create errors
    val serverId: Long? = null,   // set for update/delete errors
    val message: String
)
