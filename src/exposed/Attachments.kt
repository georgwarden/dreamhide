package net.rocketparty.exposed

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Attachments : Table() {

    val id = integer("id").autoIncrement().primaryKey()
    val content = varchar("content", 256)
    val task = integer("task_id").references(Tasks.id, onDelete = ReferenceOption.CASCADE).index()

}