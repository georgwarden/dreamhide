package net.rocketparty.repository

import net.rocketparty.entity.User
import net.rocketparty.exposed.Teams
import net.rocketparty.exposed.Users
import net.rocketparty.exposed.toUser
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
        return result?.toUser()
    }

    override fun findById(id: Int): User? {
        val result = transaction {
            Users.innerJoin(Teams)
                .slice(
                    Users.id, Users.name, Users.passwordHash,
                    Teams.id, Teams.name, Teams.avatar, Teams.score
                )
                .select {
                    Users.id eq id
                }.firstOrNull()
        }
        return result?.toUser()
    }

}