package com.productivesocial.psocial_selfmanager.di

import com.productivesocial.psocial_selfmanager.feature.habit.HabitService
import com.productivesocial.psocial_selfmanager.feature.internal.InternalService
import com.productivesocial.psocial_selfmanager.feature.project.ProjectService
import com.productivesocial.psocial_selfmanager.feature.routine.RoutineService
import com.productivesocial.psocial_selfmanager.feature.sync.SyncService
import com.productivesocial.psocial_selfmanager.feature.task.TaskService
import com.productivesocial.psocial_selfmanager.feature.user.UserService
import org.koin.dsl.module

val serviceModule = module {
    single { UserService() }
    single { ProjectService() }
    single { TaskService() }
    single { HabitService() }
    single { RoutineService() }
    single { SyncService() }
    single { InternalService() }
}
