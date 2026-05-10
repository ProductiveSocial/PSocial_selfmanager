package com.productivesocial.psocial_selfmanager.model

import com.productivesocial.psocial_selfmanager.utils.AppException
import io.ktor.http.HttpStatusCode

data class ApiError(
    val message: String,
    val errors: List<FieldError>? = null
)

data class FieldError(
    val field: String,
    val message: String
)

fun AppException.toErrorResponse() : Pair<HttpStatusCode, ApiError> =
    code to ApiError(message ?: "Unkonwn error")