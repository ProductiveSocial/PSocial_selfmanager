package com.productivesocial.psocial_selfmanager.plugin

import com.productivesocial.psocial_selfmanager.model.requests.RegistrationRequest
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.requestvalidation.RequestValidation
import io.ktor.server.plugins.requestvalidation.ValidationResult

fun Application.configureRequestValidation() {
    install(RequestValidation) {
        validate<RegistrationRequest> { register ->
            register.validation()
            ValidationResult.Valid
        }
    }
}