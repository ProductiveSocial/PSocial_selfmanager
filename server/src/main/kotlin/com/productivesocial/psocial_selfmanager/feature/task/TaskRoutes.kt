package com.productivesocial.psocial_selfmanager.feature.task

import com.productivesocial.psocial_selfmanager.model.requests.JwtTokenRequest
import com.productivesocial.psocial_selfmanager.model.requests.TaskRequest
import com.productivesocial.psocial_selfmanager.model.requests.UpdateTaskRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.taskRoutes(taskService: TaskService) {

    get("/tasks") {
        val userId = call.principal<JwtTokenRequest>()?.userId
            ?: return@get call.respond(HttpStatusCode.Unauthorized)
        call.respond(taskService.getTasksByUserId(userId))
    }

    route("/task") {
        post {
            val userId = call.principal<JwtTokenRequest>()?.userId
                ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val request = call.receive<TaskRequest>()
            call.respond(HttpStatusCode.Created, taskService.addTask(userId, request))
        }

        route("/{id}") {
            get {
                val userId = call.principal<JwtTokenRequest>()?.userId
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                val task = taskService.getTaskById(userId, id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Task not found")
                call.respond(task)
            }

            patch {
                val userId = call.principal<JwtTokenRequest>()?.userId
                    ?: return@patch call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                val request = call.receive<UpdateTaskRequest>()
                call.respond(taskService.updateTask(userId, id, request))
            }

            delete {
                val userId = call.principal<JwtTokenRequest>()?.userId
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                call.respond(taskService.removeTaskById(userId, id))
            }
        }
    }
}
