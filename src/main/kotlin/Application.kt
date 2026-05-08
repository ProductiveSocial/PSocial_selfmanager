package com.productivesocial

import com.productivesocial.config.DotEnvConfig
import com.productivesocial.database.configureDatabase
import com.productivesocial.plugin.configureBasic
import com.productivesocial.plugin.configureKoin
import com.productivesocial.plugin.configureRateLimit
import com.productivesocial.plugin.configureRoute
import com.productivesocial.plugin.configureStatusPage
import com.productivesocial.plugin.configureSwagger
import io.ktor.server.application.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty

fun main(args: Array<String>) {
    val port = DotEnvConfig.serverPort
    val host = DotEnvConfig.serverHost
    embeddedServer(factory = Netty, port = port, host = host) {
        configureAll()
    }.start(wait = true)
}

fun Application.configureAll() {
    configureDatabase()
    configureBasic()
    configureKoin()
    configureRateLimit()
    configureSwagger()
    configureStatusPage()
    configureRoute()
}
