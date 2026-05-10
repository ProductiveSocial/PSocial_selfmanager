package com.productivesocial.psocial_selfmanager.feature.sync

import com.productivesocial.psocial_selfmanager.feature.task.deleteTaskCascade
import com.productivesocial.psocial_selfmanager.feature.user.UserService
import com.productivesocial.psocial_selfmanager.model.requests.HabitSyncCreate
import com.productivesocial.psocial_selfmanager.model.requests.HabitSyncUpdate
import com.productivesocial.psocial_selfmanager.model.requests.ProjectSyncCreate
import com.productivesocial.psocial_selfmanager.model.requests.ProjectSyncUpdate
import com.productivesocial.psocial_selfmanager.model.requests.RoutineSyncCreate
import com.productivesocial.psocial_selfmanager.model.requests.RoutineSyncUpdate
import com.productivesocial.psocial_selfmanager.model.requests.SyncRequest
import com.productivesocial.psocial_selfmanager.model.requests.TaskSyncCreate
import com.productivesocial.psocial_selfmanager.model.requests.TaskSyncUpdate
import com.productivesocial.psocial_selfmanager.model.responses.DeletedEntityIds
import com.productivesocial.psocial_selfmanager.model.responses.ServerChanges
import com.productivesocial.psocial_selfmanager.model.responses.SyncError
import com.productivesocial.psocial_selfmanager.model.responses.SyncIdMappings
import com.productivesocial.psocial_selfmanager.model.responses.SyncResponse
import com.productivesocial.psocial_selfmanager.utils.toResponse
import com.productivesocial.psocial_selfmanager.utils.writeTombstone
import com.productivesocial.psocial_selfmanager.database.entities.HabitDAO
import com.productivesocial.psocial_selfmanager.database.entities.HabitReminderTimeDAO
import com.productivesocial.psocial_selfmanager.database.entities.HabitSubtaskDAO
import com.productivesocial.psocial_selfmanager.database.entities.HabitTable
import com.productivesocial.psocial_selfmanager.database.entities.HabitTimeDAO
import com.productivesocial.psocial_selfmanager.database.entities.ProjectDAO
import com.productivesocial.psocial_selfmanager.database.entities.ProjectTable
import com.productivesocial.psocial_selfmanager.database.entities.RoutineDAO
import com.productivesocial.psocial_selfmanager.database.entities.RoutineReminderTimeDAO
import com.productivesocial.psocial_selfmanager.database.entities.RoutineStepDAO
import com.productivesocial.psocial_selfmanager.database.entities.RoutineTable
import com.productivesocial.psocial_selfmanager.database.entities.RoutineTimeDAO
import com.productivesocial.psocial_selfmanager.database.entities.SubtaskDAO
import com.productivesocial.psocial_selfmanager.database.entities.SyncTombstoneTable
import com.productivesocial.psocial_selfmanager.database.entities.TagDAO
import com.productivesocial.psocial_selfmanager.database.entities.TagTable
import com.productivesocial.psocial_selfmanager.database.entities.TaskDAO
import com.productivesocial.psocial_selfmanager.database.entities.TaskTable
import com.productivesocial.psocial_selfmanager.database.entities.TaskTimesDAO
import com.productivesocial.psocial_selfmanager.database.entities.UserTable
import com.productivesocial.psocial_selfmanager.feature.habit.deleteHabitCascade
import com.productivesocial.psocial_selfmanager.feature.routine.deleteRoutineCascade
import com.productivesocial.psocial_selfmanager.model.requests.HabitSyncBatch
import com.productivesocial.psocial_selfmanager.model.requests.ProjectSyncBatch
import com.productivesocial.psocial_selfmanager.model.requests.RoutineSyncBatch
import com.productivesocial.psocial_selfmanager.model.requests.TaskSyncBatch
import com.productivesocial.psocial_selfmanager.utils.query
import kotlin.time.Clock
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.core.greaterEq
import org.jetbrains.exposed.v1.core.isNull
import org.jetbrains.exposed.v1.core.or
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import org.jetbrains.exposed.v1.jdbc.selectAll
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import kotlin.time.Instant as KtInstant

class SyncService : SyncRepository {

