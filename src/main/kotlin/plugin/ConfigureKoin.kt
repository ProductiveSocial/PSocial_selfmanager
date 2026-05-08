package com.productivesocial.plugin

import com.productivesocial.di.serviceModule
import io.ktor.server.application.Application
import io.ktor.server.application.install
import org.koin.core.logger.Level
import org.koin.ktor.plugin.Koin
import org.koin.logger.SLF4JLogger

fun Application.configureKoin() {
    install(Koin) {
        SLF4JLogger(Level.INFO)
        modules(modules = serviceModule)
    }
}