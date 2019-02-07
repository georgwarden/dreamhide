package net.rocketparty.entity

sealed class CommonError {

    data class UserNotFound(
        val login: String
    ) : CommonError()

    data class BadCredentials(
        val login: String,
        val password: String
    ) : CommonError()

}