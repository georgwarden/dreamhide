package net.rocketparty.exposed

import net.rocketparty.entity.Team
import net.rocketparty.entity.User
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toUser(): User {
    return User(
        this[Users.id],
        this[Users.name],
        this[Users.passwordHash],
        Team(
            this[Teams.id],
            this[Teams.name],
            this[Teams.avatar],
            this[Teams.score]
        )
    )
}