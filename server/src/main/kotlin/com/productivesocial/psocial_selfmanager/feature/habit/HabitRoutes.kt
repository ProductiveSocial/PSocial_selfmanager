package com.productivesocial.psocial_selfmanager.feature.habit

import com.productivesocial.psocial_selfmanager.model.requests.HabitRequest
import com.productivesocial.psocial_selfmanager.model.requests.JwtTokenRequest
import com.productivesocial.psocial_selfmanager.model.requests.LogHabitCompletionRequest
import com.productivesocial.psocial_selfmanager.model.requests.UpdateHabitRequest
import io.ktor.http.*
import io.ktor.server.auth.*
import io.ktor.server.request.*
import io.ktor.server.response.respond
import io.ktor.server.routing.*

fun Route.habitRoutes(habitService: HabitService) {
    get("/habits") {
        val userId = call.principal<JwtTokenRequest>()?.userId
            ?: return@get call.respond(HttpStatusCode.Unauthorized)
        call.respond(habitService.getHabitsByUserId(userId))
    }

    route("/habit") {
        post {
            val userId = call.principal<JwtTokenRequest>()?.userId
                ?: return@post call.respond(HttpStatusCode.Unauthorized)
            val request = call.receive<HabitRequest>()
            call.respond(HttpStatusCode.Created, habitService.addHabit(userId, request))
        }

        route("/{id}") {
            get {
                val userId = call.principal<JwtTokenRequest>()?.userId
                    ?: return@get call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid habit id")
                val habit = habitService.getHabitById(userId, id)
                    ?: return@get call.respond(HttpStatusCode.NotFound, "Habit not found")
                call.respond(habit)
            }

            patch {
                val userId = call.principal<JwtTokenRequest>()?.userId
                    ?: return@patch call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@patch call.respond(HttpStatusCode.BadRequest, "Invalid habit id")
                val request = call.receive<UpdateHabitRequest>()
                call.respond(habitService.updateHabit(userId, id, request))
            }

            delete {
                val userId = call.principal<JwtTokenRequest>()?.userId
                    ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                val id = call.parameters["id"]?.toLongOrNull()
                    ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid habit id")
                call.respond(habitService.deleteHabit(userId, id))
            }

            route("/completions") {
                get {
                    val userId = call.principal<JwtTokenRequest>()?.userId
                        ?: return@get call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@get call.respond(HttpStatusCode.BadRequest, "Invalid habit id")
                    call.respond(habitService.getCompletions(userId, id))
                }

                post {
                    val userId = call.principal<JwtTokenRequest>()?.userId
                        ?: return@post call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@post call.respond(HttpStatusCode.BadRequest, "Invalid habit id")
                    val request = call.receive<LogHabitCompletionRequest>()
                    call.respond(HttpStatusCode.Created, habitService.logCompletion(userId, id, request))
                }

                delete("/{completionId}") {
                    val userId = call.principal<JwtTokenRequest>()?.userId
                        ?: return@delete call.respond(HttpStatusCode.Unauthorized)
                    val id = call.parameters["id"]?.toLongOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid habit id")
                    val completionId = call.parameters["completionId"]?.toLongOrNull()
                        ?: return@delete call.respond(HttpStatusCode.BadRequest, "Invalid completion id")
                    habitService.deleteCompletion(userId, id, completionId)
                    call.respond(HttpStatusCode.NoContent)
                }
            }
        }
    }
}
