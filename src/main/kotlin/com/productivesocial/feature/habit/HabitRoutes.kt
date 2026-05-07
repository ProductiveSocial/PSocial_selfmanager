package com.productivesocial.com.productivesocial.feature.habit

import com.productivesocial.com.productivesocial.model.requests.HabitRequest
import com.productivesocial.com.productivesocial.model.requests.UpdateHabitRequest
import io.ktor.http.*
import io.ktor.server.application.*
import io.ktor.server.request.*
import io.ktor.server.response.*
import io.ktor.server.routing.*

fun Route.habitRoutes(habitService: HabitService) {
    route("/habits") {
        get {
            val userId = call.queryParameters["user_id"]?.toLongOrNull()
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
            call.respond(habitService.getHabitsByUserId(userId))
        }

        post {
            val userId = call.queryParameters["user_id"]?.toLongOrNull()
                ?: return@post call.respond(HttpStatusCode.BadRequest, "Missing user_id")
            val request = call.receive<HabitRequest>()
            call.respond(HttpStatusCode.Created, habitService.addHabit(userId, request))
        }

        route("/{id}") {
            get {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid habit id")
                
                val habit = habitService.getHabitById(userId, id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Habit not found")
                call.respond(habit)
            }

            patch {
                val userId = call.queryParameters["user_id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Missing user_id")
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid habit id")
                
                val request = call.receive<UpdateHabitRequest>()
                call.respond(habitService.updateHabit(userId, id, request))
            }

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
