package net.rocketparty.repository

import net.rocketparty.entity.Team
import net.rocketparty.entity.User
import net.rocketparty.exposed.Teams
import net.rocketparty.exposed.Users
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedUserRepository : UserRepository {

    override fun findByName(name: String): User? {
        val result = transaction {
            Users.innerJoin(Teams)
                .slice(
                    Users.id, Users.name, Users.passwordHash,
                    Teams.id, Teams.name, Teams.avatar
                )
                .select {
                Users.name eq name
            }.firstOrNull()
        }
        return result?.let { row ->
            User(
                row[Users.id],
                row[Users.name],
                row[Users.passwordHash],
                Team(
                    row[Teams.id],
                    row[Teams.name],
                    row[Teams.avatar],
                    row[Teams.score]
                )
            )
        }
    }

}