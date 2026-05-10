package com.productivesocial.psocial_selfmanager.plugin

import com.google.gson.JsonPrimitive
import com.google.gson.JsonSerializer
import com.productivesocial.psocial_selfmanager.config.DotEnvConfig
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.Url
import io.ktor.serialization.gson.gson
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.plugins.calllogging.processingTimeMillis
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.server.plugins.origin
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import org.slf4j.event.Level
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun Application.configureBasic() {
    install(CORS) {
        val allowedOrigins = DotEnvConfig.allowedOrigins.split(",")

        // Allow all origins if "*" is in the list
        if (allowedOrigins.any { it.trim() == "*" }) {
            anyHost()
        } else {
            allowedOrigins.forEach { origin ->
                val trimmed = origin.trim()
                // Parse the URL to extract host and scheme
                val url = Url(trimmed)
                allowHost(
                    host = url.host,
                    schemes = listOf(url.protocol.name)
                )
            }
        }

        listOf(
            HttpMethod.Put,
            HttpMethod.Post,
            HttpMethod.Delete,
            HttpMethod.Patch,
            HttpMethod.Options
        ).forEach { allowMethod(method = it) }
        allowHeader(header = HttpHeaders.ContentType)
        allowHeader(header = HttpHeaders.Authorization)
        allowHeader(header = "X-Requested-With")
    }

    install(plugin = ContentNegotiation) {
        gson {
            setPrettyPrinting()
            registerTypeAdapter(
                LocalDateTime::class.java,
                JsonSerializer<LocalDateTime> { localDateTime, _, _ ->
                    JsonPrimitive(localDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME))
                }
            )
        }
    }

    install(CallLogging) {
        level = Level.INFO
        filter { call -> call.request.path().startsWith("/") }
        format { call ->
            val status = call.response.status()
            val httpMethod = call.request.httpMethod.value
            val userAgent = call.request.headers["User-Agent"]
            val path = call.request.path()
            val queryParams =
                call.request.queryParameters
                    .entries()
                    .joinToString(", ") { "${it.key}=${it.value}" }
            val duration = call.processingTimeMillis()
            val remoteHost = call.request.origin.remoteHost
            val coloredStatus =
                when {
                    status == null -> "\u001B[33mUNKNOWN\u001B[0m"
                    status.value < 300 -> "\u001B[32m$status\u001B[0m"
                    status.value < 400 -> "\u001B[33m$status\u001B[0m"
                    else -> "\u001B[31m$status\u001B[0m"
                }
            val coloredMethod = "\u001B[36m$httpMethod\u001B[0m"
            """
            |
            |------------------------ Request Details ------------------------
            |Status: $coloredStatus
            |Method: $coloredMethod
            |Path: $path
            |Query Params: $queryParams
            |Remote Host: $remoteHost
            |User Agent: $userAgent
            |Duration: ${duration}ms
            |------------------------------------------------------------------
            |
      """.trimMargin()
        }
    }
}