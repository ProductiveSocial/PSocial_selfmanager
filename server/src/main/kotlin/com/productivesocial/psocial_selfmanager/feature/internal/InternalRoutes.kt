package com.productivesocial.psocial_selfmanager.feature.internal

import com.productivesocial.psocial_selfmanager.config.DotEnvConfig
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

/**
 * Routes exclusively for service-to-service communication.
 * All requests must carry the X-Internal-Key header matching INTERNAL_API_KEY.
 * These routes are NOT exposed to the public internet — sit behind a private network
 * or at minimum a firewall rule blocking external access to /internal/.
**/
fun Route.internalRoutes(internalService: InternalService) {
    post("/internal/time-log") {
        val key = call.request.headers["X-Internal-Key"]
        if (key.isNullOrBlank() || key != DotEnvConfig.internalApiKey) {
            return@post call.respond(HttpStatusCode.Unauthorized)
        }
        val request = call.receive<TimeLogRequest>()
        internalService.logTime(request)
        call.respond(HttpStatusCode.NoContent)
    }
}