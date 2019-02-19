package net.rocketparty.exposed

import org.jetbrains.exposed.sql.Table

object Tasks: Table() {

    val id = integer("id").autoIncrement().primaryKey()
    val name = varchar("name", 24)
    val description = varchar("description", 1000)
    val reward = integer("reward")
    val category = integer("category_id").references(Categories.id)
    val flag = varchar("flag", 32)

}