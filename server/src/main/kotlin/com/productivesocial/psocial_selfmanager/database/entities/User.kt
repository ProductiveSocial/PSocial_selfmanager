package com.productivesocial.psocial_selfmanager.database.entities

import com.productivesocial.psocial_selfmanager.database.base.BaseEntity
import com.productivesocial.psocial_selfmanager.database.base.BaseEntityClass
import com.productivesocial.psocial_selfmanager.database.base.BaseIdTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

object UserTable : BaseIdTable("users") {
    val deviceId = varchar("device_id", length = 255).uniqueIndex()
}

class UserDAO(id: EntityID<Long>) : BaseEntity(id, UserTable) {
    companion object : BaseEntityClass<UserDAO>(UserTable, UserDAO::class.java)

    var deviceId by UserTable.deviceId
}
