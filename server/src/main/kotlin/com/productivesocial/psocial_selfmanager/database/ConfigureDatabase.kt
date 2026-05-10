package com.productivesocial.psocial_selfmanager.database

import com.productivesocial.psocial_selfmanager.config.DotEnvConfig
import com.productivesocial.psocial_selfmanager.database.entities.HabitCompletionLogTable
import com.productivesocial.psocial_selfmanager.database.entities.HabitReminderTimesTable
import com.productivesocial.psocial_selfmanager.database.entities.HabitSubtaskCompletionLogTable
import com.productivesocial.psocial_selfmanager.database.entities.HabitSubtasksTable
import com.productivesocial.psocial_selfmanager.database.entities.HabitTable
import com.productivesocial.psocial_selfmanager.database.entities.HabitTagsTable
import com.productivesocial.psocial_selfmanager.database.entities.HabitTimesTable
import com.productivesocial.psocial_selfmanager.database.entities.ProjectTable
import com.productivesocial.psocial_selfmanager.database.entities.ProjectTagsTable
import com.productivesocial.psocial_selfmanager.database.entities.RoutineCompletionLogTable
import com.productivesocial.psocial_selfmanager.database.entities.RoutineReminderTimesTable
import com.productivesocial.psocial_selfmanager.database.entities.RoutineStepCompletionLogTable
import com.productivesocial.psocial_selfmanager.database.entities.RoutineStepsTable
import com.productivesocial.psocial_selfmanager.database.entities.RoutineTable
import com.productivesocial.psocial_selfmanager.database.entities.RoutineTagsTable
import com.productivesocial.psocial_selfmanager.database.entities.RoutineTimesTable
import com.productivesocial.psocial_selfmanager.database.entities.SubtaskCompletionLogTable
import com.productivesocial.psocial_selfmanager.database.entities.SubtaskTable
import com.productivesocial.psocial_selfmanager.database.entities.SyncTombstoneTable
import com.productivesocial.psocial_selfmanager.database.entities.TagTable
import com.productivesocial.psocial_selfmanager.database.entities.TaskCompletionLogTable
import com.productivesocial.psocial_selfmanager.database.entities.TaskTable
import com.productivesocial.psocial_selfmanager.database.entities.TaskTags
import com.productivesocial.psocial_selfmanager.database.entities.TaskTimesTable
import com.productivesocial.psocial_selfmanager.database.entities.UserTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.v1.core.Slf4jSqlDebugLogger
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.TransactionManager
import org.jetbrains.exposed.v1.jdbc.transactions.transaction

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
        DotEnvConfig.dbPassword?.let { password = it }
    }

    HikariDataSource(config).also { dataSource ->
        Database.connect(dataSource)
    }
}