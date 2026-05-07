package com.productivesocial.com.productivesocial.utils

import com.productivesocial.com.productivesocial.database.entities.*
import com.productivesocial.com.productivesocial.model.responses.*

fun TaskDAO.toResponse(): TaskResponse {
    return TaskResponse(
        id = this.id.value,
        userId = this.userId.value,
        projectId = this.projectId.value,
        name = this.name,
        description = this.description,
        priority = this.priority,
        target = this.target,
        recurring = this.recurring,
        sendReminder = this.sendReminder,
        date = this.date,
        completed = this.completed,
        times = this.times.map { it.taskTimes.toString() },
        subtasks = this.subtasks.map { SubtaskResponse(it.id.value, it.name, it.completed, it.type) },
        tags = this.tags.map { TagResponse(it.id.value, it.name) }
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
        tasks = this.tasks.map { it.toResponse() }
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
        times = this.times.map { it.time.toString() },
        reminderTimes = this.reminderTimes.map { it.time.toString() },
        subtasks = this.subtasks.map { HabitSubtaskResponse(it.id.value, it.name, it.completed) },
        tags = this.tags.map { TagResponse(it.id.value, it.name) }
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
        times = this.times.map { it.time.toString() },
        reminderTimes = this.reminderTimes.map { it.time.toString() },
        steps = this.steps.map { RoutineStepResponse(it.id.value, it.name, it.autoStart, it.duration, it.description, it.completed) },
        tags = this.tags.map { TagResponse(it.id.value, it.name) }
    )
}
