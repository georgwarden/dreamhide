package net.rocketparty.controller

import com.auth0.jwt.JWT
import com.auth0.jwt.algorithms.Algorithm
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
import io.ktor.util.KtorExperimentalAPI
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import net.rocketparty.dto.model.FullTaskInfoDto
import net.rocketparty.dto.request.AttemptRequest
import net.rocketparty.dto.request.AuthorizationRequest
import net.rocketparty.dto.request.GetTaskRequest
import net.rocketparty.dto.response.*
import net.rocketparty.dto.toDescription
import net.rocketparty.dto.toDto
import net.rocketparty.dto.toInfo
import net.rocketparty.entity.DomainError
import net.rocketparty.interactor.*
import net.rocketparty.utils.Claims
import net.rocketparty.utils.acquirePrincipal
import net.rocketparty.utils.restore
import java.time.Duration

@KtorExperimentalAPI
class MainController(
    private val authInteractor: AuthInteractor,
    private val userInteractor: UserInteractor,
    private val teamInteractor: TeamInteractor,
    private val platformInteractor: PlatformInteractor,
    private val jwtInteractor: JwtInteractor
) {

    fun start(testing: Boolean) {
        embeddedServer(Netty, port = 8080) {
            install(Authentication) {
                jwt("token-user") {

                    val jwtIssuer = jwtInteractor.getIssuer()
                    val jwtRealm = jwtInteractor.getRealm()
                    val jwtAudience = jwtInteractor.getAudience()
                    val jwtSecret = jwtInteractor.getSecret()
                    val jwtVerifier = JWT.require(Algorithm.HMAC256(jwtSecret))
                        .withAudience(jwtAudience)
                        .withIssuer(jwtIssuer)
                        .build()

                    verifier(jwtVerifier)
                    realm = jwtRealm

                    validate { credentials ->
                        credentials.payload
                            .takeIf { jwtInteractor.validate(it) }
                            ?.let { JWTPrincipal(it) }
                    }
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

                post("/login") {
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
                        }, { user ->
                            val token = jwtInteractor.generateToken(user.id)
                            call.respond(AuthorizationResponse(token))
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
                                    restore<DomainError, FullTaskInfoDto> {
                                        val taskAsync = async {
                                            platformInteractor.getTask(taskId).verify()
                                        }
                                        val solvedAsync = async {
                                            val user = userInteractor.getUser(id).verify()
                                            platformInteractor.isSolved(taskId, user)
                                        }
                                        val task = taskAsync.await()
                                        val solved = solvedAsync.await()
                                        FullTaskInfoDto(
                                            task.toInfo(),
                                            task.toDescription(),
                                            solved
                                        )
                                    }
                                }.fold(
                                    { err ->
                                        call.respond(
                                            when (err) {
                                                is DomainError.NotFound ->
                                                    HttpStatusCode.NotFound
                                                else ->
                                                    HttpStatusCode.InternalServerError
                                            }
                                        )
                                    },
                                    { task ->
                                        call.respond(task)
                                    }
                                )
                            }
                            get("/all") {
                                platformInteractor.getTasks()
                                    .map { task -> task.toInfo() }
                                    .let(::GetTasksResponse)
                                    .also { response -> call.respond(response) }
                            }
                            get("/cats") {
                                platformInteractor.getCategories()
                                    .map { cat -> cat.toDto() }
                                    .let(::CategoriesResponse)
                                    .also { response -> call.respond(response) }
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
                                    .map { team -> team.toDto() }
                                    .let(::GetTeamsResponse)
                                    .also { response -> call.respond(response) }
                            }


                        }

                        post("/attempt") {
                            val (taskId, flag) = call.receive<AttemptRequest>()
                            val userId = acquirePrincipal<JWTPrincipal>()
                                .payload
                                .getClaim(Claims.UserId)
                                .asInt()
                            coroutineScope {
                                restore<DomainError, Boolean> {
                                    val team = async {
                                        userInteractor.getUser(userId)
                                    }.await()
                                        .verify()
                                    async {
                                        platformInteractor.attempt(team.id, taskId, flag)
                                    }.await()
                                        .verify().also { correct ->
                                            if (correct)
                                                platformInteractor.solve(team.id, taskId)
                                        }
                                }.fold(
                                    { err ->
                                        when (err) {
                                            DomainError.AlreadyExists ->
                                                call.respond(
                                                    HttpStatusCode.UnprocessableEntity,
                                                    "This team already solved this task"
                                                )
                                            DomainError.NotFound ->
                                                call.respond(HttpStatusCode.NotFound)
                                            else ->
                                                call.respond(HttpStatusCode.InternalServerError)
                                        }
                                    },
                                    { result ->
                                        call.respond(AttemptResponse(result))
                                    }
                                )
                            }
                        }

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
        }.start(true)
    }

}