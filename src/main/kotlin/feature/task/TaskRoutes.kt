package com.productivesocial.feature.task

import com.productivesocial.model.requests.TaskRequest
import com.productivesocial.model.requests.UpdateTaskRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.taskRoutes(taskService: TaskService) {
    /**
     * Get all tasks for a user.
     * @param user_id The ID of the user whose tasks to retrieve.
     */
    get("/tasks") {
        val userId = call.queryParameters["user_id"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
        call.respond(taskService.getTasksByUserId(userId))
    }

    route("/task") {
        /**
         * Create a new task.
         * @param user_id The ID of the user creating the task.
         */
        post {
            val userId = call.queryParameters["user_id"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing user_id")
            val request = call.receive<TaskRequest>()
            call.respond(HttpStatusCode.Created, taskService.addTask(userId, request))
        }

        route("/{id}") {
            /**
             * Get a task by its ID.
             * @param id The ID of the task.
             * @param user_id The ID of the owner user.
             */
            get {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                
                val task = taskService.getTaskById(userId, id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Task not found")
                call.respond(task)
            }

            /**
             * Update an existing task.
             * @param id The ID of the task to update.
             * @param user_id The ID of the owner user.
             */
            patch {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid task id")
                
                val request = call.receive<UpdateTaskRequest>()
                call.respond(taskService.updateTask(userId, id, request))
            }

            /**
             * Delete a task.
             * @param id The ID of the task to delete.
             * @param user_id The ID of the owner user.
             */
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
