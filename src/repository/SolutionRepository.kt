package net.rocketparty.repository

import net.rocketparty.entity.Id

interface SolutionRepository {

    fun existsWith(taskId: Id, teamId: Id): Boolean

}