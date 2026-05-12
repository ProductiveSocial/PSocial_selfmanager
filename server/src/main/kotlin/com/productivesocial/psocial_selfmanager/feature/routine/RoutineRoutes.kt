package com.productivesocial.psocial_selfmanager.feature.routine

import com.productivesocial.psocial_selfmanager.model.requests.JwtTokenRequest
import com.productivesocial.psocial_selfmanager.model.requests.RoutineRequest
import com.productivesocial.psocial_selfmanager.model.requests.UpdateRoutineRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.routineRoutes(routineService: RoutineService) {
    get("/routines") {
        val userId = call.principal<JwtTokenRequest>()?.userId
            ?: return@get call.respond(HttpStatusCode.Unauthorized)
        call.respond(routineService.getRoutinesByUserId(userId))
    }

    route("/routine") {
        post {
            val userId = call.principal<JwtTokenRequest>()?.userId
                ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val request = call.receive<RoutineRequest>()
            call.respond(HttpStatusCode.Created, routineService.addRoutine(userId, request))
        }

        route("/{id}") {
            get {
                val userId = call.principal<JwtTokenRequest>()?.userId
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid routine id")
                val routine = routineService.getRoutineById(userId, id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Routine not found")
                call.respond(routine)
            }

            patch {
                val userId = call.principal<JwtTokenRequest>()?.userId
                    ?: return@patch call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid routine id")
                val request = call.receive<UpdateRoutineRequest>()
                call.respond(routineService.updateRoutine(userId, id, request))
            }

            delete {
                val userId = call.principal<JwtTokenRequest>()?.userId
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid routine id")
                call.respond(routineService.deleteRoutine(userId, id))
            }
        }
    }
}
