package net.rocketparty

import io.ktor.application.*
import io.ktor.response.*
import io.ktor.routing.*
import io.ktor.http.*
import io.ktor.auth.*
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.auth.jwt.jwt
import io.ktor.gson.*
import io.ktor.features.*
import io.ktor.websocket.*
import io.ktor.http.cio.websocket.*
import io.ktor.request.receive
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import net.rocketparty.di.DomainModule
import net.rocketparty.di.RepositoryModule
import net.rocketparty.dto.AuthorizationRequest
import net.rocketparty.dto.AuthorizationResponse
import net.rocketparty.entity.CommonError
import net.rocketparty.interactor.AuthInteractor
import org.jetbrains.exposed.sql.Database
import org.koin.standalone.StandAloneContext.startKoin
import java.time.*

fun main(args: Array<String>) {
    val koin = startKoin(
        listOf(
            RepositoryModule,
            DomainModule
        )
    )
    val authInteractor: AuthInteractor = koin.koinContext.get()
    embeddedServer(Netty) {
        module(
            true,
            authInteractor
        )
    }.start(true)
    //io.ktor.server.netty.EngineMain.main(args)
}

@kotlin.jvm.JvmOverloads
fun Application.module(
    testing: Boolean = false,
    authInteractor: AuthInteractor
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
                        is CommonError.BadCredentials ->
                            call.respond(HttpStatusCode.BadRequest)
                        is CommonError.UserNotFound ->
                            call.respond(HttpStatusCode.NotFound)
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
                    get {}
                    get("/all") {}
                    get("/cats") {}
                }
                get("/user") {}
                route("/team") {
                    get {}
                    get("/all") {}
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

