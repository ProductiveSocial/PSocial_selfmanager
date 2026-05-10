package com.productivesocial.psocial_selfmanager.database.entities

import org.jetbrains.exposed.v1.core.dao.id.LongIdTable
import org.jetbrains.exposed.v1.datetime.timestamp

/**
 * Records every server-side deletion so that syncing clients can be told
 * which entity IDs they should remove from their local database.
 *
 * A row is written here whenever a project, task, habit, or routine is deleted
 * (including cascaded deletes). Rows are only meaningful to clients that were
 * last synced *before* [deletedAt], so old rows can be pruned periodically.
 */
object SyncTombstoneTable : LongIdTable("sync_tombstones") {
    val userId = reference("user_id", UserTable.id)
    val entityType = varchar("entity_type", 20) // "project" | "task" | "habit" | "routine"
    val entityId = long("entity_id")
    val deletedAt = timestamp("deleted_at")
}
