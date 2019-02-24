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
import io.ktor.util.getOrFail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.withContext
import net.rocketparty.dto.*
import net.rocketparty.dto.model.FullTaskInfoDto
import net.rocketparty.dto.model.TaskCreationDto
import net.rocketparty.dto.model.TaskDeltaDto
import net.rocketparty.dto.model.TaskModelDto
import net.rocketparty.dto.request.AttemptRequest
import net.rocketparty.dto.request.AuthorizationRequest
import net.rocketparty.dto.response.*
import net.rocketparty.entity.DomainError
import net.rocketparty.entity.Task
import net.rocketparty.interactor.*
import net.rocketparty.utils.Claims
import net.rocketparty.utils.Configs
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

                val jwtIssuer = jwtInteractor.getIssuer()
                val jwtRealm = jwtInteractor.getRealm()
                val jwtSecret = jwtInteractor.getSecret()

                jwt(Configs.User) {

                    val jwtAudience = jwtInteractor.getAudience()

                    val jwtVerifier = JWT.require(Algorithm.HMAC256(jwtSecret))
                        .withAudience(jwtAudience)
                        .withIssuer(jwtIssuer)
                        .build()

                    verifier(jwtVerifier)
                    realm = jwtRealm

                    validate { credentials ->
                        credentials.payload
                            .takeIf(jwtInteractor::validate)
                            ?.let(::JWTPrincipal)
                    }
                }

                jwt(Configs.Admin) {

                    val jwtAudience = jwtInteractor.getAdminAudience()

                    val jwtVerifier = JWT.require(Algorithm.HMAC256(jwtSecret))
                        .withAudience(jwtAudience)
                        .withIssuer(jwtIssuer)
                        .build()

                    realm = jwtRealm
                    verifier(jwtVerifier)

                    validate { credentials ->
                        credentials.payload
                            .takeIf(jwtInteractor::validateAdmin)
                            ?.let(::JWTPrincipal)
                    }
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
                            val token = if (user.isAdmin) {
                                jwtInteractor.generateAdminToken()
                            } else {
                                jwtInteractor.generateToken(user.id)
                            }
                            call.respond(AuthorizationResponse(token))
                        })
                }

                post("/logout") {
                }

                authenticate(Configs.User, Configs.Admin) {

                    route("/platform") {

                        route("/task") {

                            get {
                                val id = acquirePrincipal<JWTPrincipal>()
                                    .payload
                                    .getClaim(Claims.UserId)
                                    .asInt()
                                val taskId = call.request.queryParameters.getOrFail<Int>("id")
                                coroutineScope {
                                    restore<DomainError, FullTaskInfoDto> {
                                        val taskAsync = async {
                                            platformInteractor.getTask(taskId).verify()
                                        }
                                        val solvedAsync = async {
                                            val user = userInteractor.getUser(id).verify()
                                            user.team?.let { team ->
                                                platformInteractor.isSolved(taskId, team)
                                            } ?: false
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
                                val id = acquirePrincipal<JWTPrincipal>()
                                    .payload
                                    .getClaim(Claims.UserId)
                                    .asInt()
                                val (tasks, maybeSolutions) = withContext(Dispatchers.IO) {
                                    coroutineScope {
                                        val tasks = async {
                                            platformInteractor.getTasks()
                                        }
                                        val solutions = async {
                                            userInteractor.getUser(id)
                                                .mapRight { user ->
                                                    user.team?.run {
                                                        platformInteractor.getSolutionsOf(id)
                                                    } ?: emptyList()
                                                }
                                        }
                                        tasks.await() to solutions.await()
                                    }
                                }
                                maybeSolutions.mapRight { solutions -> solutions.toHashSet() }
                                    .mapRight { solutions ->
                                        GetTasksResponse(tasks.map { task ->
                                            task.toEssentials(task.id in solutions)
                                        })
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
                                        { response ->
                                            call.respond(response)
                                        }
                                    )
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
                                        if (team == null) {
                                            call.respond(HttpStatusCode.OK)
                                        } else {
                                            call.respond(HttpStatusCode.OK, team.toDto())
                                        }
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

                authenticate(Configs.Admin) {

                    route("/admin") {

                        route("/task") {

                            post {
                                val model = call.receive<TaskCreationDto>()
                                platformInteractor.createTask(model.toEntity())
                                    .fold(
                                        {
                                            call.respond(HttpStatusCode.InternalServerError)
                                        },
                                        { task ->
                                            call.respond(
                                                TaskModelDto(
                                                    task.toInfo(),
                                                    task.toDescription()
                                                )
                                            )
                                        }
                                    )
                            }

                            patch {
                                val delta = call.receive<TaskDeltaDto>()
                                val task = withContext(Dispatchers.IO) {
                                    platformInteractor.editTask(delta.toEntity())
                                }
                                call.respond(
                                    TaskModelDto(
                                        task.toInfo(),
                                        task.toDescription()
                                    )
                                )
                            }

                            delete {
                                // TODO
                                // write ticket to Kotlin team about better dispatching.
                                // if there is 2 functions with following contracts:
                                // ```
                                //  fun f1(): Type1 (1)
                                //  fun <T> f1(): T (2)
                                // ```
                                // then
                                // ```
                                //  val result: Type2 = f1()
                                // ```
                                // will dispatch to (1), while it could safely (?) dispatch to (2).
                                val taskId = call.request.queryParameters.getOrFail<Int>("id")
                                withContext(Dispatchers.IO) {
                                    platformInteractor.deleteTask(taskId)
                                }
                                call.respond(HttpStatusCode.OK)
                            }

                        }
                    }
                }
            }
        }.start(true)
    }

}