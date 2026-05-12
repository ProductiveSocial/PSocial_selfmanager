package com.productivesocial.psocial_selfmanager.database.entities

import com.productivesocial.psocial_selfmanager.database.base.BaseEntity
import com.productivesocial.psocial_selfmanager.database.base.BaseEntityClass
import com.productivesocial.psocial_selfmanager.database.base.BaseIdTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.javatime.timestamp
import java.time.Instant

object RefreshTokenTable : BaseIdTable("refresh_token") {
    val userId = reference("user_id", UserTable.id)
    val tokenHash = varchar("token_hash", 255).uniqueIndex()
    val expiresAt = timestamp("expires_at")
    val revokedAt = timestamp("revoked_at").nullable()
}

class RefreshTokenDAO(id: EntityID<Long>) : BaseEntity(id, RefreshTokenTable) {
    companion object : BaseEntityClass<RefreshTokenDAO>(RefreshTokenTable, RefreshTokenDAO::class.java)

    var userId by RefreshTokenTable.userId
    var tokenHash by RefreshTokenTable.tokenHash
    var expiresAt by RefreshTokenTable.expiresAt
    var revokedAt by RefreshTokenTable.revokedAt

    val isExpired: Boolean get() = expiresAt.isBefore(Instant.now())
    val isRevoked: Boolean get() = revokedAt != null
    val isValid: Boolean get() = !isExpired && !isRevoked
}
