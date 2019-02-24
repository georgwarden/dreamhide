package net.rocketparty.repository

import net.rocketparty.entity.Id
import net.rocketparty.entity.Task
import net.rocketparty.entity.TaskCreation
import net.rocketparty.entity.TaskDelta
import net.rocketparty.exposed.*
import org.jetbrains.exposed.sql.*
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

    override fun create(taskModel: TaskCreation): Task? {
        return transaction {
            val taskId = Tasks.insert {
                it[name] = taskModel.title
                it[description] = taskModel.description
                it[reward] = taskModel.reward
                it[category] = taskModel.categoryId
                it[flag] = taskModel.flag
            } get Tasks.id

            taskId?.let { task ->
                Attachments.batchInsert(taskModel.attachments) { content ->
                    this[Attachments.content] = content
                    this[Attachments.task] = task
                }

                val attachments = Attachments.select {
                    Attachments.task eq task
                }.map { row -> row.toAttachment() }
                (Tasks innerJoin Categories).select {
                    Tasks.id eq task
                }.first().toTask(attachments)
            }
        }
    }

    override fun edit(delta: TaskDelta): Task {
        return transaction {
            Tasks.update(
                where = {
                    Tasks.id eq delta.id
                }
            ) {
                delta.title?.let { title -> it[Tasks.name] = title }
                delta.description?.let { description -> it[Tasks.description] = description }
                delta.reward?.let { reward -> it[Tasks.reward] = reward }
                delta.categoryId?.let { category -> it[Tasks.category] = category }
            }

            delta.attachments?.let { attachments ->
                Attachments.deleteWhere {
                    Attachments.task eq delta.id
                }

                Attachments.batchInsert(attachments) { content ->
                    this[Attachments.content] = content
                    this[Attachments.task] = delta.id
                }
            }

            val attachments = Attachments.select {
                Attachments.task eq delta.id
            }.map { row -> row.toAttachment() }
            (Tasks innerJoin Categories).select {
                Tasks.id eq delta.id
            }.first().toTask(attachments)
        }
    }

    private fun pickTask(taskId: Id) {

    }
}