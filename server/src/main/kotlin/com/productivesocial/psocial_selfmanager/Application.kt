package com.productivesocial.psocial_selfmanager

import com.productivesocial.psocial_selfmanager.config.DotEnvConfig
import com.productivesocial.psocial_selfmanager.plugin.configureBasic
import com.productivesocial.psocial_selfmanager.plugin.configureKoin
import com.productivesocial.psocial_selfmanager.plugin.configureRateLimit
import com.productivesocial.psocial_selfmanager.plugin.configureRoute
import com.productivesocial.psocial_selfmanager.plugin.configureStatusPage
import com.productivesocial.psocial_selfmanager.plugin.configureSwagger
import com.productivesocial.psocial_selfmanager.database.UserRegistry
import com.productivesocial.psocial_selfmanager.database.configureDatabase
import com.productivesocial.psocial_selfmanager.feature.auth.JwtConfig
import com.productivesocial.psocial_selfmanager.plugin.configureAuth
import com.productivesocial.psocial_selfmanager.plugin.configureRequestValidation
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    val port = DotEnvConfig.serverPort
    val host = DotEnvConfig.serverHost
    JwtConfig.init()
    UserRegistry.init()
    embeddedServer(factory = Netty, port = port, host = host) {
        configureAll()
    }.start(wait = true)
}

fun Application.configureAll() {
    configureDatabase()
    configureBasic()
    configureKoin()
    configureRequestValidation()
    configureAuth()
    configureRateLimit()
    configureSwagger()
    configureStatusPage()
    configureRoute()
}
