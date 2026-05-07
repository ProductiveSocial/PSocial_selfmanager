package com.productivesocial.com.productivesocial.feature.task

import com.productivesocial.com.productivesocial.model.PaginatedResponse
import com.productivesocial.com.productivesocial.model.requests.TaskRequest
import com.productivesocial.com.productivesocial.model.requests.UpdateTaskRequest
import com.productivesocial.com.productivesocial.model.responses.TaskResponse

interface TaskRepository {
    suspend fun getTasksByUserId(userId: Long): PaginatedResponse<TaskResponse>
    suspend fun getTaskById(userId: Long, taskId: Long): TaskResponse? // Added
    suspend fun removeTaskById(userId: Long, taskId: Long): TaskResponse
    suspend fun addTask(userId: Long, task: TaskRequest): TaskResponse
    suspend fun updateTask(userId: Long, taskId: Long, task: UpdateTaskRequest): TaskResponse // Added
}