package com.productivesocial.psocial_selfmanager.utils

import com.productivesocial.psocial_selfmanager.constants.Messages
import io.ktor.http.HttpStatusCode

open class AppException(
    message: String,
    val code: HttpStatusCode = HttpStatusCode.BadRequest
) : Exception(message)

// --- 400 Bad Request ----------------------------------
class ValidationException(messager: String) : AppException(messager, HttpStatusCode.BadRequest)

class InvalidEnumValueException(
    message: String,
    val enumName: String,
    val invalidValue: String
) : AppException(message, HttpStatusCode.BadRequest)

class MissingParameterException(parameterName: String) :
    AppException(Messages.Errors.MISSING_PARAMETER.format(parameterName), HttpStatusCode.BadRequest)

// --- 401 Unauthorized ----------------------------------
class UnauthorizedException(message: String = Messages.Errors.UNAUTHORIZED) :
    AppException(message, HttpStatusCode.Unauthorized)


// --- 403 Forbidden ----------------------------------
class ForbiddenException(message: String = Messages.Errors.FORBIDDEN) :
    AppException(message, HttpStatusCode.Forbidden)

// --- 404 Not Found ----------------------------------
class NotFoundException(message: String = Messages.Errors.NOT_FOUND) :
    AppException(message, HttpStatusCode.NotFound)

// --- 409 Conflict ----------------------------------
class ConflictException(message: String) : AppException(message, HttpStatusCode.Conflict)

// --- 429 Too many requests ----------------------------------
class RateLimitExceededException(message: String = "Too many requests") :
    AppException(message, HttpStatusCode.TooManyRequests)

// --- 500 Internal Server Error ----------------------------------
class InternalServerErrorException(message: String = Messages.Errors.INTERNAL) :
    AppException(message, HttpStatusCode.InternalServerError)

class DatabaseException(message: String) : AppException(message, HttpStatusCode.InternalServerError)