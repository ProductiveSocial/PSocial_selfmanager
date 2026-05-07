package com.productivesocial.com.productivesocial.feature.habit

import com.productivesocial.com.productivesocial.model.PaginatedResponse
import com.productivesocial.com.productivesocial.model.requests.HabitRequest
import com.productivesocial.com.productivesocial.model.requests.UpdateHabitRequest
import com.productivesocial.com.productivesocial.model.responses.HabitResponse

interface HabitRepository {
    suspend fun getHabitsByUserId(userId: Long): PaginatedResponse<HabitResponse>
    suspend fun getHabitById(userId: Long, habitId: Long): HabitResponse?
    suspend fun addHabit(userId: Long, habit: HabitRequest): HabitResponse
    suspend fun updateHabit(userId: Long, habitId: Long, habit: UpdateHabitRequest): HabitResponse
    suspend fun deleteHabit(userId: Long, habitId: Long): HabitResponse
}
