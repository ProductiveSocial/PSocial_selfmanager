package com.productivesocial.psocial_selfmanager.feature.routine

import com.productivesocial.psocial_selfmanager.model.PaginatedResponse
import com.productivesocial.psocial_selfmanager.model.requests.RoutineRequest
import com.productivesocial.psocial_selfmanager.model.requests.UpdateRoutineRequest
import com.productivesocial.psocial_selfmanager.model.responses.RoutineResponse


interface RoutineRepository {
    suspend fun getRoutinesByUserId(userId: Long): PaginatedResponse<RoutineResponse>
    suspend fun getRoutineById(userId: Long, routineId: Long): RoutineResponse?
    suspend fun addRoutine(userId: Long, routine: RoutineRequest): RoutineResponse
    suspend fun updateRoutine(userId: Long, routineId: Long, routine: UpdateRoutineRequest): RoutineResponse
    suspend fun deleteRoutine(userId: Long, routineId: Long): RoutineResponse
}
