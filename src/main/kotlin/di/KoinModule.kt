package com.productivesocial.di

import com.productivesocial.feature.habit.HabitService
import com.productivesocial.feature.internal.InternalService
import com.productivesocial.feature.project.ProjectService
import com.productivesocial.feature.routine.RoutineService
import com.productivesocial.feature.sync.SyncService
import com.productivesocial.feature.task.TaskService
import com.productivesocial.feature.user.UserService
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
