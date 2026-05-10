package com.productivesocial.psocial_selfmanager.database.entities

import com.productivesocial.psocial_selfmanager.constants.Priority
import com.productivesocial.psocial_selfmanager.database.base.BaseEntity
import com.productivesocial.psocial_selfmanager.database.base.BaseEntityClass
import com.productivesocial.psocial_selfmanager.database.base.BaseIdTable
import org.jetbrains.exposed.v1.core.Table
import org.jetbrains.exposed.v1.core.dao.id.EntityID

object ProjectTable : BaseIdTable("projects") {
    val userId = reference("user_id", UserTable.id)
    val name = varchar("name", 80)
    val description = text("description").nullable()
    val iconName = varchar("icon_name", 50)
    val colorHex = varchar("color_hex", 7)
    val priority = enumerationByName("priority", 20, Priority::class)
    /** Client-generated UUID used for idempotent sync. Null for entities created via regular API. */
    val syncId = varchar("sync_id", 36).nullable()

    init {
        uniqueIndex(userId, syncId)
    }
}

object ProjectTagsTable : Table("project_tags_bridge") {
    val projectId = reference("project_id", ProjectTable)
    val tagId = reference("tag_id", TagTable)
    override val primaryKey = PrimaryKey(projectId, tagId)
}

class ProjectDAO(id: EntityID<Long>) : BaseEntity(id, ProjectTable) {
    companion object : BaseEntityClass<ProjectDAO>(ProjectTable, ProjectDAO::class.java)

    var userId by ProjectTable.userId
    var name by ProjectTable.name
    var description by ProjectTable.description
    var iconName by ProjectTable.iconName
    var colorHex by ProjectTable.colorHex
    var priority by ProjectTable.priority
    var syncId by ProjectTable.syncId

    val habits by HabitDAO referrersOn HabitTable.projectId
    val routines by RoutineDAO.Companion referrersOn RoutineTable.projectId
    val tasks by TaskDAO.Companion referrersOn TaskTable.projectId

    var tags by TagDAO.Companion via ProjectTagsTable
}
