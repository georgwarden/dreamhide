package net.rocketparty.exposed

import org.jetbrains.exposed.sql.ReferenceOption
import org.jetbrains.exposed.sql.Table

object Solutions : Table() {

    val task = integer("task_id").references(Tasks.id, onDelete = ReferenceOption.CASCADE).index("solinx")
    val team = integer("team_id").references(Teams.id, onDelete = ReferenceOption.CASCADE)

}