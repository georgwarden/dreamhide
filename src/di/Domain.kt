package net.rocketparty.di

import net.rocketparty.interactor.AuthInteractor
import net.rocketparty.interactor.UserInteractor
import org.koin.dsl.module.module

val DomainModule = module {
    single { AuthInteractor(get()) }
    single { UserInteractor(get()) }
}