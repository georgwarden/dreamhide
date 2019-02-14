package net.rocketparty.di

import net.rocketparty.repository.*
import org.koin.dsl.module.module

val RepositoryModule = module {
    single { ExposedUserRepository() as UserRepository }
    single { ExposedTeamRepository() as TeamRepository }
    single { ExposedTaskRepository() as TaskRepository }
    single { ExposedSolutionRepository() as SolutionRepository }
    single { ExposedCategoriesRepository() as CategoryRepository }
}