    override suspend fun sync(request: SyncRequest): SyncResponse =
        query {
            val userId = request.userId
            val syncedAt = Clock.System.now()
            val errors = mutableListOf<SyncError>()

            val projectIdMap = mutableMapOf<String, Long>()
            val taskIdMap = mutableMapOf<String, Long>()
            val habitIdMap = mutableMapOf<String, Long>()
            val routineIdMap = mutableMapOf<String, Long>()

            val projects = request.projects
                ?: ProjectSyncBatch()
            val tasks = request.tasks
                ?: TaskSyncBatch()
            val habits = request.habits
                ?: HabitSyncBatch()
            val routines = request.routines
                ?: RoutineSyncBatch()

            // ── 1. Deletes ────────────────────────────────────────────────────────

            projects.deleted.forEach { id ->
                runCatching {
                    ProjectDAO.Companion.findById(
                        id
                    )
                        ?.takeIf { it.userId.value == userId }
                        ?.let { project ->
                            writeTombstone(
                                userId,
                                "project",
                                id
                            )
                            project.tasks.forEach {
                                deleteTaskCascade(
                                    it,
                                    recordTombstone = true
                                )
                            }
                            project.habits.forEach {
                                deleteHabitCascade(
                                    it,
                                    recordTombstone = true
                                )
                            }
                            project.routines.forEach {
                                deleteRoutineCascade(
                                    it,
                                    recordTombstone = true
                                )
                            }
                            project.delete()
                        }
                }.onFailure {
                    errors += SyncError(
                        "project",
                        "delete",
                        serverId = id,
                        message = it.message ?: "Unknown error"
                    )
                }
            }

            tasks.deleted.forEach { id ->
                runCatching {
                    TaskDAO.Companion.findById(
                        id
                    )
                        ?.takeIf { it.userId.value == userId }
                        ?.let {
                            deleteTaskCascade(
                                it,
                                recordTombstone = true
                            )
                        }
                }.onFailure {
                    errors += SyncError(
                        "task",
                        "delete",
                        serverId = id,
                        message = it.message ?: "Unknown error"
                    )
                }
            }

            habits.deleted.forEach { id ->
                runCatching {
                    HabitDAO.Companion.findById(
                        id
                    )
                        ?.takeIf { it.userId.value == userId }
                        ?.let {
                            deleteHabitCascade(
                                it,
                                recordTombstone = true
                            )
                        }
                }.onFailure {
                    errors += SyncError(
                        "habit",
                        "delete",
                        serverId = id,
                        message = it.message ?: "Unknown error"
                    )
                }
            }

            routines.deleted.forEach { id ->
                runCatching {
                    RoutineDAO.Companion.findById(
                        id
                    )
                        ?.takeIf { it.userId.value == userId }
                        ?.let {
                            deleteRoutineCascade(
                                it,
                                recordTombstone = true
                            )
                        }
                }.onFailure {
                    errors += SyncError(
                        "routine",
                        "delete",
                        serverId = id,
                        message = it.message ?: "Unknown error"
                    )
                }
            }

            // ── 2. Project creates (idempotent via syncId) ────────────────────────

            projects.created.forEach { req ->
                runCatching {
                    // If we already have an entity with this syncId the previous sync
                    // response was lost in transit — return the existing server ID.
                    val existing =
                        ProjectDAO.Companion
                            .find { (ProjectTable.userId eq userId) and (ProjectTable.syncId eq req.clientId) }
                            .singleOrNull()
                    if (existing != null) {
                        projectIdMap[req.clientId] = existing.id.value
                    } else {
                        projectIdMap[req.clientId] = createProject(userId, req).id.value
                    }
                }.onFailure {
                    errors += SyncError(
                        "project",
                        "create",
                        clientId = req.clientId,
                        message = it.message ?: "Unknown error"
                    )
                }
            }

            // ── 3. Project updates ────────────────────────────────────────────────

            projects.updated.forEach { req ->
                runCatching { updateProject(userId, req) }.onFailure {
                    errors += SyncError(
                        "project",
                        "update",
                        serverId = req.id,
                        message = it.message ?: "Unknown error"
                    )
                }
            }

            // Default project fallback for items with no project reference
            val defaultProjectId: Long by lazy {
                ProjectDAO.Companion
                    .find { (ProjectTable.userId eq userId) and (ProjectTable.name eq UserService.Companion.DEFAULT_PROJECT_NAME) }
                    .firstOrNull()?.id?.value
                    ?: error("Default project not found for user $userId")
            }

            // ── 4. Task creates ───────────────────────────────────────────────────

            tasks.created.forEach { req ->
                runCatching {
                    val existing =
                        TaskDAO.Companion
                            .find { (TaskTable.userId eq userId) and (TaskTable.syncId eq req.clientId) }
                            .singleOrNull()
                    if (existing != null) {
                        taskIdMap[req.clientId] = existing.id.value
                    } else {
                        val pid = resolveProjectId(
                            req.projectId,
                            req.projectClientId,
                            projectIdMap,
                            defaultProjectId
                        )
                        taskIdMap[req.clientId] = createTask(userId, pid, req).id.value
                    }
                }.onFailure {
                    errors += SyncError(
                        "task",
                        "create",
                        clientId = req.clientId,
                        message = it.message ?: "Unknown error"
                    )
                }
            }

            // ── 5. Task updates ───────────────────────────────────────────────────

            tasks.updated.forEach { req ->
                runCatching { updateTask(userId, req) }.onFailure {
                    errors += SyncError(
                        "task",
                        "update",
                        serverId = req.id,
                        message = it.message ?: "Unknown error"
                    )
                }
            }

            // ── 6. Habit creates ──────────────────────────────────────────────────

            habits.created.forEach { req ->
                runCatching {
                    val existing =
                        HabitDAO.Companion
                            .find { (HabitTable.userId eq userId) and (HabitTable.syncId eq req.clientId) }
                            .singleOrNull()
                    if (existing != null) {
                        habitIdMap[req.clientId] = existing.id.value
                    } else {
                        val pid = resolveProjectId(
                            req.projectId,
                            req.projectClientId,
                            projectIdMap,
                            defaultProjectId
                        )
                        habitIdMap[req.clientId] = createHabit(userId, pid, req).id.value
                    }
                }.onFailure {
                    errors += SyncError(
                        "habit",
                        "create",
                        clientId = req.clientId,
                        message = it.message ?: "Unknown error"
                    )
                }
            }

            // ── 7. Habit updates ──────────────────────────────────────────────────

            habits.updated.forEach { req ->
                runCatching { updateHabit(userId, req) }.onFailure {
                    errors += SyncError(
                        "habit",
                        "update",
                        serverId = req.id,
                        message = it.message ?: "Unknown error"
                    )
                }
            }

            // ── 8. Routine creates ────────────────────────────────────────────────

            routines.created.forEach { req ->
                runCatching {
                    val existing =
                        RoutineDAO.Companion
                            .find { (RoutineTable.userId eq userId) and (RoutineTable.syncId eq req.clientId) }
                            .singleOrNull()
                    if (existing != null) {
                        routineIdMap[req.clientId] = existing.id.value
                    } else {
                        val pid = resolveProjectId(
                            req.projectId,
                            req.projectClientId,
                            projectIdMap,
                            defaultProjectId
                        )
                        routineIdMap[req.clientId] = createRoutine(userId, pid, req).id.value
                    }
                }.onFailure {
                    errors += SyncError(
                        "routine",
                        "create",
                        clientId = req.clientId,
                        message = it.message ?: "Unknown error"
                    )
                }
            }

            // ── 9. Routine updates ────────────────────────────────────────────────

            routines.updated.forEach { req ->
                runCatching { updateRoutine(userId, req) }.onFailure {
                    errors += SyncError(
                        "routine",
                        "update",
                        serverId = req.id,
                        message = it.message ?: "Unknown error"
                    )
                }
            }

            // ── 10. Build server changes for the client ───────────────────────────

            val serverChanges = buildServerChanges(userId, request.lastSyncedAt)

            SyncResponse(
                idMappings = SyncIdMappings(projectIdMap, taskIdMap, habitIdMap, routineIdMap),
                serverChanges = serverChanges,
                errors = errors,
                syncedAt = syncedAt.toEpochMilliseconds()
            )
        }

