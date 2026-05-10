package com.productivesocial.psocial_selfmanager.feature.internal

import com.productivesocial.psocial_selfmanager.database.entities.HabitDAO
import com.productivesocial.psocial_selfmanager.database.entities.HabitSubtaskDAO
import com.productivesocial.psocial_selfmanager.database.entities.HabitSubtasksTable
import com.productivesocial.psocial_selfmanager.database.entities.SubtaskDAO
import com.productivesocial.psocial_selfmanager.database.entities.SubtaskTable
import com.productivesocial.psocial_selfmanager.database.entities.TaskDAO
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
    /** If set, also logs time on this specific subtask within the entity. */
    val subtaskId: Long? = null
)

enum class PomodoroEntityType { Task, Habit }

class InternalService {
    suspend fun logTime(request: TimeLogRequest) =
        query {
            when (request.entityType) {
                PomodoroEntityType.Task -> {
                    val task =
                        TaskDAO.Companion.findById(
                            request.entityId
                        )
                            ?: throw NotFoundException("Task ${request.entityId} not found")
                    task.timeSpentMinutes += request.minutesSpent

                    request.subtaskId?.let { subtaskId ->
                        val subtask =
                            SubtaskDAO.Companion.find {
                                (SubtaskTable.id eq subtaskId) and (SubtaskTable.taskId eq task.id)
                            }.singleOrNull()
                                ?: throw NotFoundException("Subtask $subtaskId not found on task ${request.entityId}")
                        subtask.timeSpentMinutes += request.minutesSpent
                    }
                }

                PomodoroEntityType.Habit -> {
                    val habit =
                        HabitDAO.Companion.findById(
                            request.entityId
                        )
                            ?: throw NotFoundException("Habit ${request.entityId} not found")
                    habit.timeSpentMinutes += request.minutesSpent

                    request.subtaskId?.let { subtaskId ->
                        val subtask =
                            HabitSubtaskDAO.Companion.find {
                                (HabitSubtasksTable.id eq subtaskId) and (HabitSubtasksTable.habitId eq habit.id)
                            }.singleOrNull()
                                ?: throw NotFoundException("Habit subtask $subtaskId not found on habit ${request.entityId}")
                        subtask.timeSpentMinutes += request.minutesSpent
                    }
                }
            }
        }
}
