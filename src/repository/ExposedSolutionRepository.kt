package net.rocketparty.repository

import net.rocketparty.entity.Id
import net.rocketparty.exposed.Solutions
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedSolutionRepository : SolutionRepository {

    override fun existsWith(taskId: Id, teamId: Id): Boolean {
        return transaction {
            Solutions.select {
                (Solutions.task eq taskId) and
                        (Solutions.team eq teamId)
            }
        }.any()
    }

    override fun save(taskId: Id, teamId: Id) {
        transaction {
            Solutions.insert {
                it[Solutions.task] = taskId
                it[Solutions.team] = teamId
            }
        }
    }

}