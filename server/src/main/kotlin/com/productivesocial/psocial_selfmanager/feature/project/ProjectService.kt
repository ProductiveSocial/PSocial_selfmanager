package com.productivesocial.psocial_selfmanager.feature.project

import com.productivesocial.psocial_selfmanager.utils.writeTombstone
import com.productivesocial.psocial_selfmanager.feature.routine.deleteRoutineCascade
import com.productivesocial.psocial_selfmanager.feature.task.deleteTaskCascade
import com.productivesocial.psocial_selfmanager.model.PaginatedResponse
import com.productivesocial.psocial_selfmanager.model.requests.ProjectRequest
import com.productivesocial.psocial_selfmanager.model.requests.UpdateProjectRequest
import com.productivesocial.psocial_selfmanager.model.responses.ProjectResponse
import com.productivesocial.psocial_selfmanager.utils.toResponse
import com.productivesocial.psocial_selfmanager.database.entities.ProjectDAO
import com.productivesocial.psocial_selfmanager.database.entities.ProjectTable
import com.productivesocial.psocial_selfmanager.database.entities.TagDAO
import com.productivesocial.psocial_selfmanager.database.entities.TagTable
import com.productivesocial.psocial_selfmanager.database.entities.UserDAO
import com.productivesocial.psocial_selfmanager.database.entities.UserTable
import com.productivesocial.psocial_selfmanager.feature.habit.deleteHabitCascade
import com.productivesocial.psocial_selfmanager.model.PaginationMetadata
import com.productivesocial.psocial_selfmanager.utils.query
import io.ktor.server.plugins.NotFoundException
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.SizedCollection

class ProjectService : ProjectRepository {
    override suspend fun getProjectsByUserId(userId: Long): PaginatedResponse<ProjectResponse> =
        query {
            val projects =
                ProjectDAO.Companion.find { ProjectTable.userId eq userId }
                    .map { it.toResponse() }
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

    override suspend fun getProjectById(userId: Long, projectId: Long): ProjectResponse? =
        query {
            ProjectDAO.Companion.find { (ProjectTable.userId eq userId) and (ProjectTable.id eq projectId) }
                .singleOrNull()?.toResponse()
        }

    override suspend fun addProject(userId: Long, project: ProjectRequest): ProjectResponse =
        query {
            UserDAO.Companion.findById(
                userId
            ) ?: throw NotFoundException("User not found")

            val newProject =
                ProjectDAO.Companion.new {
                    this.userId = EntityID(
                        userId,
                        UserTable
                    )
                    this.name = project.name
                    this.description = project.description
                    this.iconName = project.iconName
                    this.colorHex = project.colorHex
                    this.priority = project.priority
                }

            newProject.tags = SizedCollection(project.tags.map { findOrCreateTag(userId, it) })
            newProject.toResponse()
        }

    override suspend fun updateProject(userId: Long, projectId: Long, project: UpdateProjectRequest): ProjectResponse =
        query {
            val existingProject =
                ProjectDAO.Companion.find { (ProjectTable.userId eq userId) and (ProjectTable.id eq projectId) }
                    .singleOrNull() ?: throw NotFoundException("Project not found")

            existingProject.apply {
                project.name?.let { this.name = it }
                project.description?.let { this.description = it }
                project.iconName?.let { this.iconName = it }
                project.colorHex?.let { this.colorHex = it }
                project.priority?.let { this.priority = it }
            }

            project.tags?.let { tags ->
                existingProject.tags = SizedCollection(tags.map { findOrCreateTag(userId, it) })
            }

            existingProject.toResponse()
        }

    override suspend fun deleteProject(userId: Long, projectId: Long): ProjectResponse =
        query {
            val project =
                ProjectDAO.Companion.find { (ProjectTable.userId eq userId) and (ProjectTable.id eq projectId) }
                    .singleOrNull() ?: throw NotFoundException("Project not found")

            val response = project.toResponse()

            writeTombstone(
                userId,
                "project",
                projectId
            )
            project.tasks.forEach {
                deleteTaskCascade(
                    it,
                    recordTombstone = true
                )
            }
            project.habits.forEach {
                deleteHabitCascade(
                    it,
                    recordTombstone = true
                )
            }
            project.routines.forEach {
                deleteRoutineCascade(
                    it,
                    recordTombstone = true
                )
            }
            project.tags = SizedCollection(emptyList())
            project.delete()

            response
        }

    private fun findOrCreateTag(userId: Long, tagName: String): TagDAO =
        TagDAO.Companion.find { (TagTable.userId eq userId) and (TagTable.name eq tagName) }
            .singleOrNull()
            ?: TagDAO.Companion.new {
                this.userId = EntityID(userId,
                    UserTable
                )
                this.name = tagName
            }
}
