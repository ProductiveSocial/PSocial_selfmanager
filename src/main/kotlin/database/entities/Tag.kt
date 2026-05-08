package com.productivesocial.database.entities

import com.productivesocial.database.base.BaseEntity
import com.productivesocial.database.base.BaseEntityClass
import com.productivesocial.database.base.BaseIdTable
import com.productivesocial.model.responses.TagResponse
import org.jetbrains.exposed.v1.core.dao.id.EntityID

object TagTable : BaseIdTable("tagsrostgresql") {
    val name = varchar("name", 50).uniqueIndex()
}

class TagDAO(id: EntityID<Long>) : BaseEntity(id, TagTable) {
    companion object : BaseEntityClass<TagDAO>(TagTable, TagDAO::class.java)

    var name by TagTable.name

    fun response() = TagResponse(
        id = id.value,
        name = name
    )
}
