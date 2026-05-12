package com.productivesocial.psocial_selfmanager.model.requests

import org.valiktor.functions.isNotNull
import org.valiktor.validate

data class RefreshTokenRequest(val refreshToken: String) {
    fun validate() {
        validate(this) {
            validate(RefreshTokenRequest::refreshToken).isNotNull()
        }
    }
}
