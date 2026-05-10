package com.productivesocial.psocial_selfmanager.feature.user

import com.productivesocial.psocial_selfmanager.model.requests.UserRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.userRoutes(userService: UserService) {
    /**
     * Register or retrieve a user by device ID.
     * If the device is new, a user and a Default project are created.
     * If the device already exists, the existing user is returned.
     */
    post("/register") {
        val request = call.receive<UserRequest>()
        call.respond(HttpStatusCode.OK, userService.registerUser(request.deviceId))
    }
}
