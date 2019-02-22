package net.rocketparty.entity

sealed class DomainError {

    object NotFound : DomainError()

    data class BadCredentials(
        val login: String,
        val password: String
    ) : DomainError()

    object AlreadyExists : DomainError()

    object NotCreated : DomainError()

}