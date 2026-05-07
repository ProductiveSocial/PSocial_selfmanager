package com.productivesocial.com.productivesocial.di

import com.productivesocial.com.productivesocial.feature.habit.HabitService
import com.productivesocial.com.productivesocial.feature.project.ProjectService
import com.productivesocial.com.productivesocial.feature.routine.RoutineService
import com.productivesocial.com.productivesocial.feature.task.TaskService
import org.koin.dsl.module

val serviceModule = module {
    single { ProjectService() }
    single { TaskService() }
    single { HabitService() }
    single { RoutineService() }
}