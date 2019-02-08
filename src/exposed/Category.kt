package net.rocketparty.exposed

import org.jetbrains.exposed.sql.Table

object Category : Table() {

    val id = integer("id").primaryKey()
    val name = varchar("name", 24)

}