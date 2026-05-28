package com.productivesocial.psocial_selfmanager.utils

import com.productivesocial.psocial_selfmanager.model.responses.RoutineStepResponse
import com.productivesocial.psocial_selfmanager.model.responses.HabitSubtaskResponse
import com.productivesocial.psocial_selfmanager.model.responses.TagResponse
import com.productivesocial.psocial_selfmanager.database.entities.HabitDAO
import com.productivesocial.psocial_selfmanager.database.entities.ProjectDAO
import com.productivesocial.psocial_selfmanager.database.entities.RoutineDAO
import com.productivesocial.psocial_selfmanager.database.entities.TaskDAO
import com.productivesocial.psocial_selfmanager.model.responses.HabitResponse
import com.productivesocial.psocial_selfmanager.model.responses.ProjectResponse
import com.productivesocial.psocial_selfmanager.model.responses.RoutineResponse
import com.productivesocial.psocial_selfmanager.model.responses.SubtaskResponse
import com.productivesocial.psocial_selfmanager.model.responses.TaskResponse

fun TaskDAO.toResponse(): TaskResponse {
    return TaskResponse(
        id = this.id.value,
        userId = this.userId.value,
        projectId = this.projectId.value,
        name = this.name,
        description = this.description,
        priority = this.priority,
        urgency = this.urgency,
        target = this.target,
        recurring = this.recurring,
        sendReminder = this.sendReminder,
        date = this.date?.toEpochMilliseconds(),
        completed = this.completed,
        times = this.times.map { it.taskTimes.toEpochMilliseconds() },
        subtasks = this.subtasks.map {
            SubtaskResponse(
                it.id.value,
                it.name,
                it.completed
            )
        },
        tags = this.tags.map {
            TagResponse(
                it.id.value,
                it.name
            )
        }
    )
}

fun ProjectDAO.toResponse(): ProjectResponse {
    return ProjectResponse(
        id = this.id.value,
        userId = this.userId.value,
        name = this.name,
        description = this.description,
        iconName = this.iconName,
        colorHex = this.colorHex,
        priority = this.priority,
        habits = this.habits.map { it.toResponse() },
        routines = this.routines.map { it.toResponse() },
        tasks = this.tasks.map { it.toResponse() },
        tags = this.tags.map {
            TagResponse(
                it.id.value,
                it.name
            )
        }
    )
}

fun HabitDAO.toResponse(): HabitResponse {
    return HabitResponse(
        id = this.id.value,
        userId = this.userId.value,
        projectId = this.projectId.value,
        name = this.name,
        description = this.description,
        habitType = this.habitType,
        recurrency = this.recurrency,
        target = this.target,
        sendReminder = this.sendReminder,
        completed = this.completed,
        times = this.times.map { it.time.toEpochMilliseconds() },
        reminderTimes = this.reminderTimes.map { it.time.toEpochMilliseconds() },
        subtasks = this.subtasks.map {
            HabitSubtaskResponse(
                it.id.value,
                it.name,
                it.completed
            )
        },
        tags = this.tags.map {
            TagResponse(
                it.id.value,
                it.name
            )
        }
    )
}

fun RoutineDAO.toResponse(): RoutineResponse {
    return RoutineResponse(
        id = this.id.value,
        userId = this.userId.value,
        projectId = this.projectId.value,
        name = this.name,
        description = this.description,
        recurrency = this.recurrency,
        target = this.target,
        sendReminder = this.sendReminder,
        completed = this.completed,
        times = this.times.map { it.time.toEpochMilliseconds() },
        reminderTimes = this.reminderTimes.map { it.time.toEpochMilliseconds() },
        steps = this.steps.map {
            RoutineStepResponse(
                it.id.value,
                it.name,
                it.autoStart,
                it.duration,
                it.description,
                it.completed
            )
        },
        tags = this.tags.map {
            TagResponse(
                it.id.value,
                it.name
            )
        }
    )
}
