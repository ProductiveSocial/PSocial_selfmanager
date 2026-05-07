package com.productivesocial.com.productivesocial.database.entities

import com.productivesocial.com.productivesocial.constants.HabitType
import com.productivesocial.com.productivesocial.constants.Recurrency
import com.productivesocial.com.productivesocial.database.base.BaseEntity
import com.productivesocial.com.productivesocial.database.base.BaseEntityClass
import com.productivesocial.com.productivesocial.database.base.BaseIdTable
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.datetime.time

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
}

object HabitTimesTable : BaseIdTable("habit_times") {
    val habitId = reference("habit_id", HabitTable)
    val time = time("scheduled_time")
}

object HabitReminderTimesTable : BaseIdTable("habit_reminder_times") {
    val habitId = reference("habit_id", HabitTable)
    val time = time("reminder_time")
}

object HabitSubtasksTable : BaseIdTable("habit_subtasks") {
    val habitId = reference("habit_id", HabitTable)
    val name = varchar("name", 255)
    val completed = bool("completed").default(false)
}

object HabitTagsTable : Table("habit_tags_bridge") {
    val habitId = reference("habit_id", HabitTable)
    val tagId = reference("tag_id", TagTable)
    override val primaryKey = PrimaryKey(habitId, tagId)
}

class HabitTimeDAO(id: EntityID<Long>) : BaseEntity(id, HabitTimesTable) {
    companion object : BaseEntityClass<HabitTimeDAO>(HabitTimesTable, HabitTimeDAO::class.java)
    var time by HabitTimesTable.time
    var habit by HabitDAO referencedOn HabitTimesTable.habitId
}

class HabitReminderTimeDAO(id: EntityID<Long>) : BaseEntity(id, HabitReminderTimesTable) {
    companion object : BaseEntityClass<HabitReminderTimeDAO>(HabitReminderTimesTable, HabitReminderTimeDAO::class.java)
    var time by HabitReminderTimesTable.time
    var habit by HabitDAO referencedOn HabitReminderTimesTable.habitId
}

class HabitSubtaskDAO(id: EntityID<Long>) : BaseEntity(id, HabitSubtasksTable) {
    companion object : BaseEntityClass<HabitSubtaskDAO>(HabitSubtasksTable, HabitSubtaskDAO::class.java)
    var name by HabitSubtasksTable.name
    var completed by HabitSubtasksTable.completed
    var habit by HabitDAO referencedOn HabitSubtasksTable.habitId
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

    val times by HabitTimeDAO referrersOn HabitTimesTable.habitId
    val reminderTimes by HabitReminderTimeDAO referrersOn HabitReminderTimesTable.habitId
    val subtasks by HabitSubtaskDAO referrersOn HabitSubtasksTable.habitId

    var tags by TagDAO via HabitTagsTable
}
