package net.rocketparty.exposed

import net.rocketparty.entity.*
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toUser(): User {
    return User(
        this[Users.id],
        this[Users.name],
        this[Users.passwordHash],
        this.toTeam()
    )
}

fun ResultRow.toTeam(): Team {
    return Team(
        this[Teams.id],
        this[Teams.name],
        this[Teams.avatar],
        this[Teams.score]
    )
}

fun ResultRow.toTask(attachments: List<Attachment>): Task {
    return Task(
        this[Tasks.id],
        this[Tasks.name],
        this[Tasks.description],
        this[Tasks.reward],
        this.toCategory(),
        attachments
    )
}

fun ResultRow.toAttachment(): Attachment {
    return Attachment(
        this[Attachments.id],
        this[Attachments.content]
    )
}

fun ResultRow.toCategory(): Category {
    return Category(
        this[Categories.id],
        this[Categories.name]
    )
}