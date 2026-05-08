package com.productivesocial.plugin

import io.ktor.openapi.OpenApiInfo
import io.ktor.server.application.Application
import io.ktor.server.plugins.openapi.openAPI
import io.ktor.server.plugins.swagger.swaggerUI
import io.ktor.server.routing.routing

fun Application.configureSwagger() {
    routing {
        swaggerUI(path = "swagger") {
            info = OpenApiInfo(
                title = "PSocial SelfManager Ktor API",
                version = "1.0.0",
                description = "PSocial SelfManager Ktor API - Projects, Tasks, Habits, Routines and Pomodoro",
                termsOfService = "TODO()",
                contact= OpenApiInfo.Contact(name = "Ricardo Miambo Junior", email = "ricardo.m.jnr@mail.ru"),
                license = OpenApiInfo.License(name = "MIT")
            )
        }
    }
}