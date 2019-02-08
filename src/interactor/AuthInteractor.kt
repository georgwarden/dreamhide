package net.rocketparty.interactor

import net.rocketparty.entity.CommonError
import net.rocketparty.entity.Token
import net.rocketparty.repository.UserRepository
import net.rocketparty.utils.*

class AuthInteractor(
    private val userRepository: UserRepository
) {

    private val hashingContext = Md5Context()

    fun tryAuthorize(login: String, password: String): Either<CommonError, Token> {
        return userRepository.findByName(login)
            .wrap { CommonError.UserNotFound }
            .flatMap { cur ->
                cur.fold({
                    Left(it)
                }, { user ->
                    retrieve(hashingContext.run { password.hashed() == user.passwordHash },
                        { CommonError.BadCredentials(login, password) },
                        { generateJwt(user.id) })
                })
            }
    }

}