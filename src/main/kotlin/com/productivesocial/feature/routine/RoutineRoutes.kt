package com.productivesocial.com.productivesocial.feature.routine

import com.productivesocial.com.productivesocial.model.requests.RoutineRequest
import com.productivesocial.com.productivesocial.model.requests.UpdateRoutineRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.routineRoutes(routineService: RoutineService) {
    route("/routines") {
        get {
            val userId = call.queryParameters["user_id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
            call.respond(routineService.getRoutinesByUserId(userId))
        }

        post {
            val userId = call.queryParameters["user_id"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing user_id")
            val request = call.receive<RoutineRequest>()
            call.respond(HttpStatusCode.Created, routineService.addRoutine(userId, request))
        }

        route("/{id}") {
            get {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid routine id")
                
                val routine = routineService.getRoutineById(userId, id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Routine not found")
                call.respond(routine)
            }

            patch {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid routine id")
                
                val request = call.receive<UpdateRoutineRequest>()
                call.respond(routineService.updateRoutine(userId, id, request))
            }

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
