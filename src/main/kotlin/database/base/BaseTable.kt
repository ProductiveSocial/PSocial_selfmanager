package com.productivesocial.database.base

import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.dao.Entity
import org.jetbrains.exposed.v1.dao.EntityChangeType
import org.jetbrains.exposed.v1.dao.EntityClass
import org.jetbrains.exposed.v1.dao.EntityHook
import org.jetbrains.exposed.v1.dao.toEntity
import org.jetbrains.exposed.v1.javatime.datetime
import java.time.LocalDateTime
import java.time.ZoneOffset

abstract class BaseIdTable (name: String): LongIdTable(name) {
    val createdAt = datetime("created_at").clientDefault { currentUtc() }
    val updatedAt = datetime("updated_at").nullable()
}

abstract class BaseEntity(id: EntityID<Long>, table: BaseIdTable) : Entity<Long>(id) {
    val createdAt by table.createdAt
    var updatedAt by table.updatedAt
}

abstract class BaseEntityClass<E : BaseEntity>(table: BaseIdTable, entityType: Class<E>) : EntityClass<Long, E>(table, entityType) {
    init {
        EntityHook.subscribe { action ->
            if (action.changeType == EntityChangeType.Updated) {
                try {
                    action.toEntity(this)?.updatedAt = currentUtc()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}

// generating utc time
fun currentUtc(): LocalDateTime = LocalDateTime.now(ZoneOffset.UTC)

