package com.productivesocial.com.productivesocial.database

import com.productivesocial.com.productivesocial.config.DotEnvConfig
import com.productivesocial.com.productivesocial.database.entities.ProjectTable
import com.productivesocial.com.productivesocial.database.entities.SubtaskTable
import com.productivesocial.com.productivesocial.database.entities.TagTable
import com.productivesocial.com.productivesocial.database.entities.TaskTable
import com.productivesocial.com.productivesocial.database.entities.TaskTags
import com.productivesocial.com.productivesocial.database.entities.TaskTimesTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.flywaydb.core.Flyway
import org.jetbrains.exposed.v1.core.Slf4jSqlDebugLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import javax.sql.DataSource

fun configureDatabase() {
    initDatabase()
    transaction {
        TransactionManager.current().addLogger(Slf4jSqlDebugLogger)
        SchemaUtils.create(
            ProjectTable,
            TaskTable,
            SubtaskTable,
            TagTable,
            TaskTags,
            TaskTimesTable,
        )
    }
}

private fun initDatabase() {
    val config = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://${DotEnvConfig.dbHost}:${DotEnvConfig.dbPort}/${DotEnvConfig.dbName}"
        username = DotEnvConfig.dbUser
        password = DotEnvConfig.dbPassword
    }

    HikariDataSource(config).also { dataSource ->
        runFlyway(dataSource)
        Database.connect(dataSource)
    }
}

private fun runFlyway(datasource: DataSource) {
    val flyway = Flyway.configure().dataSource(datasource).load()
    try {
        flyway.info()
        flyway.migrate()
    } catch (e: Exception) {
        throw e
    }
}