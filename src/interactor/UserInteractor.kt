package net.rocketparty.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.rocketparty.entity.DomainError
import net.rocketparty.entity.User
import net.rocketparty.repository.UserRepository
import net.rocketparty.utils.Either
import net.rocketparty.utils.wrap

class UserInteractor(
    private val repository: UserRepository
) {

    suspend fun getUser(id: Int): Either<DomainError, User> {
        return withContext(Dispatchers.IO) {
            repository.findById(id)
                .wrap { DomainError.NotFound }
        }
    }

}