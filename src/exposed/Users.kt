package net.rocketparty.exposed

import org.jetbrains.exposed.sql.Table

object Users : Table() {

    val id = integer("id").primaryKey()
    val name = varchar("name", 24)
    val passwordHash = varchar("pswd_hash", 32)
    val teamId = integer("team_id").references(Teams.id).nullable()

}