package net.rocketparty.repository

import net.rocketparty.entity.Id
import net.rocketparty.entity.Task
import net.rocketparty.entity.TaskCreation
import net.rocketparty.exposed.*
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.selectAll
import org.jetbrains.exposed.sql.transactions.transaction

class ExposedTaskRepository : TaskRepository {

    override fun findById(id: Int): Task? {
        return transaction {
            val attachments = Attachments.select {
                Attachments.task eq id
            }.map { row -> row.toAttachment() }
            (Tasks innerJoin Categories).select {
                Tasks.id eq id
            }.firstOrNull()?.toTask(attachments)
        }
    }

    override fun findAll(): List<Task> {
        return transaction {
            val attachments = Attachments.selectAll()
                .orderBy(Attachments.task)
                .groupBy { it[Attachments.task] }
            (Tasks innerJoin Categories).selectAll()
                .map { row ->
                    val id = row[Tasks.id]
                    row.toTask(
                        attachments[id]
                            ?.map { it.toAttachment() }
                            ?: emptyList())
                }
        }
    }

    override fun create(task: TaskCreation): Id? {
        return transaction {
            Tasks.insert {
                it[name] = task.title
                it[description] = task.description
                it[reward] = task.reward
                it[category] = task.categoryId
                it[flag] = task.flag
            } get Tasks.id
        }
    }

}