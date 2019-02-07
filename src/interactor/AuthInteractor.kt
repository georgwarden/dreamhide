package net.rocketparty.interactor

import net.rocketparty.entity.User
import net.rocketparty.repository.UserRepository
import net.rocketparty.utils.Either

class AuthInteractor(
    val userRepository: UserRepository
) {

    fun tryAuthorize(login: String, password: String): Either<Error, User> {
        TODO()
    }

}