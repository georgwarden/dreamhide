package net.rocketparty.repository

import net.rocketparty.entity.Id

interface SolutionRepository {

    fun existsWith(taskId: Id, teamId: Id): Boolean
    fun save(taskId: Id, teamId: Id)
    fun findAllTasksSolvedByTeam(teamId: Id): List<Id>

}