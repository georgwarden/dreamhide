package net.rocketparty.di

import net.rocketparty.interactor.*
import org.koin.dsl.module.module

val DomainModule = module {
    single { AuthInteractor(get()) }
    single { UserInteractor(get()) }
    single { TeamInteractor(get()) }
    single { PlatformInteractor(get(), get(), get()) }
    single { JwtInteractor(get()) }
}