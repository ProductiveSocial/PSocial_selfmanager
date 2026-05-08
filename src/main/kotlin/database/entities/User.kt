package com.productivesocial.database.entities

import com.productivesocial.database.base.BaseEntity
import com.productivesocial.database.base.BaseEntityClass
import com.productivesocial.database.base.BaseIdTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

object UserTable : BaseIdTable("user") {
    val deviceId = varchar("device_id", length = 255).uniqueIndex()
}

class UserDAO(id: EntityID<Long>) : BaseEntity(id, UserTable) {
    companion object : BaseEntityClass<UserDAO>(UserTable, UserDAO::class.java)

    var deviceId by UserTable.deviceId
}
