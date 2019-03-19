package net.rocketparty.di

import net.rocketparty.controller.MainController
import org.koin.dsl.module.module

val ControllerModule = module {
    single { MainController(get(), get(), get(), get(), get(), get(), get()) }
}