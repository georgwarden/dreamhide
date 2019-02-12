package net.rocketparty.interactor

import net.rocketparty.entity.DomainError
import net.rocketparty.entity.User
import net.rocketparty.repository.UserRepository
import net.rocketparty.utils.Either
import net.rocketparty.utils.wrap

class UserInteractor(
    private val repository: UserRepository
) {

    fun getUser(id: Int): Either<DomainError, User> {
        return repository.findById(id)
            .wrap { DomainError.NotFound }
    }

}