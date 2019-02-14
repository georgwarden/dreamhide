package net.rocketparty.di

import net.rocketparty.interactor.AuthInteractor
import net.rocketparty.interactor.PlatformInteractor
import net.rocketparty.interactor.TeamInteractor
import net.rocketparty.interactor.UserInteractor
import org.koin.dsl.module.module

val DomainModule = module {
    single { AuthInteractor(get()) }
    single { UserInteractor(get()) }
    single { TeamInteractor(get()) }
    single { PlatformInteractor(get(), get(), get()) }
}