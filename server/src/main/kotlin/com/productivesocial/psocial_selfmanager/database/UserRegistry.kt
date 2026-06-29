package com.productivesocial.psocial_selfmanager.database

import com.productivesocial.psocial_selfmanager.config.DotEnvConfig
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.sql.Connection

/**
 * Lightweight JDBC connection to psocial_user — the central user registry.
 * Responsible for assigning canonical user IDs by email.
 * All other services use the ID issued here.
 */
object UserRegistry {

    private lateinit var dataSource: HikariDataSource

    fun init() {
        val config = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = "jdbc:postgresql://${DotEnvConfig.userRegistryDbHost}:${DotEnvConfig.userRegistryDbPort}/${DotEnvConfig.userRegistryDbName}"
            username = DotEnvConfig.userRegistryDbUser
            DotEnvConfig.userRegistryDbPassword?.let { password = it }
            maximumPoolSize = 5
            isAutoCommit = true
            poolName = "UserRegistryPool"
            validate()
        }
        dataSource = HikariDataSource(config)
    }

    /**
     * Find an existing user by email, or insert a new one.
     * Returns the canonical user ID from psocial_user.
     */
    suspend fun findOrCreate(email: String): Long = withContext(Dispatchers.IO) {
        dataSource.connection.use { conn ->
            findUser(conn, email) ?: createUser(conn, email)
        }
    }

    private fun findUser(conn: Connection, email: String): Long? {
        val sql = "SELECT id FROM users WHERE email = ?"
        conn.prepareStatement(sql).use { stmt ->
            stmt.setString(1, email)
            val rs = stmt.executeQuery()
            return if (rs.next()) rs.getLong("id") else null
        }
    }

    private fun createUser(conn: Connection, email: String): Long {
        val sql = "INSERT INTO users (email, created_at) VALUES (?, NOW()) ON CONFLICT (email) DO UPDATE SET email = EXCLUDED.email RETURNING id"
        conn.prepareStatement(sql).use { stmt ->
            stmt.setString(1, email)
            val rs = stmt.executeQuery()
            rs.next()
            return rs.getLong("id")
        }
    }
}
