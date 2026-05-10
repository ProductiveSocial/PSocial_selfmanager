package com.productivesocial.psocial_selfmanager.database.entities

import com.productivesocial.psocial_selfmanager.database.base.BaseEntity
import com.productivesocial.psocial_selfmanager.database.base.BaseEntityClass
import com.productivesocial.psocial_selfmanager.database.base.BaseIdTable
import com.productivesocial.psocial_selfmanager.model.responses.TagResponse
import org.jetbrains.exposed.v1.core.dao.id.EntityID

object TagTable : BaseIdTable("tags") {
    val userId = reference("user_id", UserTable.id)
    val name = varchar("name", 50)

    init {
        uniqueIndex(userId, name)
    }
}

class TagDAO(id: EntityID<Long>) : BaseEntity(id, TagTable) {
    companion object : BaseEntityClass<TagDAO>(TagTable, TagDAO::class.java)

    var userId by TagTable.userId
    var name by TagTable.name

    fun response() =
        TagResponse(
            id = id.value,
            name = name
        )
}
