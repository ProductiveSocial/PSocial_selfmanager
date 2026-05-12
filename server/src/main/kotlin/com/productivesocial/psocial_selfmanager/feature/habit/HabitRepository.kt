package com.productivesocial.psocial_selfmanager.feature.habit

import com.productivesocial.psocial_selfmanager.model.PaginatedResponse
import com.productivesocial.psocial_selfmanager.model.requests.HabitRequest
import com.productivesocial.psocial_selfmanager.model.requests.LogHabitCompletionRequest
import com.productivesocial.psocial_selfmanager.model.requests.UpdateHabitRequest
import com.productivesocial.psocial_selfmanager.model.responses.HabitCompletionResponse
import com.productivesocial.psocial_selfmanager.model.responses.HabitResponse

interface HabitRepository {
    suspend fun getHabitsByUserId(userId: Long): PaginatedResponse<HabitResponse>
    suspend fun getHabitById(userId: Long, habitId: Long): HabitResponse?
    suspend fun addHabit(userId: Long, habit: HabitRequest): HabitResponse
    suspend fun updateHabit(userId: Long, habitId: Long, habit: UpdateHabitRequest): HabitResponse
    suspend fun deleteHabit(userId: Long, habitId: Long): HabitResponse
    suspend fun logCompletion(userId: Long, habitId: Long, request: LogHabitCompletionRequest): HabitCompletionResponse
    suspend fun deleteCompletion(userId: Long, habitId: Long, completionId: Long)
    suspend fun getCompletions(userId: Long, habitId: Long): List<HabitCompletionResponse>
}
