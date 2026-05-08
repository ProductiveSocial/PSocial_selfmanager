package com.productivesocial.di

import com.productivesocial.feature.habit.HabitService
import com.productivesocial.feature.project.ProjectService
import com.productivesocial.feature.routine.RoutineService
import com.productivesocial.feature.task.TaskService
import org.koin.dsl.module

val serviceModule = module {
    single { ProjectService() }
    single { TaskService() }
    single { HabitService() }
    single { RoutineService() }
}