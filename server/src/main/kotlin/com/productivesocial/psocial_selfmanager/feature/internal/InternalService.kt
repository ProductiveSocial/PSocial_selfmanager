package com.productivesocial.psocial_selfmanager.feature.internal

import com.productivesocial.psocial_selfmanager.database.entities.HabitDAO
import com.productivesocial.psocial_selfmanager.database.entities.HabitSubtaskDAO
import com.productivesocial.psocial_selfmanager.database.entities.HabitSubtasksTable
import com.productivesocial.psocial_selfmanager.database.entities.HabitTable
import com.productivesocial.psocial_selfmanager.database.entities.RoutineDAO
import com.productivesocial.psocial_selfmanager.database.entities.RoutineTable
import com.productivesocial.psocial_selfmanager.database.entities.SubtaskDAO
import com.productivesocial.psocial_selfmanager.database.entities.SubtaskTable
import com.productivesocial.psocial_selfmanager.database.entities.TaskDAO
import com.productivesocial.psocial_selfmanager.database.entities.TaskTable
import com.productivesocial.psocial_selfmanager.utils.query
import io.ktor.server.plugins.NotFoundException
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.eq

@Serializable
data class TimeLogRequest(
    val entityType: PomodoroEntityType,
    val entityId: Long,
    val minutesSpent: Int,
    val subtaskId: Long? = null,
)

enum class PomodoroEntityType { Task, Habit }

@Serializable
data class InternalTaskResponse(
    val id: Long,
    val name: String,
    val priority: String,
    val completed: Boolean,
    val timeSpentMinutes: Int,
)

@Serializable
data class InternalHabitResponse(
    val id: Long,
    val name: String,
    val habitType: String,
    val recurrency: String,
    val timeSpentMinutes: Int,
)

@Serializable
data class InternalRoutineResponse(
    val id: Long,
    val name: String,
    val recurrency: String,
)

class InternalService {

    suspend fun logTime(request: TimeLogRequest) = query {
        when (request.entityType) {
            PomodoroEntityType.Task -> {
                val task = TaskDAO.findById(request.entityId)
                    ?: throw NotFoundException("Task ${request.entityId} not found")
                task.timeSpentMinutes += request.minutesSpent
                request.subtaskId?.let { subtaskId ->
                    val subtask = SubtaskDAO.find {
                        (SubtaskTable.id eq subtaskId) and (SubtaskTable.taskId eq task.id)
                    }.singleOrNull() ?: throw NotFoundException("Subtask $subtaskId not found")
                    subtask.timeSpentMinutes += request.minutesSpent
                }
            }
            PomodoroEntityType.Habit -> {
                val habit = HabitDAO.findById(request.entityId)
                    ?: throw NotFoundException("Habit ${request.entityId} not found")
                habit.timeSpentMinutes += request.minutesSpent
                request.subtaskId?.let { subtaskId ->
                    val subtask = HabitSubtaskDAO.find {
                        (HabitSubtasksTable.id eq subtaskId) and (HabitSubtasksTable.habitId eq habit.id)
                    }.singleOrNull() ?: throw NotFoundException("Habit subtask $subtaskId not found")
                    subtask.timeSpentMinutes += request.minutesSpent
                }
            }
        }
    }

    suspend fun getUserTasks(userId: Long): List<InternalTaskResponse> = query {
        TaskDAO.find { TaskTable.userId eq userId }.map {
            InternalTaskResponse(
                id = it.id.value,
                name = it.name,
                priority = it.priority.name,
                completed = it.completed,
                timeSpentMinutes = it.timeSpentMinutes,
            )
        }
    }

    suspend fun getUserHabits(userId: Long): List<InternalHabitResponse> = query {
        HabitDAO.find { HabitTable.userId eq userId }.map {
            InternalHabitResponse(
                id = it.id.value,
                name = it.name,
                habitType = it.habitType.name,
                recurrency = it.recurrency.name,
                timeSpentMinutes = it.timeSpentMinutes,
            )
        }
    }

    suspend fun getUserRoutines(userId: Long): List<InternalRoutineResponse> = query {
        RoutineDAO.find { RoutineTable.userId eq userId }.map {
            InternalRoutineResponse(
                id = it.id.value,
                name = it.name,
                recurrency = it.recurrency.name,
            )
        }
    }
}
