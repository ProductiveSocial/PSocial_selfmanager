package com.productivesocial.database.entities

import com.productivesocial.constants.Priority
import com.productivesocial.database.base.BaseEntity
import com.productivesocial.database.base.BaseEntityClass
import com.productivesocial.database.base.BaseIdTable
import com.productivesocial.model.responses.TaskResponse
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.datetime.time

object TaskTable : BaseIdTable("task") {
    val userId = reference("user_id", UserTable.id)
    val projectId = reference("project_id", ProjectTable.id)
    //    val goalId = reference("goal_id", Goal)
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val priority = enumerationByName("priority", 20, Priority::class)
    val target = varchar("target", 100).nullable()
    val recurring = bool("recurring").default(false)
    val sendReminder = bool("send_reminder").default(false)
    val date = varchar("date", 25).nullable()
    val completed = bool("completed").default(false)
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
    var target by TaskTable.target
    var recurring by TaskTable.recurring
    var sendReminder by TaskTable.sendReminder
    var date by TaskTable.date
    var completed by TaskTable.completed
    val subtasks by SubtaskDAO referrersOn SubtaskTable.taskId

    fun response() = TaskResponse(
        id = id.value,
        userId = userId.value,
        projectId = projectId.value,
        name = name,
        description = description,
        priority = priority,
        target = target,
        recurring = recurring,
        sendReminder = sendReminder,
        date = date,
        completed = completed,
        times = times.map { it.taskTimes.toString() },
        subtasks = subtasks.map { it.response() },
        tags = tags.map { it.response() }
    )
}

object TaskTags : Table("task_tags") {
    val taskId = reference("task_id", TaskTable)
    val tagId = reference("tag_id", TagTable)
    override val primaryKey: PrimaryKey?
        get() = PrimaryKey(taskId, tagId)
}

object TaskTimesTable : BaseIdTable("task_times") {
    val taskId = reference("task_id", TaskTable)
    val taskTime = time("task_time")
}

class TaskTimesDAO(id: EntityID<Long>) : BaseEntity(id, TaskTimesTable) {
    companion object : BaseEntityClass<TaskTimesDAO>(TaskTimesTable, TaskTimesDAO::class.java)

    var taskId by TaskTimesTable.taskId
    var taskTimes by TaskTimesTable.taskTime
}
