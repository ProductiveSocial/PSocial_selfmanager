package com.productivesocial.com.productivesocial.database.entities

import com.productivesocial.com.productivesocial.constants.Priority
import com.productivesocial.com.productivesocial.database.base.BaseEntity
import com.productivesocial.com.productivesocial.database.base.BaseEntityClass
import com.productivesocial.com.productivesocial.database.base.BaseIdTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

object ProjectTable : BaseIdTable("projects") {
    val name = varchar("name", 80)
    val description = text("description").nullable()
    val iconName = varchar("icon_name", 50)
    val colorHex = varchar("color_hex", 7)
    val priority = enumerationByName("priority", 20, Priority::class)
}

class ProjectDAO(id: EntityID<Long>) : BaseEntity(id, ProjectTable) {
    companion object : BaseEntityClass<ProjectDAO>(ProjectTable, ProjectDAO::class.java)

    var name by ProjectTable.name
    var description by ProjectTable.description
    var iconName by ProjectTable.iconName
    var colorHex by ProjectTable.colorHex
    var priority by ProjectTable.priority
}
