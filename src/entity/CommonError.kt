package net.rocketparty.entity

sealed class CommonError {

    object UserNotFound : CommonError()

    data class BadCredentials(
        val login: String,
        val password: String
    ) : CommonError()

}