package net.rocketparty.entity

sealed class CommonError {

    object NotFound : CommonError()

    data class BadCredentials(
        val login: String,
        val password: String
    ) : CommonError()

}