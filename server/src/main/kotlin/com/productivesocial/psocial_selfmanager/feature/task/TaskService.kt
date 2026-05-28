package com.productivesocial.psocial_selfmanager.feature.task

import com.productivesocial.psocial_selfmanager.model.PaginatedResponse
import com.productivesocial.psocial_selfmanager.model.requests.TaskRequest
import com.productivesocial.psocial_selfmanager.model.requests.UpdateTaskRequest
import com.productivesocial.psocial_selfmanager.model.responses.TaskResponse
import com.productivesocial.psocial_selfmanager.utils.toResponse
import com.productivesocial.psocial_selfmanager.database.entities.ProjectDAO
import com.productivesocial.psocial_selfmanager.database.entities.ProjectTable
import com.productivesocial.psocial_selfmanager.database.entities.SubtaskDAO
import com.productivesocial.psocial_selfmanager.database.entities.TagDAO
import com.productivesocial.psocial_selfmanager.database.entities.TagTable
import com.productivesocial.psocial_selfmanager.database.entities.TaskDAO
import com.productivesocial.psocial_selfmanager.database.entities.TaskTable
import com.productivesocial.psocial_selfmanager.database.entities.TaskTimesDAO
import com.productivesocial.psocial_selfmanager.database.entities.UserDAO
import com.productivesocial.psocial_selfmanager.database.entities.UserTable
import com.productivesocial.psocial_selfmanager.model.PaginationMetadata
import com.productivesocial.psocial_selfmanager.utils.query
import com.productivesocial.psocial_selfmanager.utils.writeTombstone
import io.ktor.server.plugins.NotFoundException
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import kotlin.time.Instant

class TaskService : TaskRepository {
    override suspend fun getTasksByUserId(userId: Long): PaginatedResponse<TaskResponse> =
        query {
            val tasks =
                TaskDAO.Companion.find { TaskTable.userId eq userId }
                    .map { it.toResponse() }
            PaginatedResponse(
                data = tasks,
                metadata = PaginationMetadata(
                    totalItems = tasks.size,
                    currentPage = 1,
                    itemsPerPage = tasks.size.coerceAtLeast(1),
                    totalPages = 1
                )
            )
        }

    override suspend fun getTaskById(userId: Long, taskId: Long): TaskResponse? =
        query {
            TaskDAO.Companion.find { (TaskTable.userId eq userId) and (TaskTable.id eq taskId) }
                .singleOrNull()?.toResponse()
        }

    override suspend fun removeTaskById(userId: Long, taskId: Long): TaskResponse =
        query {
            val task =
                TaskDAO.Companion.find { (TaskTable.userId eq userId) and (TaskTable.id eq taskId) }
                    .singleOrNull() ?: throw NotFoundException("Task not found")

            val response = task.toResponse()
            deleteTaskCascade(task, recordTombstone = true)
            response
        }

    override suspend fun addTask(userId: Long, task: TaskRequest): TaskResponse =
        query {
            validateUserAndProject(userId, task.projectId)

            val newTask =
                TaskDAO.Companion.new {
                    this.userId = EntityID(
                        userId,
                        UserTable
                    )
                    this.projectId = EntityID(
                        task.projectId,
                        ProjectTable
                    )
                    this.name = task.name
                    this.description = task.description
                    this.priority = task.priority
                    this.urgency = task.urgency
                    this.target = task.target
                    this.recurring = task.recurring
                    this.sendReminder = task.sendReminder
                    this.date = task.date?.let { Instant.fromEpochMilliseconds(it) }
                    this.completed = task.completed
                }

            val tagsList = task.tags.map { tagName -> findOrCreateTag(userId, tagName) }
            newTask.tags = SizedCollection(tagsList)

            task.times.forEach { timestamp ->
                TaskTimesDAO.Companion.new {
                    this.taskId = newTask.id
                    this.taskTimes = Instant.fromEpochMilliseconds(timestamp)
                }
            }

            task.subtasks.forEach { subtaskReq ->
                SubtaskDAO.Companion.new {
                    this.name = subtaskReq.name
                    this.completed = subtaskReq.completed
                    this.task = newTask
                }
            }

            newTask.toResponse()
        }

    override suspend fun updateTask(
        userId: Long,
        taskId: Long,
        task: UpdateTaskRequest
    ): TaskResponse =
        query {
            val existingTask =
                TaskDAO.Companion.find { (TaskTable.userId eq userId) and (TaskTable.id eq taskId) }
                    .singleOrNull() ?: throw NotFoundException("Task not found")

            task.projectId?.let {
                validateUserAndProject(userId, it)
                existingTask.projectId = EntityID(
                    it,
                    ProjectTable
                )
            }
            task.name?.let { existingTask.name = it }
            task.description?.let { existingTask.description = it }
            task.priority?.let { existingTask.priority = it }
            task.urgency?.let { existingTask.urgency = it }
            task.target?.let { existingTask.target = it }
            task.recurring?.let { existingTask.recurring = it }
            task.sendReminder?.let { existingTask.sendReminder = it }
            task.date?.let { existingTask.date = Instant.fromEpochMilliseconds(it) }
            task.completed?.let { existingTask.completed = it }

            task.tags?.let { tags ->
                existingTask.tags = SizedCollection(tags.map { findOrCreateTag(userId, it) })
            }

            task.times?.let { times ->
                existingTask.times.forEach { it.delete() }
                times.forEach { timestamp ->
                    TaskTimesDAO.Companion.new {
                        this.taskId = existingTask.id
                        this.taskTimes = Instant.fromEpochMilliseconds(timestamp)
                    }
                }
            }

            task.subtasks?.let { subtasks ->
                existingTask.subtasks.forEach { subtask ->
                    subtask.completionLogs.forEach { it.delete() }
                    subtask.delete()
                }
                subtasks.forEach { subtaskReq ->
                    SubtaskDAO.Companion.new {
                        this.name = subtaskReq.name
                        this.completed = subtaskReq.completed
                        this.task = existingTask
                    }
                }
            }

            existingTask.toResponse()
        }

    private fun findOrCreateTag(userId: Long, tagName: String): TagDAO =
        TagDAO.Companion.find { (TagTable.userId eq userId) and (TagTable.name eq tagName) }
            .singleOrNull()
            ?: TagDAO.Companion.new {
                this.userId = EntityID(userId,
                    UserTable
                )
                this.name = tagName
            }

    private fun validateUserAndProject(userId: Long, projectId: Long) {
        UserDAO.Companion.findById(userId) ?: throw NotFoundException("User not found")
        ProjectDAO.Companion.find { (ProjectTable.id eq projectId) and (ProjectTable.userId eq userId) }
            .singleOrNull() ?: throw NotFoundException("Project not found")
    }
}

fun deleteTaskCascade(task: TaskDAO, recordTombstone: Boolean = false) {
    if (recordTombstone) writeTombstone(
        task.userId.value,
        "task",
        task.id.value
    )
    task.subtasks.forEach { subtask ->
        subtask.completionLogs.forEach { it.delete() }
        subtask.delete()
    }
    task.completionLogs.forEach { it.delete() }
    task.times.forEach { it.delete() }
    task.tags = SizedCollection(emptyList())
    task.delete()
}
