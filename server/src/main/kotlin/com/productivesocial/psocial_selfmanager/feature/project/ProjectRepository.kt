package com.productivesocial.psocial_selfmanager.feature.project

import com.productivesocial.psocial_selfmanager.model.PaginatedResponse
import com.productivesocial.psocial_selfmanager.model.requests.ProjectRequest
import com.productivesocial.psocial_selfmanager.model.requests.UpdateProjectRequest
import com.productivesocial.psocial_selfmanager.model.responses.ProjectResponse


interface ProjectRepository {
    suspend fun getProjectsByUserId(userId: Long): PaginatedResponse<ProjectResponse>
    suspend fun getProjectById(userId: Long, projectId: Long): ProjectResponse?
    suspend fun addProject(userId: Long, project: ProjectRequest): ProjectResponse
    suspend fun updateProject(userId: Long, projectId: Long, project: UpdateProjectRequest): ProjectResponse
    suspend fun deleteProject(userId: Long, projectId: Long): ProjectResponse
}
