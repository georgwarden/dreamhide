package net.rocketparty.interactor

import net.rocketparty.entity.DomainError
import net.rocketparty.entity.Token
import net.rocketparty.entity.User
import net.rocketparty.repository.UserRepository
import net.rocketparty.utils.*

class AuthInteractor(
    private val userRepository: UserRepository
) {

    private val hashingContext = Md5Context()

    fun tryAuthorize(login: String, password: String): Either<DomainError, User> {
        return userRepository.findByName(login)
            .wrap { DomainError.NotFound }
            .flatMap { cur ->
                cur.fold({
                    Left(it)
                }, { user ->
                    retrieve(hashingContext.run { password.hashed() == user.passwordHash },
                        { DomainError.BadCredentials(login, password) },
                        { user })
                })
            }
    }

}