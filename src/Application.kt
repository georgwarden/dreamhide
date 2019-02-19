package net.rocketparty

import io.ktor.util.KtorExperimentalAPI
import net.rocketparty.controller.MainController
import net.rocketparty.di.ControllerModule
import net.rocketparty.di.DomainModule
import net.rocketparty.di.RepositoryModule
import org.jetbrains.exposed.sql.Database
import org.koin.standalone.StandAloneContext.startKoin

@KtorExperimentalAPI
fun main(args: Array<String>) {
    val koin = startKoin(
        listOf(
            RepositoryModule,
            DomainModule,
            ControllerModule
        )
    )

    Database.connect("jdbc:h2:~/test", driver = "org.h2.Driver", user = "sa")

    val controller = koin.koinContext.get<MainController>()
    controller.start(true)
}

