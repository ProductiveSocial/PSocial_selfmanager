package com.productivesocial.com.productivesocial.feature.routine

import com.productivesocial.com.productivesocial.database.entities.*
import com.productivesocial.com.productivesocial.model.PaginatedResponse
import com.productivesocial.com.productivesocial.model.PaginationMetadata
import com.productivesocial.com.productivesocial.model.requests.RoutineRequest
import com.productivesocial.com.productivesocial.model.requests.UpdateRoutineRequest
import com.productivesocial.com.productivesocial.model.responses.RoutineResponse
import com.productivesocial.com.productivesocial.utils.query
import com.productivesocial.com.productivesocial.utils.toResponse
import io.ktor.server.plugins.NotFoundException
import kotlinx.datetime.LocalTime
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SizedCollection

class RoutineService : RoutineRepository {
    override suspend fun getRoutinesByUserId(userId: Long): PaginatedResponse<RoutineResponse> = query {
        val routines = RoutineDAO.find { RoutineTable.userId eq userId }.map { it.toResponse() }
        PaginatedResponse(
            data = routines,
            metadata = PaginationMetadata(
                totalItems = routines.size,
                currentPage = 1,
                itemsPerPage = routines.size.coerceAtLeast(1),
                totalPages = 1
            )
        )
    }

    override suspend fun getRoutineById(userId: Long, routineId: Long): RoutineResponse? = query {
        RoutineDAO.find { (RoutineTable.userId eq userId) and (RoutineTable.id eq routineId) }
            .singleOrNull()?.toResponse()
    }

    override suspend fun addRoutine(userId: Long, routine: RoutineRequest): RoutineResponse = query {
        validateUserAndProject(userId, routine.projectId)

        val newRoutine = RoutineDAO.new {
            this.userId = EntityID(userId, UserTable)
            this.projectId = EntityID(routine.projectId, ProjectTable)
            this.name = routine.name
            this.description = routine.description
            this.recurrency = routine.recurrency
            this.target = routine.target
            this.sendReminder = routine.sendReminder
            this.completed = routine.completed
        }

        val tagsList = routine.tags.map { tagName ->
            TagDAO.find { TagTable.name eq tagName }.singleOrNull() ?: TagDAO.new { name = tagName }
        }
        newRoutine.tags = SizedCollection(tagsList)

        routine.times.forEach { timeStr ->
            RoutineTimeDAO.new {
                this.routine = newRoutine
                this.time = LocalTime.parse(timeStr)
            }
        }

        routine.reminderTimes.forEach { timeStr ->
            RoutineReminderTimeDAO.new {
                this.routine = newRoutine
                this.time = LocalTime.parse(timeStr)
            }
        }

        routine.steps.forEach { stepReq ->
            RoutineStepDAO.new {
                this.routine = newRoutine
                this.name = stepReq.name
                this.autoStart = stepReq.autoStart
                this.duration = stepReq.duration
                this.description = stepReq.description
                this.completed = stepReq.completed
            }
        }

        newRoutine.toResponse()
    }

    override suspend fun updateRoutine(userId: Long, routineId: Long, routine: UpdateRoutineRequest): RoutineResponse = query {
        val existingRoutine = RoutineDAO.find { (RoutineTable.userId eq userId) and (RoutineTable.id eq routineId) }
            .singleOrNull() ?: throw NotFoundException("Routine not found")

        routine.projectId?.let { 
            validateUserAndProject(userId, it)
            existingRoutine.projectId = EntityID(it, ProjectTable) 
        }
        routine.name?.let { existingRoutine.name = it }
        routine.description?.let { existingRoutine.description = it }
        routine.recurrency?.let { existingRoutine.recurrency = it }
        routine.target?.let { existingRoutine.target = it }
        routine.sendReminder?.let { existingRoutine.sendReminder = it }
        routine.completed?.let { existingRoutine.completed = it }

        routine.tags?.let { tags ->
            val tagsList = tags.map { tagName ->
                TagDAO.find { TagTable.name eq tagName }.singleOrNull() ?: TagDAO.new { name = tagName }
            }
            existingRoutine.tags = SizedCollection(tagsList)
        }

        routine.times?.let { times ->
            existingRoutine.times.forEach { it.delete() }
            times.forEach { timeStr ->
                RoutineTimeDAO.new {
                    this.routine = existingRoutine
                    this.time = LocalTime.parse(timeStr)
                }
            }
        }

        routine.reminderTimes?.let { reminderTimes ->
            existingRoutine.reminderTimes.forEach { it.delete() }
            reminderTimes.forEach { timeStr ->
                RoutineReminderTimeDAO.new {
                    this.routine = existingRoutine
                    this.time = LocalTime.parse(timeStr)
                }
            }
        }

        routine.steps?.let { steps ->
            existingRoutine.steps.forEach { it.delete() }
            steps.forEach { stepReq ->
                RoutineStepDAO.new {
                    this.routine = existingRoutine
                    this.name = stepReq.name
                    this.autoStart = stepReq.autoStart
                    this.duration = stepReq.duration
                    this.description = stepReq.description
                    this.completed = stepReq.completed
                }
            }
        }

        existingRoutine.toResponse()
    }

    override suspend fun deleteRoutine(userId: Long, routineId: Long): RoutineResponse = query {
        val routine = RoutineDAO.find { (RoutineTable.userId eq userId) and (RoutineTable.id eq routineId) }
            .singleOrNull() ?: throw NotFoundException("Routine not found")

        val response = routine.toResponse()
        routine.steps.forEach { it.delete() }
        routine.times.forEach { it.delete() }
        routine.reminderTimes.forEach { it.delete() }
        routine.delete()
        response
    }

    private fun validateUserAndProject(userId: Long, projectId: Long) {
        UserDAO.findById(userId) ?: throw NotFoundException("User not found")
        ProjectDAO.find { (ProjectTable.id eq projectId) and (ProjectTable.userId eq userId) }
            .singleOrNull() ?: throw NotFoundException("Project not found")
    }
}
