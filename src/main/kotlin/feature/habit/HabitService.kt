package com.productivesocial.feature.habit

import com.productivesocial.model.PaginatedResponse
import com.productivesocial.model.PaginationMetadata
import com.productivesocial.model.requests.HabitRequest
import com.productivesocial.model.requests.UpdateHabitRequest
import com.productivesocial.model.responses.HabitResponse
import com.productivesocial.utils.query
import com.productivesocial.utils.toResponse
import com.productivesocial.database.entities.HabitDAO
import com.productivesocial.database.entities.HabitReminderTimeDAO
import com.productivesocial.database.entities.HabitSubtaskDAO
import com.productivesocial.database.entities.HabitTable
import com.productivesocial.database.entities.HabitTimeDAO
import com.productivesocial.database.entities.ProjectDAO
import com.productivesocial.database.entities.ProjectTable
import com.productivesocial.database.entities.TagDAO
import com.productivesocial.database.entities.TagTable
import com.productivesocial.database.entities.UserDAO
import com.productivesocial.database.entities.UserTable
import com.productivesocial.utils.writeTombstone
import io.ktor.server.plugins.NotFoundException
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SizedCollection
import kotlin.time.Instant

class HabitService : HabitRepository {
    override suspend fun getHabitsByUserId(userId: Long): PaginatedResponse<HabitResponse> = query {
        val habits = HabitDAO.Companion.find { HabitTable.userId eq userId }.map { it.toResponse() }
        PaginatedResponse(
            data = habits,
            metadata = PaginationMetadata(
                totalItems = habits.size,
                currentPage = 1,
                itemsPerPage = habits.size.coerceAtLeast(1),
                totalPages = 1
            )
        )
    }

    override suspend fun getHabitById(userId: Long, habitId: Long): HabitResponse? = query {
        HabitDAO.Companion.find { (HabitTable.userId eq userId) and (HabitTable.id eq habitId) }
            .singleOrNull()?.toResponse()
    }

    override suspend fun addHabit(userId: Long, habit: HabitRequest): HabitResponse = query {
        validateUserAndProject(userId, habit.projectId)

        val newHabit = HabitDAO.Companion.new {
            this.userId = EntityID(userId, UserTable)
            this.projectId = EntityID(habit.projectId, ProjectTable)
            this.name = habit.name
            this.description = habit.description
            this.habitType = habit.habitType
            this.recurrency = habit.recurrency
            this.target = habit.target
            this.sendReminder = habit.sendReminder
            this.completed = habit.completed
        }

        newHabit.tags = SizedCollection(habit.tags.map { findOrCreateTag(userId, it) })

        habit.times.forEach { timeStamp ->
            HabitTimeDAO.Companion.new {
                this.habit = newHabit
                this.time = Instant.fromEpochMilliseconds(timeStamp)
            }
        }

        habit.reminderTimes.forEach { timeStamp ->
            HabitReminderTimeDAO.Companion.new {
                this.habit = newHabit
                this.time = Instant.fromEpochMilliseconds(timeStamp)
            }
        }

        habit.subtasks.forEach { subtaskReq ->
            HabitSubtaskDAO.Companion.new {
                this.name = subtaskReq.name
                this.completed = subtaskReq.completed
                this.habit = newHabit
            }
        }

        newHabit.toResponse()
    }

    override suspend fun updateHabit(userId: Long, habitId: Long, habit: UpdateHabitRequest): HabitResponse =
        query {
            val existingHabit =
                HabitDAO.Companion.find { (HabitTable.userId eq userId) and (HabitTable.id eq habitId) }
                    .singleOrNull() ?: throw NotFoundException("Habit not found")

            habit.projectId?.let {
                validateUserAndProject(userId, it)
                existingHabit.projectId = EntityID(it, ProjectTable)
            }
            habit.name?.let { existingHabit.name = it }
            habit.description?.let { existingHabit.description = it }
            habit.habitType?.let { existingHabit.habitType = it }
            habit.recurrency?.let { existingHabit.recurrency = it }
            habit.target?.let { existingHabit.target = it }
            habit.sendReminder?.let { existingHabit.sendReminder = it }
            habit.completed?.let { existingHabit.completed = it }

            habit.tags?.let { tags ->
                existingHabit.tags = SizedCollection(tags.map { findOrCreateTag(userId, it) })
            }

            habit.times?.let { times ->
                existingHabit.times.forEach { it.delete() }
                times.forEach { timeStamp ->
                    HabitTimeDAO.Companion.new {
                        this.habit = existingHabit
                        this.time = Instant.fromEpochMilliseconds(timeStamp)
                    }
                }
            }

            habit.reminderTimes?.let { reminderTimes ->
                existingHabit.reminderTimes.forEach { it.delete() }
                reminderTimes.forEach { timeStamp ->
                    HabitReminderTimeDAO.Companion.new {
                        this.habit = existingHabit
                        this.time = Instant.fromEpochMilliseconds(timeStamp)
                    }
                }
            }

            habit.subtasks?.let { subtasks ->
                existingHabit.subtasks.forEach { subtask ->
                    subtask.completionLogs.forEach { it.delete() }
                    subtask.delete()
                }
                subtasks.forEach { subtaskReq ->
                    HabitSubtaskDAO.Companion.new {
                        this.name = subtaskReq.name
                        this.completed = subtaskReq.completed
                        this.habit = existingHabit
                    }
                }
            }

            existingHabit.toResponse()
        }

    override suspend fun deleteHabit(userId: Long, habitId: Long): HabitResponse = query {
        val habit =
            HabitDAO.Companion.find { (HabitTable.userId eq userId) and (HabitTable.id eq habitId) }
                .singleOrNull() ?: throw NotFoundException("Habit not found")

        val response = habit.toResponse()
        deleteHabitCascade(habit, recordTombstone = true)
        response
    }

    private fun findOrCreateTag(userId: Long, tagName: String): TagDAO =
        TagDAO.Companion.find { (TagTable.userId eq userId) and (TagTable.name eq tagName) }
            .singleOrNull()
            ?: TagDAO.Companion.new {
                this.userId = EntityID(userId, UserTable)
                this.name = tagName
            }

    private fun validateUserAndProject(userId: Long, projectId: Long) {
        UserDAO.Companion.findById(userId) ?: throw NotFoundException("User not found")
        ProjectDAO.Companion.find { (ProjectTable.id eq projectId) and (ProjectTable.userId eq userId) }
            .singleOrNull() ?: throw NotFoundException("Project not found")
    }
}

fun deleteHabitCascade(habit: HabitDAO, recordTombstone: Boolean = false) {
    if (recordTombstone) writeTombstone(habit.userId.value, "habit", habit.id.value)
    habit.subtasks.forEach { subtask ->
        subtask.completionLogs.forEach { it.delete() }
        subtask.delete()
    }
    habit.completionLogs.forEach { it.delete() }
    habit.times.forEach { it.delete() }
    habit.reminderTimes.forEach { it.delete() }
    habit.delete()
}
