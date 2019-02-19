package net.rocketparty.repository

import net.rocketparty.entity.Team
import net.rocketparty.exposed.Teams
import net.rocketparty.exposed.toTeam
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedTeamRepository : TeamRepository {

    override fun findById(id: Int): Team? {
        return transaction {
            Teams.select {
                Teams.id eq id
            }.firstOrNull()?.toTeam()
        }
    }

    override fun getAll(): List<Team> {
        return transaction {
            Teams.selectAll()
                .map { row -> row.toTeam() }
        }
    }

}