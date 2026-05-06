package com.productivesocial.com.productivesocial.database.entities

import com.productivesocial.com.productivesocial.database.base.BaseEntity
import com.productivesocial.com.productivesocial.database.base.BaseEntityClass
import com.productivesocial.com.productivesocial.database.base.BaseIdTable
import org.jetbrains.exposed.v1.core.dao.id.EntityID

object RoutineStepsTable : BaseIdTable("routine_steps") {
    val name = varchar("name", 255)
    val autoStart = bool("auto_start").default(false)
    val duration = integer("duration") // Stored in seconds or minutes
    val description = text("description").nullable()
    val completed = bool("completed").default(false)

    // Reference to the parent Routine table
    val routineId = reference("routine_id", RoutineTable)
}

class RoutineStepDAO(id: EntityID<Long>) : BaseEntity(id, RoutineStepsTable) {
    companion object : BaseEntityClass<RoutineStepDAO>(RoutineStepsTable, RoutineStepDAO::class.java)

    var name by RoutineStepsTable.name
    var autoStart by RoutineStepsTable.autoStart
    var duration by RoutineStepsTable.duration
    var description by RoutineStepsTable.description
    var completed by RoutineStepsTable.completed
    var routine by RoutineDAO referencedOn RoutineStepsTable.routineId
}