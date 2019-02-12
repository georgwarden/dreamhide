package net.rocketparty.repository

import net.rocketparty.entity.Task
import net.rocketparty.exposed.Attachments
import net.rocketparty.exposed.Tasks
import net.rocketparty.exposed.toAttachment
import net.rocketparty.exposed.toTask
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedTaskRepository : TaskRepository {

    override fun findById(id: Int): Task? {
        return transaction {
            val attachments = Attachments.select {
                Attachments.task eq id
            }.map { row -> row.toAttachment() }
            Tasks.select {
                Tasks.id eq id
            }.firstOrNull()?.toTask(attachments)
        }
    }

    override fun findAll(): List<Task> {
        return transaction {
            val attachments = Attachments.selectAll()
                .orderBy(Attachments.task)
                .groupBy { it[Attachments.task] }
            Tasks.selectAll()
                .map { row ->
                    val id = row[Tasks.id]
                    row.toTask(
                        attachments[id]
                            ?.map { it.toAttachment() }
                            ?: emptyList())
                }
        }
    }

}