package com.productivesocial.com.productivesocial.database.entities

import com.productivesocial.com.productivesocial.constants.Priority
import com.productivesocial.com.productivesocial.database.base.BaseEntity
import com.productivesocial.com.productivesocial.database.base.BaseEntityClass
import com.productivesocial.com.productivesocial.database.base.BaseIdTable
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.datetime.date
import org.jetbrains.exposed.v1.datetime.time

object TaskTable : BaseIdTable("task") {
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val projectId = reference("project_id", ProjectTable)
    //    val goalId = reference("goal_id", Goal)
    val priority = enumerationByName("priority", 20, Priority::class)
    val target = varchar("target", 100).nullable()
    val recurring = bool("recurring").default(false)
    val sendReminder = bool("send_reminder").default(false)
    val date = date("date").nullable()
    val completed = bool("completed").default(false)
}

class TaskDAO(id: EntityID<Long>) : BaseEntity(id, TaskTable) {
    companion object : BaseEntityClass<TaskDAO>(TaskTable, TaskDAO::class.java)

    var name by TaskTable.name
    var description by TaskTable.description
    var projectId by TaskTable.projectId
    //    var goalId by TaskTable.goalId
    var priority by TaskTable.priority
    var tags by TagDAO via TaskTags
    val times by TaskTimesDAO referrersOn TaskTimesTable.taskId
    var target by TaskTable.target
    var recurring by TaskTable.recurring
    var sendReminder by TaskTable.sendReminder
    var date by TaskTable.date
    var completed by TaskTable.completed
    val subtasks by SubtaskDAO referrersOn SubtaskTable.taskId
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
