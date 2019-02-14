package net.rocketparty

import net.rocketparty.controller.MainController
import net.rocketparty.di.ControllerModule
import net.rocketparty.di.DomainModule
import net.rocketparty.di.RepositoryModule
import org.koin.standalone.StandAloneContext.startKoin

fun main(args: Array<String>) {
    val koin = startKoin(
        listOf(
            RepositoryModule,
            DomainModule,
            ControllerModule
        )
    )
    val controller = koin.koinContext.get<MainController>()
    controller.start(true)
}

