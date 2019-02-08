package net.rocketparty.exposed

import org.jetbrains.exposed.sql.Table

object Teams : Table() {

    val id = integer("id").primaryKey()
    val name = varchar("name", 24)
    val avatar = varchar("avatar", 256).nullable()
    val score = integer("score").default(0)

}