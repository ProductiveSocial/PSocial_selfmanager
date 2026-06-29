package com.productivesocial.psocial_selfmanager.client

import com.google.gson.Gson
import com.productivesocial.psocial_selfmanager.config.DotEnvConfig
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.gson.gson

/**
 * HTTP client for calling psocial_billing_service internal API.
 * Used to run ML/LLM predictions on behalf of users and charge their credits.
 * All calls use X-Internal-Key — no JWT needed for service-to-service.
 */
class BillingClient {

    private val httpClient = HttpClient(CIO) {
        install(ContentNegotiation) { gson() }
    }

    private val baseUrl get() = DotEnvConfig.billingServiceUrl
    private val internalKey get() = DotEnvConfig.internalApiKey
    private val gson = Gson()

    /**
     * Run an ML or LLM prediction on behalf of a selfmanager user.
     * Credits are automatically charged to the user's billing account.
     *
     * @param selfmanagerUserId the user's ID in psocial_selfmanager
     * @param modelId UUID of the model to use (from billing service)
     * @param inputData prediction payload (e.g. task list, habit data, prompt)
     * @param contextEntityType optional: "task" | "habit" | "routine"
     * @param contextEntityId optional: ID of the entity this prediction is for
     * @return raw JSON response from the billing service, or null on failure
     */
    suspend fun predict(
        selfmanagerUserId: String,
        modelId: String,
        inputData: Map<String, Any>,
        contextEntityType: String? = null,
        contextEntityId: String? = null,
    ): String? = try {
        val payload = buildMap {
            put("selfmanager_user_id", selfmanagerUserId)
            put("model_id", modelId)
            put("input_data", inputData)
            contextEntityType?.let { put("context_entity_type", it) }
            contextEntityId?.let { put("context_entity_id", it) }
        }
        val response = httpClient.post("$baseUrl/api/v1/internal/predict") {
            contentType(ContentType.Application.Json)
            header("X-Internal-Key", internalKey)
            setBody(gson.toJson(payload))
        }
        if (response.status.isSuccess()) response.bodyAsText() else null
    } catch (e: Exception) {
        println("BillingClient.predict failed: ${e.message}")
        null
    }

    /** Deposit credits for a user. Fire-and-forget — failures are logged, not thrown. */
    suspend fun deposit(selfmanagerUserId: String, amount: Int, description: String = "Manual deposit"): Boolean = try {
        val payload = mapOf("amount" to amount, "description" to description)
        val response = httpClient.post("$baseUrl/api/v1/internal/users/$selfmanagerUserId/deposit") {
            contentType(ContentType.Application.Json)
            header("X-Internal-Key", internalKey)
            setBody(gson.toJson(payload))
        }
        response.status.isSuccess()
    } catch (e: Exception) {
        println("BillingClient.deposit failed: ${e.message}")
        false
    }

    /**
     * Check how many credits a user has in the billing service.
     */
    suspend fun getBalance(selfmanagerUserId: String): Int? = try {
        val response = httpClient.get("$baseUrl/api/v1/internal/users/$selfmanagerUserId/balance") {
            header("X-Internal-Key", internalKey)
        }
        if (response.status.isSuccess()) {
            val body = response.bodyAsText()
            @Suppress("UNCHECKED_CAST")
            (gson.fromJson(body, Map::class.java)["credits_balance"] as? Double)?.toInt()
        } else null
    } catch (e: Exception) {
        println("BillingClient.getBalance failed: ${e.message}")
        null
    }

    fun close() = httpClient.close()
}
