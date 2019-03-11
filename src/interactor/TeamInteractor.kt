package net.rocketparty.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.rocketparty.entity.DomainError
import net.rocketparty.entity.Team
import net.rocketparty.repository.TeamRepository
import net.rocketparty.utils.Either
import net.rocketparty.utils.wrap

class TeamInteractor(
    private val teamRepository: TeamRepository
) {

    fun getTeam(id: Int): Either<DomainError, Team> {
        return teamRepository.findById(id)
            .wrap { DomainError.NotFound }
    }

    suspend fun getAllTeams(): List<Team> {
        return withContext(Dispatchers.IO) {
            teamRepository.getAll()
        }
    }

}