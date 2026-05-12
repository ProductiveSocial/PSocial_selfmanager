package com.productivesocial.psocial_selfmanager.model.requests

import com.productivesocial.psocial_selfmanager.constants.HabitType
import com.productivesocial.psocial_selfmanager.constants.Priority
import com.productivesocial.psocial_selfmanager.constants.Recurrency
import kotlinx.serialization.Serializable

@Serializable
data class SyncRequest(
    /**
     * Epoch millis of the client's last successful sync.
     * The server will return all entities modified after this timestamp so the
     * client can update its local database with changes it may have missed
     * (e.g., from another device or direct API calls).
     * Pass null on the very first sync to receive all entities.
     */
    val lastSyncedAt: Long? = null,
    val projects: ProjectSyncBatch? = null,
    val tasks: TaskSyncBatch? = null,
    val habits: HabitSyncBatch? = null,
    val routines: RoutineSyncBatch? = null,
    val habitCompletions: HabitCompletionSyncBatch? = null,
)

// ── Projects ─────────────────────────────────────────────────────────────────

@Serializable
data class ProjectSyncBatch(
    val created: List<ProjectSyncCreate> = emptyList(),
    val updated: List<ProjectSyncUpdate> = emptyList(),
    val deleted: List<Long> = emptyList()
)

@Serializable
data class ProjectSyncCreate(
    val clientId: String,
    val name: String,
    val description: String? = null,
    val iconName: String,
    val colorHex: String,
    val priority: Priority,
    val tags: List<String> = emptyList()
)

@Serializable
data class ProjectSyncUpdate(
    val id: Long,
    val name: String? = null,
    val description: String? = null,
    val iconName: String? = null,
    val colorHex: String? = null,
    val priority: Priority? = null,
    val tags: List<String>? = null
)

// ── Tasks ─────────────────────────────────────────────────────────────────────

@Serializable
data class TaskSyncBatch(
    val created: List<TaskSyncCreate> = emptyList(),
    val updated: List<TaskSyncUpdate> = emptyList(),
    val deleted: List<Long> = emptyList()
)

@Serializable
data class TaskSyncCreate(
    val clientId: String,
    /** Server-assigned project ID. Use when project already existed before going offline. */
    val projectId: Long? = null,
    /** Client-assigned temp ID of a project also created in this sync batch. */
    val projectClientId: String? = null,
    val name: String,
    val description: String? = null,
    val priority: Priority,
    val target: String? = null,
    val recurring: Boolean = false,
    val sendReminder: Boolean = false,
    val date: Long? = null,
    val completed: Boolean = false,
    val times: List<Long> = emptyList(),
    val subtasks: List<TaskSubtaskRequest> = emptyList(),
    val tags: List<String> = emptyList()
)

@Serializable
data class TaskSyncUpdate(
    val id: Long,
    val projectId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val priority: Priority? = null,
    val target: String? = null,
    val recurring: Boolean? = null,
    val sendReminder: Boolean? = null,
    val date: Long? = null,
    val completed: Boolean? = null,
    val times: List<Long>? = null,
    val subtasks: List<TaskSubtaskRequest>? = null,
    val tags: List<String>? = null
)

// ── Habits ────────────────────────────────────────────────────────────────────

@Serializable
data class HabitSyncBatch(
    val created: List<HabitSyncCreate> = emptyList(),
    val updated: List<HabitSyncUpdate> = emptyList(),
    val deleted: List<Long> = emptyList()
)

@Serializable
data class HabitSyncCreate(
    val clientId: String,
    val projectId: Long? = null,
    val projectClientId: String? = null,
    val name: String,
    val description: String? = null,
    val habitType: HabitType,
    val recurrency: Recurrency,
    val target: String,
    val sendReminder: Boolean = false,
    val completed: Boolean = false,
    val times: List<Long> = emptyList(),
    val reminderTimes: List<Long> = emptyList(),
    val subtasks: List<HabitSubtaskRequest> = emptyList(),
    val tags: List<String> = emptyList()
)

@Serializable
data class HabitSyncUpdate(
    val id: Long,
    val projectId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val habitType: HabitType? = null,
    val recurrency: Recurrency? = null,
    val target: String? = null,
    val sendReminder: Boolean? = null,
    val completed: Boolean? = null,
    val times: List<Long>? = null,
    val reminderTimes: List<Long>? = null,
    val subtasks: List<HabitSubtaskRequest>? = null,
    val tags: List<String>? = null
)

// ── Routines ──────────────────────────────────────────────────────────────────

@Serializable
data class RoutineSyncBatch(
    val created: List<RoutineSyncCreate> = emptyList(),
    val updated: List<RoutineSyncUpdate> = emptyList(),
    val deleted: List<Long> = emptyList()
)

@Serializable
data class RoutineSyncCreate(
    val clientId: String,
    val projectId: Long? = null,
    val projectClientId: String? = null,
    val name: String,
    val description: String? = null,
    val recurrency: Recurrency,
    val target: String,
    val sendReminder: Boolean = false,
    val completed: Boolean = false,
    val times: List<Long> = emptyList(),
    val reminderTimes: List<Long> = emptyList(),
    val steps: List<RoutineStepRequest> = emptyList(),
    val tags: List<String> = emptyList()
)

@Serializable
data class RoutineSyncUpdate(
    val id: Long,
    val projectId: Long? = null,
    val name: String? = null,
    val description: String? = null,
    val recurrency: Recurrency? = null,
    val target: String? = null,
    val sendReminder: Boolean? = null,
    val completed: Boolean? = null,
    val times: List<Long>? = null,
    val reminderTimes: List<Long>? = null,
    val steps: List<RoutineStepRequest>? = null,
    val tags: List<String>? = null
)

// ── Habit Completions ─────────────────────────────────────────────────────────

@Serializable
data class HabitCompletionSyncBatch(
    val created: List<HabitCompletionSyncCreate> = emptyList(),
    val deleted: List<Long> = emptyList()  // server completion IDs to delete
)

@Serializable
data class HabitCompletionSyncCreate(
    val clientId: String,
    val habitId: Long? = null,
    val habitClientId: String? = null,
    val completedAt: Long,
    val habitTimeId: Long? = null,
)
