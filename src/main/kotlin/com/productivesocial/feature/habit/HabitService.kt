package com.productivesocial.com.productivesocial.feature.habit

import com.productivesocial.com.productivesocial.database.entities.*
import com.productivesocial.com.productivesocial.model.PaginatedResponse
import com.productivesocial.com.productivesocial.model.PaginationMetadata
import com.productivesocial.com.productivesocial.model.requests.HabitRequest
import com.productivesocial.com.productivesocial.model.requests.UpdateHabitRequest
import com.productivesocial.com.productivesocial.model.responses.HabitResponse
import com.productivesocial.com.productivesocial.utils.query
import com.productivesocial.com.productivesocial.utils.toResponse
import io.ktor.server.plugins.NotFoundException
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SizedCollection

class HabitService : HabitRepository {
    override suspend fun getHabitsByUserId(userId: Long): PaginatedResponse<HabitResponse> = query {
        val habits = HabitDAO.find { HabitTable.userId eq userId }.map { it.toResponse() }
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
        HabitDAO.find { (HabitTable.userId eq userId) and (HabitTable.id eq habitId) }
            .singleOrNull()?.toResponse()
    }

    override suspend fun addHabit(userId: Long, habit: HabitRequest): HabitResponse = query {
        validateUserAndProject(userId, habit.projectId)

        val newHabit = HabitDAO.new {
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

        // Handle Tags
        val tagsList = habit.tags.map { tagName ->
            TagDAO.find { TagTable.name eq tagName }.singleOrNull() ?: TagDAO.new { name = tagName }
        }
        newHabit.tags = SizedCollection(tagsList)

        // Handle Times
        habit.times.forEach { timeStr ->
            HabitTimeDAO.new {
                this.habit = newHabit
                this.time = LocalTime.parse(timeStr)
            }
        }

        // Handle Reminder Times
        habit.reminderTimes.forEach { timeStr ->
            HabitReminderTimeDAO.new {
                this.habit = newHabit
                this.time = LocalTime.parse(timeStr)
            }
        }

        // Handle Subtasks
        habit.subtasks.forEach { subtaskReq ->
            HabitSubtaskDAO.new {
                this.name = subtaskReq.name
                this.completed = subtaskReq.completed
                this.habit = newHabit
            }
        }

        newHabit.toResponse()
    }

    override suspend fun updateHabit(userId: Long, habitId: Long, habit: UpdateHabitRequest): HabitResponse = query {
        val existingHabit = HabitDAO.find { (HabitTable.userId eq userId) and (HabitTable.id eq habitId) }
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
            val tagsList = tags.map { tagName ->
                TagDAO.find { TagTable.name eq tagName }.singleOrNull() ?: TagDAO.new { name = tagName }
            }
            existingHabit.tags = SizedCollection(tagsList)
        }

        habit.times?.let { times ->
            existingHabit.times.forEach { it.delete() }
            times.forEach { timeStr ->
                HabitTimeDAO.new {
                    this.habit = existingHabit
                    this.time = LocalTime.parse(timeStr)
                }
            }
        }

        habit.reminderTimes?.let { reminderTimes ->
            existingHabit.reminderTimes.forEach { it.delete() }
            reminderTimes.forEach { timeStr ->
                HabitReminderTimeDAO.new {
                    this.habit = existingHabit
                    this.time = LocalTime.parse(timeStr)
                }
            }
        }

        habit.subtasks?.let { subtasks ->
            existingHabit.subtasks.forEach { it.delete() }
            subtasks.forEach { subtaskReq ->
                HabitSubtaskDAO.new {
                    this.name = subtaskReq.name
                    this.completed = subtaskReq.completed
                    this.habit = existingHabit
                }
            }
        }

        existingHabit.toResponse()
    }

    override suspend fun deleteHabit(userId: Long, habitId: Long): HabitResponse = query {
        val habit = HabitDAO.find { (HabitTable.userId eq userId) and (HabitTable.id eq habitId) }
            .singleOrNull() ?: throw NotFoundException("Habit not found")

        val response = habit.toResponse()
        habit.subtasks.forEach { it.delete() }
        habit.times.forEach { it.delete() }
        habit.reminderTimes.forEach { it.delete() }
        habit.delete()
        response
    }

    private fun validateUserAndProject(userId: Long, projectId: Long) {
        UserDAO.findById(userId) ?: throw NotFoundException("User not found")
        ProjectDAO.find { (ProjectTable.id eq projectId) and (ProjectTable.userId eq userId) }
            .singleOrNull() ?: throw NotFoundException("Project not found")
    }
}
