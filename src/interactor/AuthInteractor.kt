package net.rocketparty.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.rocketparty.entity.DomainError
import net.rocketparty.entity.User
import net.rocketparty.repository.UserRepository
import net.rocketparty.utils.*

class AuthInteractor(
    private val userRepository: UserRepository
) {

    private val hashingContext = Md5Context()

    suspend fun tryAuthorize(login: String, password: String): Either<DomainError, User> {
        return withContext(Dispatchers.IO) {
            userRepository.findByName(login)
        }
            .wrap { DomainError.NotFound }
            .flatMap { cur ->
                cur.fold({
                    Left(it)
                }, { user ->
                    retrieve(
                        withContext(Dispatchers.Default) {
                            hashingContext.run {
                                password.hashed() == user.passwordHash
                            }
                        },
                        { DomainError.BadCredentials(login, password) },
                        { user })
                })
            }
    }

}