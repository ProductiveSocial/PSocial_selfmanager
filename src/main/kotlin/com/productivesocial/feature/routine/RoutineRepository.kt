package com.productivesocial.com.productivesocial.feature.routine

import com.productivesocial.com.productivesocial.model.PaginatedResponse
import com.productivesocial.com.productivesocial.model.requests.RoutineRequest
import com.productivesocial.com.productivesocial.model.requests.UpdateRoutineRequest
import com.productivesocial.com.productivesocial.model.responses.RoutineResponse

interface RoutineRepository {
    suspend fun getRoutinesByUserId(userId: Long): PaginatedResponse<RoutineResponse>
    suspend fun getRoutineById(userId: Long, routineId: Long): RoutineResponse?
    suspend fun addRoutine(userId: Long, routine: RoutineRequest): RoutineResponse
    suspend fun updateRoutine(userId: Long, routineId: Long, routine: UpdateRoutineRequest): RoutineResponse
    suspend fun deleteRoutine(userId: Long, routineId: Long): RoutineResponse
}
