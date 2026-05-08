package com.productivesocial.feature.project

import com.productivesocial.model.PaginatedResponse
import com.productivesocial.model.requests.ProjectRequest
import com.productivesocial.model.requests.UpdateProjectRequest
import com.productivesocial.model.responses.ProjectResponse

interface ProjectRepository {
    suspend fun getProjectsByUserId(userId: Long): PaginatedResponse<ProjectResponse>
    suspend fun getProjectById(userId: Long, projectId: Long): ProjectResponse?
    suspend fun addProject(userId: Long, project: ProjectRequest): ProjectResponse
    suspend fun updateProject(userId: Long, projectId: Long, project: UpdateProjectRequest): ProjectResponse
    suspend fun deleteProject(userId: Long, projectId: Long): ProjectResponse
}
