package net.rocketparty.event

import com.google.gson.Gson
import io.ktor.application.call
import io.ktor.auth.jwt.JWTPrincipal
import io.ktor.request.receive
import io.ktor.response.respondTextWriter
import io.ktor.routing.Route
import io.ktor.routing.get
import io.ktor.routing.post
import net.rocketparty.interactor.EventInteractor
import net.rocketparty.utils.Claims
import net.rocketparty.utils.acquirePrincipal

fun Route.subscribe(gson: Gson, eventInteractor: EventInteractor) {
    get("/subscribe") {
        val id = acquirePrincipal<JWTPrincipal>()
            .payload
            .getClaim(Claims.UserId)
            .asInt()
        call.respondTextWriter {
            val source = eventInteractor.subscribe(id)
            for (event in source) {
                write(gson.toJson(event))
            }
            close()
        }
    }
}

fun Route.adminBroadcast(eventInteractor: EventInteractor) {
    post("/broadcast") {
        val message = call.receive<Broadcast>()
        eventInteractor.adminBroadcast(message)
    }
}