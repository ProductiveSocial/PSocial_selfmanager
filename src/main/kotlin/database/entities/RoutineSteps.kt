package com.productivesocial.database.entities

import com.productivesocial.database.base.BaseEntity
import com.productivesocial.database.base.BaseEntityClass
import com.productivesocial.database.base.BaseIdTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.datetime.timestamp

object RoutineStepsTable : BaseIdTable("routine_steps") {
    val routineId = reference("routine_id", RoutineTable.id)
    val name = varchar("name", 255)
    val autoStart = bool("auto_start").default(false)
    val duration = integer("duration")
    val description = text("description").nullable()
    val completed = bool("completed").default(false)
    val position = integer("position")

    init {
        uniqueIndex(routineId, position)
    }
}

object RoutineStepCompletionLogTable : BaseIdTable("routine_step_completion_log") {
    val routineStepId = reference("routine_step_id", RoutineStepsTable)
    val completedAt = timestamp("completed_at")
    val routineCompletionLogId = reference("routine_completion_log_id", RoutineCompletionLogTable).nullable()
}

class RoutineStepCompletionLogDAO(id: EntityID<Long>) : BaseEntity(id, RoutineStepCompletionLogTable) {
    companion object : BaseEntityClass<RoutineStepCompletionLogDAO>(RoutineStepCompletionLogTable, RoutineStepCompletionLogDAO::class.java)
    var routineStep by RoutineStepDAO referencedOn RoutineStepCompletionLogTable.routineStepId
    var completedAt by RoutineStepCompletionLogTable.completedAt
    var routineCompletionLog by RoutineCompletionLogDAO optionalReferencedOn RoutineStepCompletionLogTable.routineCompletionLogId
}

class RoutineStepDAO(id: EntityID<Long>) : BaseEntity(id, RoutineStepsTable) {
    companion object : BaseEntityClass<RoutineStepDAO>(RoutineStepsTable, RoutineStepDAO::class.java)

    var routine by RoutineDAO referencedOn RoutineStepsTable.routineId
    var name by RoutineStepsTable.name
    var autoStart by RoutineStepsTable.autoStart
    var duration by RoutineStepsTable.duration
    var description by RoutineStepsTable.description
    var completed by RoutineStepsTable.completed
    var position by RoutineStepsTable.position

    val completionLogs by RoutineStepCompletionLogDAO referrersOn RoutineStepCompletionLogTable.routineStepId
}
