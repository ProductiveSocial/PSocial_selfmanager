package com.productivesocial.database.entities

import com.productivesocial.database.base.BaseEntity
import com.productivesocial.database.base.BaseEntityClass
import com.productivesocial.database.base.BaseIdTable
import com.productivesocial.model.responses.SubtaskResponse
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.datetime.timestamp

object SubtaskTable : BaseIdTable("subtasks") {
    val name = varchar("name", 255)
    val completed = bool("completed").default(false)
    val taskId = reference("task_id", TaskTable)
}

object SubtaskCompletionLogTable : BaseIdTable("subtask_completion_log") {
    val subtaskId = reference("subtask_id", SubtaskTable)
    val completedAt = timestamp("completed_at")
    val taskCompletionLogId = reference("task_completion_log_id", TaskCompletionLogTable).nullable()
}

class SubtaskDAO(id: EntityID<Long>) : BaseEntity(id, SubtaskTable) {
    companion object : BaseEntityClass<SubtaskDAO>(SubtaskTable, SubtaskDAO::class.java)

    var name by SubtaskTable.name
    var completed by SubtaskTable.completed
    var task by TaskDAO.Companion referencedOn SubtaskTable.taskId

    val completionLogs by SubtaskCompletionLogDAO referrersOn SubtaskCompletionLogTable.subtaskId

    fun response() = SubtaskResponse(
        id = id.value,
        name = name,
        completed = completed
    )
}

class SubtaskCompletionLogDAO(id: EntityID<Long>) : BaseEntity(id, SubtaskCompletionLogTable) {
    companion object : BaseEntityClass<SubtaskCompletionLogDAO>(SubtaskCompletionLogTable, SubtaskCompletionLogDAO::class.java)
    var subtask by SubtaskDAO referencedOn SubtaskCompletionLogTable.subtaskId
    var completedAt by SubtaskCompletionLogTable.completedAt
    var taskCompletionLog by TaskCompletionLogDAO optionalReferencedOn SubtaskCompletionLogTable.taskCompletionLogId
}
