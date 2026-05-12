package com.productivesocial.psocial_selfmanager.client

import com.google.gson.Gson
import com.productivesocial.psocial_selfmanager.config.DotEnvConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.statement.bodyAsText
import io.ktor.http.isSuccess
import io.ktor.serialization.gson.gson

data class UserPomodoroStats(
    val userId: Long,
    val totalSessions: Int,
    val completedSessions: Int,
    val totalWorkMinutes: Int,
    val totalCycles: Int,
    val avgWorkMinutesPerSession: Double,
    val sessionsByEntityType: Map<String, Int>,
    val workMinutesByEntityType: Map<String, Int>,
)

/**
 * HTTP client for calling psocial_timer's internal API.
 * Used to fetch pomodoro stats when building an analysis context for Claude.
 */
class TimerClient {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) { gson() }
    }

    private val baseUrl get() = DotEnvConfig.timerServiceUrl
    private val internalKey get() = DotEnvConfig.internalApiKey
    private val gson = Gson()

    suspend fun getUserStats(userId: Long): UserPomodoroStats? = try {
        val response = httpClient.get("$baseUrl/internal/users/$userId/stats") {
            header("X-Internal-Key", internalKey)
        }
        if (response.status.isSuccess()) {
            gson.fromJson(response.bodyAsText(), UserPomodoroStats::class.java)
        } else null
    } catch (e: Exception) {
        println("TimerClient.getUserStats failed: ${e.message}")
        null
    }

    fun close() = httpClient.close()
}
