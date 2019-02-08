package net.rocketparty.di

import net.rocketparty.repository.ExposedUserRepository
import net.rocketparty.repository.UserRepository
import org.koin.dsl.module.module

val RepositoryModule = module {
    single { ExposedUserRepository() as UserRepository }
}