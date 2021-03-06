package net.rocketparty.exposed

import net.rocketparty.entity.*
import org.jetbrains.exposed.sql.ResultRow

fun ResultRow.toUser(): User {
    return User(
        this[Users.id],
        this[Users.name],
        this[Users.passwordHash],
        this.toTeam(),
        this[Users.isAdmin]
    )
}

@Suppress("SENSELESS_COMPARISON")
fun ResultRow.toTeam(): Team? {
    return this.takeIf { row -> row[Teams.id] != null }
        ?.run {
            Team(
                this[Teams.id],
                this[Teams.name],
                this[Teams.avatar],
                this[Teams.score]
            )
        }
}

fun ResultRow.toTask(attachments: List<Attachment>): Task {
    return Task(
        this[Tasks.id],
        this[Tasks.name],
        this[Tasks.description],
        this[Tasks.reward],
        this.toCategory(),
        this[Tasks.flag],
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