package com.productivesocial.psocial_selfmanager.model.requests

import kotlinx.serialization.Serializable
import org.valiktor.functions.isEmail
import org.valiktor.validate
import org.valiktor.functions.isNotNull

@Serializable
data class RegistrationRequest(
    val email: String,
) {
    fun validation() {
        validate(this) {
            validate(RegistrationRequest::email).isNotNull().isEmail()
        }
    }
}