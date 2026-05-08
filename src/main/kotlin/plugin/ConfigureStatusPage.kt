package com.productivesocial.plugin

import com.productivesocial.model.ApiError
import com.productivesocial.utils.AppException
import com.productivesocial.utils.InvalidEnumValueException
import com.productivesocial.utils.MissingParameterException
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.MissingRequestParameterException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond

/**
 * Global exception handler - Industry-standard (Stripe/GitHub/OpenAI)
 *
 * Success: Return data directly (HTTP status = source of truth)
 * Error: Return APIError { message, errors? }
 */
fun Application.configureStatusPage() {
    install(StatusPages) {
        exception<Throwable> { call, error ->
            when (error) {
                is InvalidEnumValueException -> {
                    call.application.environment.log.warn("Invalid enum: ${error.invalidValue} for ${error.enumName}")
                    call.respond(status = error.code, message = ApiError(
                        message = error.message ?: "Invalid value"
                    )
                    )
                }
                is MissingParameterException -> {
                    call.respond(status = error.code, message = ApiError(
                        message = error.message ?: "Missing Parameter"
                    )
                    )
                }
                is AppException -> {
                    call.application.environment.log.warn("${error::class.simpleName}: ${error.message}")
                    call.respond(error.code, ApiError(error.message ?: "Unknown error"))
                }

                is MissingRequestParameterException ->
                    call.respond(HttpStatusCode.BadRequest,
                        ApiError("Missing parameter: ${error.parameterName}")
                    )

                is NumberFormatException ->
                    call.respond(HttpStatusCode.BadRequest, ApiError("Invalid numeric value"))

                is IllegalArgumentException ->
                    call.respond(HttpStatusCode.BadRequest,
                        ApiError(error.message ?: "Invalid argument")
                    )

                else -> {
                    call.application.environment.log.error("Unhandled: ${error::class.simpleName}", error)
                    call.respond(HttpStatusCode.InternalServerError,
                        ApiError("Internal server error")
                    )
                }
            }
        }

        status(HttpStatusCode.Unauthorized) { call, _ ->
            call.respond(HttpStatusCode.Unauthorized, message = ApiError(message = "Authentication required"))
        }

        status(HttpStatusCode.NotFound) { call, _ ->
            call.respond(HttpStatusCode.NotFound, ApiError("Resource not found"))
        }

        status(HttpStatusCode.MethodNotAllowed) { call, _ ->
            call.respond(HttpStatusCode.MethodNotAllowed, ApiError("Method not allowed"))
        }

    }
}