package net.rocketparty

import io.ktor.util.KtorExperimentalAPI
import net.rocketparty.controller.MainController
import net.rocketparty.di.ControllerModule
import net.rocketparty.di.DomainModule
import net.rocketparty.di.RepositoryModule
import net.rocketparty.exposed.*
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.transactions.transaction
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

    Database.connect("jdbc:h2:tcp://localhost/~/test", driver = "org.h2.Driver", user = "dreamhide")
    transaction {

        SchemaUtils.drop(
            Attachments,
            Categories,
            Solutions,
            Tasks,
            Teams,
            Users
        )

        SchemaUtils.create(
            Attachments,
            Categories,
            Solutions,
            Tasks,
            Teams,
            Users
        )

        val team = Teams.insert {
            it[name] = "Wizards of the Coast"
        } get Teams.id

        Users.insert {
            it[name] = "vasya228"
            it[passwordHash] = "25d55ad283aa400af464c76d713c07ad"
            it[teamId] = team
        }

        val categoryId = Categories.insert {
            it[name] = "web"
        } get Categories.id ?: throw Exception("db init exception")

        Tasks.insert {
            it[name] = "Hello World"
            it[description] = "Nothing really special about it"
            it[reward] = 200
            it[category] = categoryId
            it[flag] = "12345678"
        }

    }

    val controller = koin.koinContext.get<MainController>()
    controller.start(true)
}

