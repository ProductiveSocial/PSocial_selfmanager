package com.productivesocial.database

import com.productivesocial.config.DotEnvConfig
import com.productivesocial.database.entities.HabitCompletionLogTable
import com.productivesocial.database.entities.HabitReminderTimesTable
import com.productivesocial.database.entities.HabitSubtaskCompletionLogTable
import com.productivesocial.database.entities.HabitSubtasksTable
import com.productivesocial.database.entities.HabitTable
import com.productivesocial.database.entities.HabitTagsTable
import com.productivesocial.database.entities.HabitTimesTable
import com.productivesocial.database.entities.ProjectTable
import com.productivesocial.database.entities.ProjectTagsTable
import com.productivesocial.database.entities.RoutineCompletionLogTable
import com.productivesocial.database.entities.RoutineReminderTimesTable
import com.productivesocial.database.entities.RoutineStepCompletionLogTable
import com.productivesocial.database.entities.RoutineStepsTable
import com.productivesocial.database.entities.SyncTombstoneTable
import com.productivesocial.database.entities.RoutineTable
import com.productivesocial.database.entities.RoutineTagsTable
import com.productivesocial.database.entities.RoutineTimesTable
import com.productivesocial.database.entities.SubtaskCompletionLogTable
import com.productivesocial.database.entities.SubtaskTable
import com.productivesocial.database.entities.TagTable
import com.productivesocial.database.entities.TaskCompletionLogTable
import com.productivesocial.database.entities.TaskTable
import com.productivesocial.database.entities.TaskTags
import com.productivesocial.database.entities.TaskTimesTable
import com.productivesocial.database.entities.UserTable
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
            // Core
            UserTable,
            TagTable,
            // Projects
            ProjectTable,
            ProjectTagsTable,
            // Tasks
            TaskTable,
            TaskTags,
            TaskTimesTable,
            TaskCompletionLogTable,
            SubtaskTable,
            SubtaskCompletionLogTable,
            // Habits
            HabitTable,
            HabitTagsTable,
            HabitTimesTable,
            HabitReminderTimesTable,
            HabitSubtasksTable,
            HabitCompletionLogTable,
            HabitSubtaskCompletionLogTable,
            // Routines
            RoutineTable,
            RoutineTagsTable,
            RoutineTimesTable,
            RoutineReminderTimesTable,
            RoutineStepsTable,
            RoutineCompletionLogTable,
            RoutineStepCompletionLogTable,
            // Sync
            SyncTombstoneTable,
        )
    }
}

private fun initDatabase() {
    val config = HikariConfig().apply {
        driverClassName = "org.postgresql.Driver"
        jdbcUrl = "jdbc:postgresql://${DotEnvConfig.dbHost}:${DotEnvConfig.dbPort}/${DotEnvConfig.dbName}"
        username = DotEnvConfig.dbUser
    }

    HikariDataSource(config).also { dataSource ->
//        runFlyway(dataSource)
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
