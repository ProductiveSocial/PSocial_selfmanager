package com.productivesocial.psocial_selfmanager.database.entities

import com.productivesocial.psocial_selfmanager.constants.Priority
import com.productivesocial.psocial_selfmanager.database.base.BaseEntity
import com.productivesocial.psocial_selfmanager.database.base.BaseEntityClass
import com.productivesocial.psocial_selfmanager.database.base.BaseIdTable
import com.productivesocial.psocial_selfmanager.model.responses.TaskResponse
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.datetime.timestamp

object TaskTable : BaseIdTable("tasks") {
    val userId = reference("user_id", UserTable.id)
    val projectId = reference("project_id", ProjectTable.id)
    //    val goalId = reference("goal_id", Goal)
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val priority = enumerationByName("priority", 20, Priority::class)
    val target = varchar("target", 100).nullable()
    val recurring = bool("recurring").default(false)
    val sendReminder = bool("send_reminder").default(false)
    val date = timestamp("date").nullable()
    val completed = bool("completed").default(false)
    val urgency = varchar("urgency", 50).nullable()
    val timeSpentMinutes = integer("time_spent_minutes").default(0)
    /** Client-generated UUID used for idempotent sync. Null for entities created via regular API. */
    val syncId = varchar("sync_id", 36).nullable()

    init {
        uniqueIndex(userId, syncId)
    }
}

object TaskCompletionLogTable : BaseIdTable("task_completion_log") {
    val taskId = reference("task_id", TaskTable)
    val completedAt = timestamp("completed_at")
}

class TaskDAO(id: EntityID<Long>) : BaseEntity(id, TaskTable) {
    companion object : BaseEntityClass<TaskDAO>(TaskTable, TaskDAO::class.java)

    var userId by TaskTable.userId
    var projectId by TaskTable.projectId
    //    var goalId by TaskTable.goalId
    var name by TaskTable.name
    var description by TaskTable.description
    var priority by TaskTable.priority
    var tags by TagDAO via TaskTags
    val times by TaskTimesDAO.Companion referrersOn TaskTimesTable.taskId
    var urgency by TaskTable.urgency
    var target by TaskTable.target
    var recurring by TaskTable.recurring
    var sendReminder by TaskTable.sendReminder
    var date by TaskTable.date
    var completed by TaskTable.completed
    var timeSpentMinutes by TaskTable.timeSpentMinutes
    var syncId by TaskTable.syncId
    val subtasks by SubtaskDAO referrersOn SubtaskTable.taskId
    val completionLogs by TaskCompletionLogDAO.Companion referrersOn TaskCompletionLogTable.taskId

    fun response() =
        TaskResponse(
            id = id.value,
            userId = userId.value,
            projectId = projectId.value,
            name = name,
            description = description,
            priority = priority,
            urgency = urgency,
            target = target,
            recurring = recurring,
            sendReminder = sendReminder,
            date = date?.toEpochMilliseconds(),
            completed = completed,
            times = times.map { it.taskTimes.toEpochMilliseconds() },
            subtasks = subtasks.map { it.response() },
            tags = tags.map { it.response() }
        )
}

class TaskCompletionLogDAO(id: EntityID<Long>) : BaseEntity(id, TaskCompletionLogTable) {
    companion object : BaseEntityClass<TaskCompletionLogDAO>(TaskCompletionLogTable, TaskCompletionLogDAO::class.java)
    var task by TaskDAO referencedOn TaskCompletionLogTable.taskId
    var completedAt by TaskCompletionLogTable.completedAt
    val subtaskCompletionLogs by SubtaskCompletionLogDAO optionalReferrersOn SubtaskCompletionLogTable.taskCompletionLogId
}

object TaskTags : Table("task_tags") {
    val taskId = reference("task_id", TaskTable)
    val tagId = reference("tag_id", TagTable)
    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(taskId, tagId)
}

object TaskTimesTable : BaseIdTable("task_times") {
    val taskId = reference("task_id", TaskTable)
    val taskTime = timestamp("task_time")
}

class TaskTimesDAO(id: EntityID<Long>) : BaseEntity(id, TaskTimesTable) {
    companion object : BaseEntityClass<TaskTimesDAO>(TaskTimesTable, TaskTimesDAO::class.java)

    var taskId by TaskTimesTable.taskId
    var taskTimes by TaskTimesTable.taskTime
}
