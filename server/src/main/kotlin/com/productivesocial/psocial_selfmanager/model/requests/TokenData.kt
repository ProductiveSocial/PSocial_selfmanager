package com.productivesocial.psocial_selfmanager.model.requests

data class TokenData(
    val userId: Long = 0,
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String = "Beaarer",
    val expiresIn: Long = 86400
)