    // ── Server changes ────────────────────────────────────────────────────────

    private fun buildServerChanges(userId: Long, lastSyncedAt: Long?): ServerChanges {
        // Newly created entities have updatedAt = null, so we check both columns.
        // The cutoff is exclusive — if lastSyncedAt is null we return everything.
        val cutoff: LocalDateTime? = lastSyncedAt?.let {
            LocalDateTime.ofInstant(Instant.ofEpochMilli(it), ZoneOffset.UTC)
        }

        fun <T> changedSince(
            find: () -> Iterable<T>
        ): List<T> = if (cutoff == null) find().toList() else find().toList()

        val projects = if (cutoff == null) {
            ProjectDAO.Companion.find { ProjectTable.userId eq userId }.map { it.toResponse() }
        } else {
            ProjectDAO.Companion.find {
                (ProjectTable.userId eq userId) and (
                    (ProjectTable.updatedAt greaterEq cutoff) or
                    (ProjectTable.updatedAt.isNull() and (ProjectTable.createdAt greaterEq cutoff))
                )
            }.map { it.toResponse() }
        }

        val tasks = if (cutoff == null) {
            TaskDAO.Companion.find { TaskTable.userId eq userId }.map { it.toResponse() }
        } else {
            TaskDAO.Companion.find {
                (TaskTable.userId eq userId) and (
                    (TaskTable.updatedAt greaterEq cutoff) or
                    (TaskTable.updatedAt.isNull() and (TaskTable.createdAt greaterEq cutoff))
                )
            }.map { it.toResponse() }
        }

        val habits = if (cutoff == null) {
            HabitDAO.Companion.find { HabitTable.userId eq userId }.map { it.toResponse() }
        } else {
            HabitDAO.Companion.find {
                (HabitTable.userId eq userId) and (
                    (HabitTable.updatedAt greaterEq cutoff) or
                    (HabitTable.updatedAt.isNull() and (HabitTable.createdAt greaterEq cutoff))
                )
            }.map { it.toResponse() }
        }

        val routines = if (cutoff == null) {
            RoutineDAO.Companion.find { RoutineTable.userId eq userId }.map { it.toResponse() }
        } else {
            RoutineDAO.Companion.find {
                (RoutineTable.userId eq userId) and (
                    (RoutineTable.updatedAt greaterEq cutoff) or
                    (RoutineTable.updatedAt.isNull() and (RoutineTable.createdAt greaterEq cutoff))
                )
            }.map { it.toResponse() }
        }

        // Tombstones: entities deleted on the server since lastSyncedAt
        val tombstones = if (cutoff == null) emptyList() else {
            val cutoffInstant = KtInstant.fromEpochMilliseconds(lastSyncedAt)
            SyncTombstoneTable
                .selectAll()
                .where { (SyncTombstoneTable.userId eq userId) and (SyncTombstoneTable.deletedAt greaterEq cutoffInstant) }
                .toList()
        }

        val deletedProjectIds = tombstones.filter { it[SyncTombstoneTable.entityType] == "project" }.map { it[SyncTombstoneTable.entityId] }
        val deletedTaskIds = tombstones.filter { it[SyncTombstoneTable.entityType] == "task" }.map { it[SyncTombstoneTable.entityId] }
        val deletedHabitIds = tombstones.filter { it[SyncTombstoneTable.entityType] == "habit" }.map { it[SyncTombstoneTable.entityId] }
        val deletedRoutineIds = tombstones.filter { it[SyncTombstoneTable.entityType] == "routine" }.map { it[SyncTombstoneTable.entityId] }

        return ServerChanges(
            projects = projects,
            tasks = tasks,
            habits = habits,
            routines = routines,
            deletedIds = DeletedEntityIds(deletedProjectIds, deletedTaskIds, deletedHabitIds, deletedRoutineIds)
        )
    }

