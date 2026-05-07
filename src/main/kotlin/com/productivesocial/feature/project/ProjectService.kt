package com.productivesocial.com.productivesocial.feature.project

import com.productivesocial.com.productivesocial.database.entities.*
import com.productivesocial.com.productivesocial.model.PaginatedResponse
import com.productivesocial.com.productivesocial.model.PaginationMetadata
import com.productivesocial.com.productivesocial.model.requests.ProjectRequest
import com.productivesocial.com.productivesocial.model.requests.UpdateProjectRequest
import com.productivesocial.com.productivesocial.model.responses.ProjectResponse
import com.productivesocial.com.productivesocial.utils.query
import com.productivesocial.com.productivesocial.utils.toResponse
import io.ktor.server.plugins.NotFoundException
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq

class ProjectService : ProjectRepository {
    override suspend fun getProjectsByUserId(userId: Long): PaginatedResponse<ProjectResponse> = query {
        val projects = ProjectDAO.find { ProjectTable.userId eq userId }.map { it.toResponse() }
        PaginatedResponse(
            data = projects,
            metadata = PaginationMetadata(
                totalItems = projects.size,
                currentPage = 1,
                itemsPerPage = projects.size.coerceAtLeast(1),
                totalPages = 1
            )
        )
    }

    override suspend fun getProjectById(userId: Long, projectId: Long): ProjectResponse? = query {
        ProjectDAO.find { (ProjectTable.userId eq userId) and (ProjectTable.id eq projectId) }
            .singleOrNull()?.toResponse()
    }

    override suspend fun addProject(userId: Long, project: ProjectRequest): ProjectResponse = query {
        UserDAO.findById(userId) ?: throw NotFoundException("User not found")

        ProjectDAO.new {
            this.userId = EntityID(userId, UserTable)
            this.name = project.name
            this.description = project.description
            this.iconName = project.iconName
            this.colorHex = project.colorHex
            this.priority = project.priority
        }.toResponse()
    }

    override suspend fun updateProject(userId: Long, projectId: Long, project: UpdateProjectRequest): ProjectResponse = query {
        val existingProject = ProjectDAO.find { (ProjectTable.userId eq userId) and (ProjectTable.id eq projectId) }
            .singleOrNull() ?: throw NotFoundException("Project not found")

        existingProject.apply {
            project.name?.let { this.name = it }
            project.description?.let { this.description = it }
            project.iconName?.let { this.iconName = it }
            project.colorHex?.let { this.colorHex = it }
            project.priority?.let { this.priority = it }
        }.toResponse()
    }

    override suspend fun deleteProject(userId: Long, projectId: Long): ProjectResponse = query {
        val project = ProjectDAO.find { (ProjectTable.userId eq userId) and (ProjectTable.id eq projectId) }
            .singleOrNull() ?: throw NotFoundException("Project not found")

        val response = project.toResponse()
        
        // Tasks, Habits, and Routines should probably be handled (deleted or unassigned)
        // For now, assuming CASCADE in DB or manual deletion if needed.
        project.tasks.forEach { task ->
            task.subtasks.forEach { it.delete() }
            task.times.forEach { it.delete() }
            task.delete()
        }
        project.habits.forEach { habit ->
            habit.subtasks.forEach { it.delete() }
            habit.times.forEach { it.delete() }
            habit.reminderTimes.forEach { it.delete() }
            habit.delete()
        }
        project.routines.forEach { routine ->
            routine.steps.forEach { it.delete() }
            routine.times.forEach { it.delete() }
            routine.reminderTimes.forEach { it.delete() }
            routine.delete()
        }
        
        project.delete()
        response
    }
}
