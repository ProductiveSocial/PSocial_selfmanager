package com.productivesocial.feature.routine

import com.productivesocial.model.requests.RoutineRequest
import com.productivesocial.model.requests.UpdateRoutineRequest
import io.ktor.http.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.routineRoutes(routineService: RoutineService) {
    /**
     * Get all routines for a user.
     * @param user_id The ID of the user whose routines to retrieve.
     */
    get("/routines") {
        val userId = call.queryParameters["user_id"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
        call.respond(routineService.getRoutinesByUserId(userId))
    }

    route("/routine") {
        /**
         * Create a new routine.
         * @param user_id The ID of the user creating the routine.
         */
        post {
            val userId = call.queryParameters["user_id"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing user_id")
            val request = call.receive<RoutineRequest>()
            call.respond(HttpStatusCode.Created, routineService.addRoutine(userId, request))
        }

        route("/{id}") {
            /**
             * Get a routine by its ID.
             * @param id The ID of the routine.
             * @param user_id The ID of the owner user.
             */
            get {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid routine id")
                
                val routine = routineService.getRoutineById(userId, id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Routine not found")
                call.respond(routine)
            }

            /**
             * Update an existing routine.
             * @param id The ID of the routine to update.
             * @param user_id The ID of the owner user.
             */
            patch {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid routine id")
                
                val request = call.receive<UpdateRoutineRequest>()
                call.respond(routineService.updateRoutine(userId, id, request))
            }

            /**
             * Delete a routine.
             * @param id The ID of the routine to delete.
             * @param user_id The ID of the owner user.
             */
            delete {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid routine id")
                
                call.respond(routineService.deleteRoutine(userId, id))
            }
        }
    }
}
