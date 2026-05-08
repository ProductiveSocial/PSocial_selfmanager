package com.productivesocial.feature.habit

import com.productivesocial.model.requests.HabitRequest
import com.productivesocial.model.requests.UpdateHabitRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.habitRoutes(habitService: HabitService) {
    /**
     * Get all habits for a user.
     * @param user_id The ID of the user whose habits to retrieve.
     */
    get("/habits") {
        val userId = call.queryParameters["user_id"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
        call.respond(habitService.getHabitsByUserId(userId))
    }

    route("/habit") {
        /**
         * Create a new habit.
         * @param user_id The ID of the user creating the habit.
         */
        post {
            val userId = call.queryParameters["user_id"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing user_id")
            val request = call.receive<HabitRequest>()
            call.respond(HttpStatusCode.Created, habitService.addHabit(userId, request))
        }

        route("/{id}") {
            /**
             * Get a habit by its ID.
             * @param id The ID of the habit.
             * @param user_id The ID of the owner user.
             */
            get {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid habit id")

                val habit = habitService.getHabitById(userId, id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Habit not found")
                call.respond(habit)
            }

            /**
             * Update an existing habit.
             * @param id The ID of the habit to update.
             * @param user_id The ID of the owner user.
             */
            patch {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid habit id")

                val request = call.receive<UpdateHabitRequest>()
                call.respond(habitService.updateHabit(userId, id, request))
            }

            /**
             * Delete a habit.
             * @param id The ID of the habit to delete.
             * @param user_id The ID of the owner user.
             */
            delete {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid habit id")

                call.respond(habitService.deleteHabit(userId, id))
            }
        }
    }
}
