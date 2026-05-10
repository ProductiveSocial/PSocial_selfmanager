package com.productivesocial.psocial_selfmanager.config

import io.github.cdimascio.dotenv.Dotenv
import io.github.cdimascio.dotenv.dotenv

object DotEnv {

    private val dotenv: Dotenv = dotenv {
        directory = "/Users/rcolejnr/ProductiveSocial/psocial_selfmanager/"
        filename = ".env"
        ignoreIfMissing = true
        ignoreIfMalformed = true
    }

    fun get(key: String): String? = dotenv[key]

    fun get(key: String, defaultValue: String): String = dotenv[key] ?: defaultValue

    fun getInt(key: String, defaultValue: Int): Int = dotenv[key]?.toIntOrNull() ?: defaultValue



}