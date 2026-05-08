package com.productivesocial.database.entities

import com.productivesocial.constants.TaskSelectionTypes
import com.productivesocial.database.base.BaseEntity
import com.productivesocial.database.base.BaseEntityClass
import com.productivesocial.database.base.BaseIdTable
import com.productivesocial.model.responses.SubtaskResponse
import org.jetbrains.exposed.v1.core.dao.id.EntityID

object SubtaskTable : BaseIdTable("subtask") {
    val name = varchar("name", 255)
    val completed = bool("completed").default(false)
    val type = enumerationByName("type", 20, TaskSelectionTypes::class)

    // Most subtasks belong to a Task
    val taskId = reference("task_id", TaskTable)
}

class SubtaskDAO(id: EntityID<Long>) : BaseEntity(id, SubtaskTable) {
    companion object : BaseEntityClass<SubtaskDAO>(SubtaskTable, SubtaskDAO::class.java)

    var name by SubtaskTable.name
    var completed by SubtaskTable.completed
    var type by SubtaskTable.type
    var task by TaskDAO.Companion referencedOn SubtaskTable.taskId

    fun response() = SubtaskResponse(
        id = id.value,
        name = name,
        completed = completed,
        type = type
    )
}
