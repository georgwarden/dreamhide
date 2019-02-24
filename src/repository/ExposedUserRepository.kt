package net.rocketparty.repository

import net.rocketparty.entity.User
import net.rocketparty.exposed.Teams
import net.rocketparty.exposed.Users
import net.rocketparty.exposed.toUser
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedUserRepository : UserRepository {

    override fun findByName(name: String): User? {
        return transaction {
            Users.leftJoin(Teams)
                .select {
                    Users.name eq name
                }.firstOrNull()?.toUser()
        }
    }

    override fun findById(id: Int): User? {
        return transaction {
            Users.leftJoin(Teams)
                .select {
                    Users.id eq id
                }.firstOrNull()?.toUser()
        }
    }

}