    // ── Helpers ───────────────────────────────────────────────────────────────

    private fun resolveProjectId(
        projectId: Long?,
        projectClientId: String?,
        projectIdMap: Map<String, Long>,
        defaultProjectId: Long
    ): Long = projectId
        ?: projectClientId?.let { projectIdMap[it] }
        ?: defaultProjectId

    private fun findOrCreateTag(userId: Long, tagName: String): TagDAO =
        TagDAO.Companion.find { (TagTable.userId eq userId) and (TagTable.name eq tagName) }
            .singleOrNull()
            ?: TagDAO.Companion.new {
                this.userId = EntityID(userId,
                    UserTable
                )
                this.name = tagName
            }

    // ── Project operations ────────────────────────────────────────────────────

    private fun createProject(userId: Long, req: ProjectSyncCreate): ProjectDAO {
        val project = ProjectDAO.Companion.new {
            this.userId = EntityID(userId,
                UserTable
            )
            this.name = req.name
            this.description = req.description
            this.iconName = req.iconName
            this.colorHex = req.colorHex
            this.priority = req.priority
            this.syncId = req.clientId
        }
        project.tags = SizedCollection(req.tags.map { findOrCreateTag(userId, it) })
        return project
    }

    private fun updateProject(userId: Long, req: ProjectSyncUpdate) {
        val project = ProjectDAO.Companion.findById(req.id)
            ?.takeIf { it.userId.value == userId } ?: return
        req.name?.let { project.name = it }
        req.description?.let { project.description = it }
        req.iconName?.let { project.iconName = it }
        req.colorHex?.let { project.colorHex = it }
        req.priority?.let { project.priority = it }
        req.tags?.let { project.tags = SizedCollection(it.map { t -> findOrCreateTag(userId, t) }) }
    }

