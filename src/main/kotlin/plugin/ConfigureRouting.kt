package com.productivesocial.plugin

import com.productivesocial.feature.habit.HabitService
import com.productivesocial.feature.habit.habitRoutes
import com.productivesocial.feature.project.ProjectService
import com.productivesocial.feature.project.projectRoutes
import com.productivesocial.feature.routine.RoutineService
import com.productivesocial.feature.routine.routineRoutes
import com.productivesocial.feature.task.TaskService
import com.productivesocial.feature.task.taskRoutes
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
    val projectService: ProjectService by inject()
    val taskService: TaskService by inject()
    val habitService: HabitService by inject()
    val routineService: RoutineService by inject()

    routing {
        get("/") {
            call.respondRedirect("/swagger")
        }.hide()
        route("/api") {
            route("v1") {
                route("projects") { projectRoutes(projectService) }
                route("tasks") { taskRoutes(taskService) }
                route("habits") { habitRoutes(habitService) }
                route("routines") { routineRoutes(routineService) }
            }
        }
    }
}