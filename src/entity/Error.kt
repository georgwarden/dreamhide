package net.rocketparty.entity

sealed class Error {

    data class UserNotFound(
        val login: String
    ) : Error()

}