    // ── Task operations ───────────────────────────────────────────────────────

    private fun createTask(userId: Long, projectId: Long, req: TaskSyncCreate): TaskDAO {
        val task = TaskDAO.Companion.new {
            this.userId = EntityID(userId,
                UserTable
            )
            this.projectId = EntityID(projectId,
                ProjectTable
            )
            this.name = req.name
            this.description = req.description
            this.priority = req.priority
            this.target = req.target
            this.recurring = req.recurring
            this.sendReminder = req.sendReminder
            this.date = req.date?.let { KtInstant.fromEpochMilliseconds(it) }
            this.completed = req.completed
            this.syncId = req.clientId
        }
        task.tags = SizedCollection(req.tags.map { findOrCreateTag(userId, it) })
        req.times.forEach { ts ->
            TaskTimesDAO.Companion.new {
                this.taskId = task.id
                this.taskTimes = KtInstant.fromEpochMilliseconds(ts)
            }
        }
        req.subtasks.forEach { s ->
            SubtaskDAO.Companion.new {
                this.name = s.name
                this.completed = s.completed
                this.task = task
            }
        }
        return task
    }

    private fun updateTask(userId: Long, req: TaskSyncUpdate) {
        val task = TaskDAO.Companion.findById(req.id)
            ?.takeIf { it.userId.value == userId } ?: return
        req.projectId?.let { task.projectId = EntityID(it,
            ProjectTable
        ) }
        req.name?.let { task.name = it }
        req.description?.let { task.description = it }
        req.priority?.let { task.priority = it }
        req.target?.let { task.target = it }
        req.recurring?.let { task.recurring = it }
        req.sendReminder?.let { task.sendReminder = it }
        req.date?.let { task.date = KtInstant.fromEpochMilliseconds(it) }
        req.completed?.let { task.completed = it }
        req.tags?.let { task.tags = SizedCollection(it.map { t -> findOrCreateTag(userId, t) }) }
        req.times?.let { times ->
            task.times.forEach { it.delete() }
            times.forEach { ts ->
                TaskTimesDAO.Companion.new {
                    this.taskId = task.id
                    this.taskTimes = KtInstant.fromEpochMilliseconds(ts)
                }
            }
        }
        req.subtasks?.let { subtasks ->
            task.subtasks.forEach { s ->
                s.completionLogs.forEach { it.delete() }
                s.delete()
            }
            subtasks.forEach { s ->
                SubtaskDAO.Companion.new {
                    this.name = s.name
                    this.completed = s.completed
                    this.task = task
                }
            }
        }
    }

    // ── Habit operations ──────────────────────────────────────────────────────

    private fun createHabit(userId: Long, projectId: Long, req: HabitSyncCreate): HabitDAO {
        val habit = HabitDAO.Companion.new {
            this.userId = EntityID(userId,
                UserTable
            )
            this.projectId = EntityID(projectId,
                ProjectTable
            )
            this.name = req.name
            this.description = req.description
            this.habitType = req.habitType
            this.recurrency = req.recurrency
            this.target = req.target
            this.sendReminder = req.sendReminder
            this.completed = req.completed
            this.syncId = req.clientId
        }
        habit.tags = SizedCollection(req.tags.map { findOrCreateTag(userId, it) })
        req.times.forEach { ts ->
            HabitTimeDAO.Companion.new {
                this.habit = habit
                this.time = KtInstant.fromEpochMilliseconds(ts)
            }
        }
        req.reminderTimes.forEach { ts ->
            HabitReminderTimeDAO.Companion.new {
                this.habit = habit
                this.time = KtInstant.fromEpochMilliseconds(ts)
            }
        }
        req.subtasks.forEach { s ->
            HabitSubtaskDAO.Companion.new {
                this.name = s.name
                this.completed = s.completed
                this.habit = habit
            }
        }
        return habit
    }

