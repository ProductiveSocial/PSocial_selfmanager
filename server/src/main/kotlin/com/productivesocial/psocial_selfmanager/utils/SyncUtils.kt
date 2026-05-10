package com.productivesocial.psocial_selfmanager.utils

import com.productivesocial.psocial_selfmanager.database.entities.SyncTombstoneTable
import com.productivesocial.psocial_selfmanager.database.entities.UserTable
import kotlin.time.Clock
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.jdbc.insertIgnore

/**
 * Records a tombstone so syncing clients know to delete this entity from their local DB.
 * Uses insertIgnore so duplicate tombstones (e.g., on retry) are silently dropped.
 */
fun writeTombstone(userId: Long, entityType: String, entityId: Long) {
    SyncTombstoneTable.insertIgnore {
        it[SyncTombstoneTable.userId] = EntityID(userId,
            UserTable
        )
        it[SyncTombstoneTable.entityType] = entityType
        it[SyncTombstoneTable.entityId] = entityId
        it[SyncTombstoneTable.deletedAt] = Clock.System.now()
    }
}
