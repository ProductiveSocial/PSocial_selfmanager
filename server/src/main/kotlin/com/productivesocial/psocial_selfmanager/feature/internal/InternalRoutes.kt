package com.productivesocial.psocial_selfmanager.feature.internal

import com.productivesocial.psocial_selfmanager.config.DotEnvConfig
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.ApplicationCall
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post

/**
 * Routes exclusively for service-to-service communication.
 * All requests must carry the X-Internal-Key header matching INTERNAL_API_KEY.
 */
fun Route.internalRoutes(internalService: InternalService) {

    fun ApplicationCall.checkKey(): Boolean {
        val key = request.headers["X-Internal-Key"]
        return !key.isNullOrBlank() && key == DotEnvConfig.internalApiKey
    }

    post("/internal/time-log") {
        if (!call.checkKey()) return@post call.respond(HttpStatusCode.Unauthorized)
        val request = call.receive<TimeLogRequest>()
        internalService.logTime(request)
        call.respond(HttpStatusCode.NoContent)
    }

    get("/internal/users/{userId}/tasks") {
        if (!call.checkKey()) return@get call.respond(HttpStatusCode.Unauthorized)
        val userId = call.parameters["userId"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid userId"))
        call.respond(HttpStatusCode.OK, internalService.getUserTasks(userId))
    }

    get("/internal/users/{userId}/habits") {
        if (!call.checkKey()) return@get call.respond(HttpStatusCode.Unauthorized)
        val userId = call.parameters["userId"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid userId"))
        call.respond(HttpStatusCode.OK, internalService.getUserHabits(userId))
    }

    get("/internal/users/{userId}/routines") {
        if (!call.checkKey()) return@get call.respond(HttpStatusCode.Unauthorized)
        val userId = call.parameters["userId"]?.toLongOrNull()
            ?: return@get call.respond(HttpStatusCode.BadRequest, mapOf("message" to "Invalid userId"))
        call.respond(HttpStatusCode.OK, internalService.getUserRoutines(userId))
    }
}