    private fun updateHabit(userId: Long, req: HabitSyncUpdate) {
        val habit = HabitDAO.Companion.findById(req.id)
            ?.takeIf { it.userId.value == userId } ?: return
        req.projectId?.let { habit.projectId = EntityID(it,
            ProjectTable
        ) }
        req.name?.let { habit.name = it }
        req.description?.let { habit.description = it }
        req.habitType?.let { habit.habitType = it }
        req.recurrency?.let { habit.recurrency = it }
        req.target?.let { habit.target = it }
        req.sendReminder?.let { habit.sendReminder = it }
        req.completed?.let { habit.completed = it }
        req.tags?.let { habit.tags = SizedCollection(it.map { t -> findOrCreateTag(userId, t) }) }
        req.times?.let { times ->
            habit.times.forEach { it.delete() }
            times.forEach { ts ->
                HabitTimeDAO.Companion.new {
                    this.habit = habit
                    this.time = KtInstant.fromEpochMilliseconds(ts)
                }
            }
        }
        req.reminderTimes?.let { reminderTimes ->
            habit.reminderTimes.forEach { it.delete() }
            reminderTimes.forEach { ts ->
                HabitReminderTimeDAO.Companion.new {
                    this.habit = habit
                    this.time = KtInstant.fromEpochMilliseconds(ts)
                }
            }
        }
        req.subtasks?.let { subtasks ->
            habit.subtasks.forEach { s ->
                s.completionLogs.forEach { it.delete() }
                s.delete()
            }
            subtasks.forEach { s ->
                HabitSubtaskDAO.Companion.new {
                    this.name = s.name
                    this.completed = s.completed
                    this.habit = habit
                }
            }
        }
    }

    // ── Routine operations ────────────────────────────────────────────────────

    private fun createRoutine(userId: Long, projectId: Long, req: RoutineSyncCreate): RoutineDAO {
        val routine = RoutineDAO.Companion.new {
            this.userId = EntityID(userId,
                UserTable
            )
            this.projectId = EntityID(projectId,
                ProjectTable
            )
            this.name = req.name
            this.description = req.description
            this.recurrency = req.recurrency
            this.target = req.target
            this.sendReminder = req.sendReminder
            this.completed = req.completed
            this.syncId = req.clientId
        }
        routine.tags = SizedCollection(req.tags.map { findOrCreateTag(userId, it) })
        req.times.forEach { ts ->
            RoutineTimeDAO.Companion.new {
                this.routine = routine
                this.time = KtInstant.fromEpochMilliseconds(ts)
            }
        }
        req.reminderTimes.forEach { ts ->
            RoutineReminderTimeDAO.Companion.new {
                this.routine = routine
                this.time = KtInstant.fromEpochMilliseconds(ts)
            }
        }
        req.steps.forEachIndexed { index, step ->
            RoutineStepDAO.Companion.new {
                this.routine = routine
                this.name = step.name
                this.autoStart = step.autoStart
                this.duration = step.duration
                this.description = step.description
                this.completed = step.completed
                this.position = index
            }
        }
        return routine
    }

    private fun updateRoutine(userId: Long, req: RoutineSyncUpdate) {
        val routine = RoutineDAO.Companion.findById(req.id)
            ?.takeIf { it.userId.value == userId } ?: return
        req.projectId?.let { routine.projectId = EntityID(it,
            ProjectTable
        ) }
        req.name?.let { routine.name = it }
        req.description?.let { routine.description = it }
        req.recurrency?.let { routine.recurrency = it }
        req.target?.let { routine.target = it }
        req.sendReminder?.let { routine.sendReminder = it }
        req.completed?.let { routine.completed = it }
        req.tags?.let { routine.tags = SizedCollection(it.map { t -> findOrCreateTag(userId, t) }) }
        req.times?.let { times ->
            routine.times.forEach { it.delete() }
            times.forEach { ts ->
                RoutineTimeDAO.Companion.new {
                    this.routine = routine
                    this.time = KtInstant.fromEpochMilliseconds(ts)
                }
            }
        }
        req.reminderTimes?.let { reminderTimes ->
            routine.reminderTimes.forEach { it.delete() }
            reminderTimes.forEach { ts ->
                RoutineReminderTimeDAO.Companion.new {
                    this.routine = routine
                    this.time = KtInstant.fromEpochMilliseconds(ts)
                }
            }
        }
        req.steps?.let { steps ->
            routine.steps.forEach { step ->
                step.completionLogs.forEach { it.delete() }
                step.delete()
            }
            steps.forEachIndexed { index, step ->
                RoutineStepDAO.Companion.new {
                    this.routine = routine
                    this.name = step.name
                    this.autoStart = step.autoStart
                    this.duration = step.duration
                    this.description = step.description
                    this.completed = step.completed
                    this.position = index
                }
            }
        }
    }
}
