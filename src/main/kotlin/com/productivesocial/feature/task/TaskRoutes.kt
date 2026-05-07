package com.productivesocial.com.productivesocial.feature.task

import com.productivesocial.com.productivesocial.model.requests.TaskRequest
import com.productivesocial.com.productivesocial.model.requests.UpdateTaskRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.taskRoutes(taskService: TaskService) {
    route("/tasks") {
        get {
            val userId = call.queryParameters["user_id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
            call.respond(taskService.getTasksByUserId(userId))
        }

        post {
            val userId = call.queryParameters["user_id"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing user_id")
            val request = call.receive<TaskRequest>()
            call.respond(HttpStatusCode.Created, taskService.addTask(userId, request))
        }

        route("/{id}") {
            get {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                
                val task = taskService.getTaskById(userId, id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Task not found")
                call.respond(task)
            }

            patch {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                
                val request = call.receive<UpdateTaskRequest>()
                call.respond(taskService.updateTask(userId, id, request))
            }

            delete {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                
                call.respond(taskService.removeTaskById(userId, id))
            }
        }
    }
}
