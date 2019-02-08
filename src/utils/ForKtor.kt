package net.rocketparty.utils

import io.ktor.application.ApplicationCall
import io.ktor.application.call
import io.ktor.auth.Principal
import io.ktor.auth.principal
import io.ktor.util.pipeline.PipelineContext


inline fun <reified T : Principal> PipelineContext<*, ApplicationCall>.acquirePrincipal(): T =
    call.principal() ?: throw IllegalStateException("principal should not be null")