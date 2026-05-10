package com.productivesocial.psocial_selfmanager.feature.project

import com.productivesocial.psocial_selfmanager.model.requests.ProjectRequest
import com.productivesocial.psocial_selfmanager.model.requests.UpdateProjectRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.projectRoutes(projectService: ProjectService) {
    /**
     * Get all projects for a user.
     * @param user_id The ID of the user whose projects to retrieve.
     */
    get("/projects") {
        val userId = call.request.queryParameters["user_id"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
        call.respond(projectService.getProjectsByUserId(userId))
    }

    route("/project") {
        /**
         * Create a new project.
         * @param user_id The ID of the user creating the project.
         */
        post {
            val userId = call.request.queryParameters["user_id"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing user_id")
            val project = call.receive<ProjectRequest>()
            call.respond(HttpStatusCode.Created, projectService.addProject(userId, project))
        }

        route("/{id}") {
            /**
             * Get a project by its ID.
             * @param id The ID of the project.
             * @param user_id The ID of the owner user.
             */
            get {
                val userId = call.request.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val projectId = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid project id")
                
                val project = projectService.getProjectById(userId, projectId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Project not found")
                call.respond(project)
            }

            /**
             * Update an existing project.
             * @param id The ID of the project to update.
             * @param user_id The ID of the owner user.
             */
            patch {
                val userId = call.request.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val projectId = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid project id")
                
                val request = call.receive<UpdateProjectRequest>()
                call.respond(projectService.updateProject(userId, projectId, request))
            }

            /**
             * Delete a project.
             * @param id The ID of the project to delete.
             * @param user_id The ID of the owner user.
             */
            delete {
                val userId = call.request.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val projectId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid project id")
                
                call.respond(projectService.deleteProject(userId, projectId))
            }
        }
    }
}
