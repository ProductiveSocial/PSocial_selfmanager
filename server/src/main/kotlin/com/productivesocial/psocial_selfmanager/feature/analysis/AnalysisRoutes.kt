package com.productivesocial.psocial_selfmanager.feature.analysis

import com.productivesocial.psocial_selfmanager.model.requests.JwtTokenRequest
import io.ktor.http.HttpStatusCode
import io.ktor.server.auth.principal
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.post

fun Route.analysisRoutes(analysisService: AnalysisService) {

    /**
     * POST /api/v1/analysis
     * Requires: Bearer JWT
     *
     * Gathers the authenticated user's tasks, habits, routines + pomodoro stats,
     * sends them to Claude via the billing service, and returns an AI analysis.
     * Credits are deducted from the user's billing account automatically.
     *
     * Body: { type: "PRODUCTIVITY_SUMMARY" | "TASK_PRIORITIZATION" |
     *                "HABIT_INSIGHTS" | "ROUTINE_OPTIMIZATION" | "CUSTOM",
     *         modelId: "<billing-service-model-uuid>",
     *         customPrompt?: "..." }
     */
    post("/analysis") {
        val userId = call.principal<JwtTokenRequest>()?.userId
            ?: return@post call.respond(HttpStatusCode.Unauthorized)

        val request = call.receive<AnalysisRequest>()

        try {
            val result = analysisService.analyze(userId, request)
            call.respond(HttpStatusCode.OK, result)
        } catch (e: IllegalStateException) {
            call.respond(HttpStatusCode.PaymentRequired, mapOf("error" to e.message))
        } catch (e: Exception) {
            call.respond(HttpStatusCode.InternalServerError, mapOf("error" to e.message))
        }
    }
}
