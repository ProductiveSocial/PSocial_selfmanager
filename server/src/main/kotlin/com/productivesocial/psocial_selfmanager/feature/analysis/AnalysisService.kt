package com.productivesocial.psocial_selfmanager.feature.analysis

import com.productivesocial.psocial_selfmanager.client.BillingClient
import com.productivesocial.psocial_selfmanager.client.TimerClient
import com.productivesocial.psocial_selfmanager.database.entities.HabitDAO
import com.productivesocial.psocial_selfmanager.database.entities.HabitTable
import com.productivesocial.psocial_selfmanager.database.entities.RoutineDAO
import com.productivesocial.psocial_selfmanager.database.entities.RoutineTable
import com.productivesocial.psocial_selfmanager.database.entities.TaskDAO
import com.productivesocial.psocial_selfmanager.database.entities.TaskTable
import com.productivesocial.psocial_selfmanager.database.entities.UserDAO
import com.productivesocial.psocial_selfmanager.database.entities.UserTable
import com.productivesocial.psocial_selfmanager.utils.query
import io.ktor.server.plugins.NotFoundException
import kotlinx.serialization.Serializable
import org.jetbrains.exposed.v1.core.eq

enum class AnalysisType {
    PRODUCTIVITY_SUMMARY,   // "How productive was I this week?"
    TASK_PRIORITIZATION,    // "What should I focus on next?"
    HABIT_INSIGHTS,         // "How are my habits going?"
    ROUTINE_OPTIMIZATION,   // "How can I improve my routines?"
    CUSTOM                  // Free-form query
}

@Serializable
data class AnalysisRequest(
    val type: AnalysisType,
    val modelId: String,             // billing service model UUID
    val customPrompt: String? = null // used with CUSTOM type
)

@Serializable
data class AnalysisResult(
    val type: String,
    val insight: String,
    val creditsCharged: Int,
)

class AnalysisService(
    private val billingClient: BillingClient,
    private val timerClient: TimerClient,
) {
    suspend fun analyze(userId: Long, request: AnalysisRequest): AnalysisResult {
        // 1. Load user (needed to get selfmanager_user_id for billing linkage)
        val user = query {
            UserDAO.find { UserTable.id eq userId }.singleOrNull()
        } ?: throw NotFoundException("User not found")

        val selfmanagerUserId = userId.toString()

        // 2. Gather data from own DB
        val tasks = query {
            TaskDAO.find { TaskTable.userId eq userId }.map {
                mapOf(
                    "name" to it.name,
                    "priority" to it.priority.name,
                    "completed" to (it.completionLogs.count() > 0),
                    "timeSpentMinutes" to it.timeSpentMinutes,
                )
            }
        }

        val habits = query {
            HabitDAO.find { HabitTable.userId eq userId }.map {
                mapOf(
                    "name" to it.name,
                    "type" to it.habitType.name,
                    "recurrency" to it.recurrency.name,
                    "timeSpentMinutes" to it.timeSpentMinutes,
                )
            }
        }

        val routines = query {
            RoutineDAO.find { RoutineTable.userId eq userId }.map {
                mapOf(
                    "name" to it.name,
                    "recurrency" to it.recurrency.name,
                )
            }
        }

        // 3. Fetch pomodoro stats from timer service
        val pomodoroStats = timerClient.getUserStats(userId)

        // 4. Build input payload for Claude
        val inputData = buildMap<String, Any> {
            put("analysis_type", request.type.name)
            put("tasks", tasks)
            put("habits", habits)
            put("routines", routines)
            pomodoroStats?.let {
                put("pomodoro", mapOf(
                    "totalSessions" to it.totalSessions,
                    "completedSessions" to it.completedSessions,
                    "totalWorkMinutes" to it.totalWorkMinutes,
                    "totalCycles" to it.totalCycles,
                    "avgWorkMinutesPerSession" to it.avgWorkMinutesPerSession,
                    "sessionsByEntityType" to it.sessionsByEntityType,
                ))
            }
            put("prompt", buildPrompt(request))
        }

        // 5. Call billing service — it runs Claude and charges credits
        val rawResult = billingClient.predict(
            selfmanagerUserId = selfmanagerUserId,
            modelId = request.modelId,
            inputData = inputData,
            contextEntityType = "analysis",
            contextEntityId = request.type.name,
        ) ?: throw IllegalStateException("Analysis failed — check credits or billing service")

        // 6. Parse response (billing returns JSON with output_data.text)
        val insight = extractInsight(rawResult)
        val creditsCharged = extractCreditsCharged(rawResult)

        return AnalysisResult(
            type = request.type.name,
            insight = insight,
            creditsCharged = creditsCharged,
        )
    }

    private fun buildPrompt(request: AnalysisRequest): String = when (request.type) {
        AnalysisType.PRODUCTIVITY_SUMMARY ->
            "Based on the user's tasks, habits, routines and focus sessions, provide a concise productivity summary. Highlight what went well and what needs attention."
        AnalysisType.TASK_PRIORITIZATION ->
            "Looking at the user's pending tasks and time spent so far, suggest a prioritized focus list for today. Be specific and actionable."
        AnalysisType.HABIT_INSIGHTS ->
            "Analyze the user's habits. Which are consistent? Which are at risk? What one change would have the highest impact?"
        AnalysisType.ROUTINE_OPTIMIZATION ->
            "Review the user's routines and time spent. How can they be made more efficient? Suggest concrete improvements."
        AnalysisType.CUSTOM ->
            request.customPrompt ?: "Provide a general productivity analysis."
    }

    private fun extractInsight(rawJson: String): String = try {
        val map = com.google.gson.Gson().fromJson(rawJson, Map::class.java)
        @Suppress("UNCHECKED_CAST")
        val outputData = map["output_data"] as? Map<String, Any>
        outputData?.get("text") as? String ?: rawJson
    } catch (e: Exception) { rawJson }

    private fun extractCreditsCharged(rawJson: String): Int = try {
        val map = com.google.gson.Gson().fromJson(rawJson, Map::class.java)
        (map["credits_charged"] as? Double)?.toInt() ?: 0
    } catch (e: Exception) { 0 }
}
