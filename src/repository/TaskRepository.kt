package net.rocketparty.repository

import net.rocketparty.entity.Id
import net.rocketparty.entity.Task
import net.rocketparty.entity.TaskCreation
import net.rocketparty.entity.TaskDelta

interface TaskRepository {

    fun findById(id: Int): Task?
    fun findAll(): List<Task>
    fun create(taskModel: TaskCreation): Task?
    fun edit(delta: TaskDelta): Task
    fun delete(id: Id)

}