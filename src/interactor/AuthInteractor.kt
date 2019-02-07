package net.rocketparty.interactor

import net.rocketparty.entity.CommonError
import net.rocketparty.entity.Token
import net.rocketparty.repository.UserRepository
import net.rocketparty.utils.*

class AuthInteractor(
    val userRepository: UserRepository
) {

    fun tryAuthorize(login: String, password: String): Either<CommonError, Token> {
        return userRepository.findByName(login)
            .wrap { CommonError.UserNotFound(login) }
            .flatMap { cur ->
                cur.fold({
                    Left(it)
                }, { user ->
                    retrieve(password == user.passwordHash,
                        { CommonError.BadCredentials(login, password) },
                        { TODO() })
                })
            }
    }

}