package com.productivesocial.psocial_selfmanager.database.entities

import com.productivesocial.psocial_selfmanager.constants.HabitType
import com.productivesocial.psocial_selfmanager.constants.Recurrency
import com.productivesocial.psocial_selfmanager.database.base.BaseEntity
import com.productivesocial.psocial_selfmanager.database.base.BaseEntityClass
import com.productivesocial.psocial_selfmanager.database.base.BaseIdTable
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.datetime.timestamp

object HabitTable : BaseIdTable("habits") {
    val userId = reference("user_id", UserTable.id)
    val projectId = reference("project_id", ProjectTable.id)
    val name = varchar("name", 255)
    val description = text("description").nullable()
    val habitType = enumerationByName("habit_type", 50, HabitType::class)
    val recurrency = enumerationByName("recurrency", 50, Recurrency::class)
    val target = varchar("target", 255)
    val sendReminder = bool("send_reminder").default(false)
    val completed = bool("completed").default(false)
    val timeSpentMinutes = integer("time_spent_minutes").default(0)
    /** Client-generated UUID used for idempotent sync. Null for entities created via regular API. */
    val syncId = varchar("sync_id", 36).nullable()

    init {
        uniqueIndex(userId, syncId)
    }
}

object HabitTimesTable : BaseIdTable("habit_times") {
    val habitId = reference("habit_id", HabitTable)
    val time = timestamp("scheduled_time")
}

object HabitReminderTimesTable : BaseIdTable("habit_reminder_times") {
    val habitId = reference("habit_id", HabitTable)
    val time = timestamp("reminder_time")
}

object HabitSubtasksTable : BaseIdTable("habit_subtasks") {
    val habitId = reference("habit_id", HabitTable)
    val name = varchar("name", 255)
    val completed = bool("completed").default(false)
    val timeSpentMinutes = integer("time_spent_minutes").default(0)
}

object HabitTagsTable : Table("habit_tags_bridge") {
    val habitId = reference("habit_id", HabitTable)
    val tagId = reference("tag_id", TagTable)
    override val primaryKey = PrimaryKey(habitId, tagId)
}

object HabitCompletionLogTable : BaseIdTable("habit_completion_log") {
    val habitId = reference("habit_id", HabitTable)
    val completedAt = timestamp("completed_at")
    val habitTimeId = reference("habit_time_id", HabitTimesTable).nullable()
    /** Client-generated UUID for idempotent sync creates. */
    val syncId = varchar("sync_id", 36).nullable()
}

object HabitSubtaskCompletionLogTable : BaseIdTable("habit_subtask_completion_log") {
    val habitSubtaskId = reference("habit_subtask_id", HabitSubtasksTable)
    val completedAt = timestamp("completed_at")
    val habitCompletionLogId = reference("habit_completion_log_id", HabitCompletionLogTable).nullable()
}

class HabitTimeDAO(id: EntityID<Long>) : BaseEntity(id, HabitTimesTable) {
    companion object : BaseEntityClass<HabitTimeDAO>(HabitTimesTable, HabitTimeDAO::class.java)
    var time by HabitTimesTable.time
    var habit by HabitDAO.Companion referencedOn HabitTimesTable.habitId
}

class HabitReminderTimeDAO(id: EntityID<Long>) : BaseEntity(id, HabitReminderTimesTable) {
    companion object : BaseEntityClass<HabitReminderTimeDAO>(HabitReminderTimesTable, HabitReminderTimeDAO::class.java)
    var time by HabitReminderTimesTable.time
    var habit by HabitDAO.Companion referencedOn HabitReminderTimesTable.habitId
}

class HabitSubtaskDAO(id: EntityID<Long>) : BaseEntity(id, HabitSubtasksTable) {
    companion object : BaseEntityClass<HabitSubtaskDAO>(HabitSubtasksTable, HabitSubtaskDAO::class.java)
    var name by HabitSubtasksTable.name
    var completed by HabitSubtasksTable.completed
    var timeSpentMinutes by HabitSubtasksTable.timeSpentMinutes
    var habit by HabitDAO.Companion referencedOn HabitSubtasksTable.habitId
    val completionLogs by HabitSubtaskCompletionLogDAO.Companion referrersOn HabitSubtaskCompletionLogTable.habitSubtaskId
}

class HabitCompletionLogDAO(id: EntityID<Long>) : BaseEntity(id, HabitCompletionLogTable) {
    companion object : BaseEntityClass<HabitCompletionLogDAO>(HabitCompletionLogTable, HabitCompletionLogDAO::class.java)
    var habit by HabitDAO.Companion referencedOn HabitCompletionLogTable.habitId
    var completedAt by HabitCompletionLogTable.completedAt
    var habitTime by HabitTimeDAO optionalReferencedOn HabitCompletionLogTable.habitTimeId
    var syncId by HabitCompletionLogTable.syncId
    val subtaskCompletionLogs by HabitSubtaskCompletionLogDAO.Companion optionalReferrersOn HabitSubtaskCompletionLogTable.habitCompletionLogId
}

class HabitSubtaskCompletionLogDAO(id: EntityID<Long>) : BaseEntity(id, HabitSubtaskCompletionLogTable) {
    companion object : BaseEntityClass<HabitSubtaskCompletionLogDAO>(HabitSubtaskCompletionLogTable, HabitSubtaskCompletionLogDAO::class.java)
    var habitSubtask by HabitSubtaskDAO referencedOn HabitSubtaskCompletionLogTable.habitSubtaskId
    var completedAt by HabitSubtaskCompletionLogTable.completedAt
    var habitCompletionLog by HabitCompletionLogDAO optionalReferencedOn HabitSubtaskCompletionLogTable.habitCompletionLogId
}

class HabitDAO(id: EntityID<Long>) : BaseEntity(id, HabitTable) {
    companion object : BaseEntityClass<HabitDAO>(HabitTable, HabitDAO::class.java)

    var userId by HabitTable.userId
    var name by HabitTable.name
    var description by HabitTable.description
    var projectId by HabitTable.projectId
    var habitType by HabitTable.habitType
    var recurrency by HabitTable.recurrency
    var target by HabitTable.target
    var sendReminder by HabitTable.sendReminder
    var completed by HabitTable.completed
    var timeSpentMinutes by HabitTable.timeSpentMinutes
    var syncId by HabitTable.syncId

    val times by HabitTimeDAO referrersOn HabitTimesTable.habitId
    val reminderTimes by HabitReminderTimeDAO referrersOn HabitReminderTimesTable.habitId
    val subtasks by HabitSubtaskDAO referrersOn HabitSubtasksTable.habitId
    val completionLogs by HabitCompletionLogDAO referrersOn HabitCompletionLogTable.habitId

    var tags by TagDAO.Companion via HabitTagsTable
}
