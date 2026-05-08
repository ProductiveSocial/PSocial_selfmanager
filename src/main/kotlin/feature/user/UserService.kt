package com.productivesocial.feature.user

import com.productivesocial.constants.Priority
import com.productivesocial.database.entities.ProjectDAO
import com.productivesocial.database.entities.ProjectTable
import com.productivesocial.database.entities.UserDAO
import com.productivesocial.database.entities.UserTable
import com.productivesocial.model.responses.UserResponse
import com.productivesocial.utils.query
import org.jetbrains.exposed.v1.core.and
import org.jetbrains.exposed.v1.core.dao.id.EntityID
import org.jetbrains.exposed.v1.core.eq

class UserService : UserRepository {
    override suspend fun registerUser(deviceId: String): UserResponse = query {
        val existingUser = UserDAO.Companion.find { UserTable.deviceId eq deviceId }.singleOrNull()
        if (existingUser != null) {
            val defaultProject = ProjectDAO.Companion
                .find { (ProjectTable.userId eq existingUser.id.value) and (ProjectTable.name eq DEFAULT_PROJECT_NAME) }
                .firstOrNull()
                ?: createDefaultProject(existingUser)
            return@query UserResponse(existingUser.id.value, existingUser.deviceId, defaultProject.id.value)
        }

        val newUser = UserDAO.Companion.new { this.deviceId = deviceId }
        val defaultProject = createDefaultProject(newUser)
        UserResponse(newUser.id.value, newUser.deviceId, defaultProject.id.value)
    }

    private fun createDefaultProject(user: UserDAO): ProjectDAO =
        ProjectDAO.Companion.new {
            this.userId = EntityID(user.id.value, UserTable)
            this.name = DEFAULT_PROJECT_NAME
            this.description = null
            this.iconName = "folder"
            this.colorHex = "#6B7280"
            this.priority = Priority.None
        }

    companion object {
        const val DEFAULT_PROJECT_NAME = "Default"
    }
}
