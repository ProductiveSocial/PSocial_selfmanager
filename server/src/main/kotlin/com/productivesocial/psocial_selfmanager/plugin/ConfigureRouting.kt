package com.productivesocial.psocial_selfmanager.plugin

import com.productivesocial.psocial_selfmanager.feature.habit.habitRoutes
import com.productivesocial.psocial_selfmanager.feature.internal.internalRoutes
import com.productivesocial.psocial_selfmanager.feature.project.projectRoutes
import com.productivesocial.psocial_selfmanager.feature.routine.routineRoutes
import com.productivesocial.psocial_selfmanager.feature.sync.syncRoutes
import com.productivesocial.psocial_selfmanager.feature.task.taskRoutes
import com.productivesocial.psocial_selfmanager.feature.user.userRoutes
import com.productivesocial.psocial_selfmanager.feature.habit.HabitService
import com.productivesocial.psocial_selfmanager.feature.internal.InternalService
import com.productivesocial.psocial_selfmanager.feature.project.ProjectService
import com.productivesocial.psocial_selfmanager.feature.routine.RoutineService
import com.productivesocial.psocial_selfmanager.feature.sync.SyncService
import com.productivesocial.psocial_selfmanager.feature.task.TaskService
import com.productivesocial.psocial_selfmanager.feature.user.UserService
import io.ktor.server.application.Application
import io.ktor.server.response.respondRedirect
import io.ktor.server.routing.get
import io.ktor.server.routing.openapi.hide
import io.ktor.server.routing.route
import io.ktor.server.routing.routing
import io.ktor.utils.io.ExperimentalKtorApi
import org.koin.ktor.ext.inject

@OptIn(ExperimentalKtorApi::class)
fun Application.configureRoute() {
    val userService: UserService by inject()
    val projectService: ProjectService by inject()
    val taskService: TaskService by inject()
    val habitService: HabitService by inject()
    val routineService: RoutineService by inject()
    val syncService: SyncService by inject()
    val internalService: InternalService by inject()

    routing {
        get("/") {
            call.respondRedirect("/swagger")
        }.hide()
        internalRoutes(internalService)
        route("/api") {
            route("v1") {
                route("users") { userRoutes(userService) }
                route("projects") { projectRoutes(projectService) }
                route("tasks") { taskRoutes(taskService) }
                route("habits") { habitRoutes(habitService) }
                route("routines") { routineRoutes(routineService) }
                syncRoutes(syncService)
            }
        }
    }
}
