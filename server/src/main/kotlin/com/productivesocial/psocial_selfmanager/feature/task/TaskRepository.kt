package com.productivesocial.psocial_selfmanager.feature.task

import com.productivesocial.psocial_selfmanager.model.PaginatedResponse
import com.productivesocial.psocial_selfmanager.model.requests.TaskRequest
import com.productivesocial.psocial_selfmanager.model.requests.UpdateTaskRequest
import com.productivesocial.psocial_selfmanager.model.responses.TaskResponse

interface TaskRepository {
    suspend fun getTasksByUserId(userId: Long): PaginatedResponse<TaskResponse>
    suspend fun getTaskById(userId: Long, taskId: Long): TaskResponse? // Added
    suspend fun removeTaskById(userId: Long, taskId: Long): TaskResponse
    suspend fun addTask(userId: Long, task: TaskRequest): TaskResponse
    suspend fun updateTask(userId: Long, taskId: Long, task: UpdateTaskRequest): TaskResponse // Added
}