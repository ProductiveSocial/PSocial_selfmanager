package com.productivesocial.psocial_selfmanager.feature.project

import com.productivesocial.psocial_selfmanager.model.requests.JwtTokenRequest
import com.productivesocial.psocial_selfmanager.model.requests.ProjectRequest
import com.productivesocial.psocial_selfmanager.model.requests.UpdateProjectRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.*
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.delete
import io.ktor.server.routing.get
import io.ktor.server.routing.patch
import io.ktor.server.routing.post
import io.ktor.server.routing.route

fun Route.projectRoutes(projectService: ProjectService) {
    get("/projects") {
        val userId = call.principal<JwtTokenRequest>()?.userId
            ?: return@get call.respond(HttpStatusCode.Unauthorized)
        call.respond(projectService.getProjectsByUserId(userId))
    }

    route("/project") {
        post {
            val userId = call.principal<JwtTokenRequest>()?.userId
                ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val project = call.receive<ProjectRequest>()
            call.respond(HttpStatusCode.Created, projectService.addProject(userId, project))
        }

        route("/{id}") {
            get {
                val userId = call.principal<JwtTokenRequest>()?.userId
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val projectId = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid project id")
                val project = projectService.getProjectById(userId, projectId)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Project not found")
                call.respond(project)
            }

            patch {
                val userId = call.principal<JwtTokenRequest>()?.userId
                    ?: return@patch call.respond(HttpStatusCode.Unauthorized)
                val projectId = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid project id")
                val request = call.receive<UpdateProjectRequest>()
                call.respond(projectService.updateProject(userId, projectId, request))
            }

            delete {
                val userId = call.principal<JwtTokenRequest>()?.userId
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val projectId = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid project id")
                call.respond(projectService.deleteProject(userId, projectId))
            }
        }
    }
}
