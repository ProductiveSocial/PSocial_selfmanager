package com.productivesocial.database.entities

import com.productivesocial.constants.Recurrency
import com.productivesocial.database.base.BaseEntity
import com.productivesocial.database.base.BaseEntityClass
import com.productivesocial.database.base.BaseIdTable
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.datetime.timestamp

object RoutineTable : BaseIdTable("routines") {
    val userId = reference("user_id", UserTable.id)
    val projectId = reference("project_id", ProjectTable.id)
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val recurrency = enumerationByName("recurrency", 50, Recurrency::class)
    val target = varchar("target", 255)
    val sendReminder = bool("send_reminder").default(false)
    val completed = bool("completed").default(false)
    /** Client-generated UUID used for idempotent sync. Null for entities created via regular API. */
    val syncId = varchar("sync_id", 36).nullable()

    init {
        uniqueIndex(userId, syncId)
    }
}

object RoutineTimesTable : BaseIdTable("routine_times") {
    val routineId = reference("routine_id", RoutineTable)
    val time = timestamp("scheduled_time")
}

object RoutineReminderTimesTable : BaseIdTable("routine_reminder_times") {
    val routineId = reference("routine_id", RoutineTable)
    val time = timestamp("reminder_time")
}

object RoutineTagsTable : Table("routine_tags_bridge") {
    val routineId = reference("routine_id", RoutineTable)
    val tagId = reference("tag_id", TagTable)
    override val primaryKey = PrimaryKey(routineId, tagId)
}

object RoutineCompletionLogTable : BaseIdTable("routine_completion_log") {
    val routineId = reference("routine_id", RoutineTable)
    val completedAt = timestamp("completed_at")
    val routineTimeId = reference("routine_time_id", RoutineTimesTable).nullable()
}

class RoutineTimeDAO(id: EntityID<Long>) : BaseEntity(id, RoutineTimesTable) {
    companion object : BaseEntityClass<RoutineTimeDAO>(RoutineTimesTable, RoutineTimeDAO::class.java)
    var time by RoutineTimesTable.time
    var routine by RoutineDAO.Companion referencedOn RoutineTimesTable.routineId
}

class RoutineReminderTimeDAO(id: EntityID<Long>) : BaseEntity(id, RoutineReminderTimesTable) {
    companion object : BaseEntityClass<RoutineReminderTimeDAO>(RoutineReminderTimesTable, RoutineReminderTimeDAO::class.java)
    var time by RoutineReminderTimesTable.time
    var routine by RoutineDAO.Companion referencedOn RoutineReminderTimesTable.routineId
}

class RoutineCompletionLogDAO(id: EntityID<Long>) : BaseEntity(id, RoutineCompletionLogTable) {
    companion object : BaseEntityClass<RoutineCompletionLogDAO>(RoutineCompletionLogTable, RoutineCompletionLogDAO::class.java)
    var routine by RoutineDAO referencedOn RoutineCompletionLogTable.routineId
    var completedAt by RoutineCompletionLogTable.completedAt
    var routineTime by RoutineTimeDAO optionalReferencedOn RoutineCompletionLogTable.routineTimeId
    val stepCompletionLogs by RoutineStepCompletionLogDAO optionalReferrersOn RoutineStepCompletionLogTable.routineCompletionLogId
}

class RoutineDAO(id: EntityID<Long>) : BaseEntity(id, RoutineTable) {
    companion object : BaseEntityClass<RoutineDAO>(RoutineTable, RoutineDAO::class.java)

    var userId by RoutineTable.userId
    var name by RoutineTable.name
    var description by RoutineTable.description
    var projectId by RoutineTable.projectId
    var recurrency by RoutineTable.recurrency
    var target by RoutineTable.target
    var sendReminder by RoutineTable.sendReminder
    var completed by RoutineTable.completed
    var syncId by RoutineTable.syncId

    val times by RoutineTimeDAO referrersOn RoutineTimesTable.routineId
    val reminderTimes by RoutineReminderTimeDAO referrersOn RoutineReminderTimesTable.routineId
    val steps by RoutineStepDAO.Companion referrersOn RoutineStepsTable.routineId
    val completionLogs by RoutineCompletionLogDAO referrersOn RoutineCompletionLogTable.routineId
    var tags by TagDAO.Companion via RoutineTagsTable
}
