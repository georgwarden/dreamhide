package net.rocketparty

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.auth.Authentication
import io.ktor.auth.authenticate
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.features.ContentNegotiation
import io.ktor.features.HttpsRedirect
import io.ktor.gson.gson
import io.ktor.http.HttpStatusCode
import io.ktor.request.receive
import io.ktor.response.respond
import io.ktor.routing.*
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import net.rocketparty.di.DomainModule
import net.rocketparty.di.RepositoryModule
import net.rocketparty.dto.*
import net.rocketparty.entity.DomainError
import net.rocketparty.entity.Task
import net.rocketparty.interactor.AuthInteractor
import net.rocketparty.interactor.PlatformInteractor
import net.rocketparty.interactor.TeamInteractor
import net.rocketparty.interactor.UserInteractor
import net.rocketparty.utils.Claims
import net.rocketparty.utils.acquirePrincipal
import net.rocketparty.utils.restore
import org.koin.standalone.StandAloneContext.startKoin
import java.time.Duration

fun main(args: Array<String>) {
    val koin = startKoin(
        listOf(
            RepositoryModule,
            DomainModule
        )
    )
    val authInteractor: AuthInteractor = koin.koinContext.get()
    val userInteractor: UserInteractor = koin.koinContext.get()
    val teamInteractor: TeamInteractor = koin.koinContext.get()
    val platformInteractor: PlatformInteractor = koin.koinContext.get()
    embeddedServer(Netty) {
        module(
            true,
            authInteractor,
            userInteractor,
            teamInteractor,
            platformInteractor
        )
    }.start(true)
    //io.ktor.server.netty.EngineMain.main(args)
}

@kotlin.jvm.JvmOverloads
fun Application.module(
    testing: Boolean = false,
    authInteractor: AuthInteractor,
    userInteractor: UserInteractor,
    teamInteractor: TeamInteractor,
    platformInteractor: PlatformInteractor
) {
    install(Authentication) {

        jwt("token-user") {
        }

        jwt("token-admin") {

        }

    }

    install(ContentNegotiation) {
        gson {
        }
    }

    // http://ktor.io/servers/features/https-redirect.html#testing
    if (!testing) {
        install(HttpsRedirect) {
            sslPort = 443
            permanentRedirect = true
        }
    }

    install(io.ktor.websocket.WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }

    routing {

        get("/login") {
            val (login, password) = call.receive<AuthorizationRequest>()
            authInteractor.tryAuthorize(login, password)
                .fold({ err ->
                    when (err) {
                        is DomainError.BadCredentials ->
                            call.respond(HttpStatusCode.BadRequest)
                        is DomainError.NotFound ->
                            call.respond(HttpStatusCode.NotFound)
                        else ->
                            call.respond(HttpStatusCode.InternalServerError)
                    }
                }, { token ->
                    call.respond(
                        HttpStatusCode.OK,
                        AuthorizationResponse(token)
                    )
                })
        }

        post("/logout") {
        }

        authenticate("token-user") {
            route("/platform") {
                route("/task") {
                    get {
                        val id = acquirePrincipal<JWTPrincipal>()
                            .payload
                            .getClaim(Claims.UserId)
                            .asInt()
                        val (taskId) = call.receive<GetTaskRequest>()
                        coroutineScope {
                            restore<DomainError, TaskDto> {
                                val taskAsync = async {
                                    platformInteractor.getTask(taskId).verify()
                                }
                                val solvedAsync = async {
                                    val user = userInteractor.getUser(id).verify()
                                    platformInteractor.isSolved(taskId, user)
                                }
                                val task = taskAsync.await()
                                val solved = solvedAsync.await()
                                task.toDto(solved)
                            }
                        }
                    }
                    get("/all") {
                        platformInteractor.getTasks()
                    }
                    get("/cats") {
                        platformInteractor.getCategories()
                    }
                }

                get("/user") {
                    val id = acquirePrincipal<JWTPrincipal>()
                        .payload
                        .getClaim(Claims.UserId)
                        .asInt()
                    userInteractor.getUser(id)
                        .fold({ err ->
                            when (err) {
                                DomainError.NotFound ->
                                    call.respond(HttpStatusCode.NotFound)
                                else ->
                                    call.respond(HttpStatusCode.InternalServerError)
                            }
                        }, { user ->
                            call.respond(
                                HttpStatusCode.OK,
                                user.toDto()
                            )
                        })
                }

                route("/team") {

                    get {
                        val id = acquirePrincipal<JWTPrincipal>()
                            .payload
                            .getClaim(Claims.UserId)
                            .asInt()
                        userInteractor.getUser(id)
                            .mapRight { user -> user.team }
                            .fold({ err ->
                                when (err) {
                                    DomainError.NotFound ->
                                        call.respond(HttpStatusCode.NotFound)
                                    else ->
                                        call.respond(HttpStatusCode.InternalServerError)
                                }
                            }, { team ->
                                call.respond(HttpStatusCode.OK, team.toDto())
                            })
                    }

                    get("/all") {
                        teamInteractor.getAllTeams()
                    }

                }

                post("/attempt") {}

                get("/subscribe") {}
            }
        }

        authenticate("token-admin") {
            route("/admin") {
                route("/task") {
                    post {}
                    patch {}
                    delete {}
                }
            }
        }
    }
}

