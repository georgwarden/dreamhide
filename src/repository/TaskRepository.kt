package net.rocketparty.repository

import net.rocketparty.entity.Id
import net.rocketparty.entity.Task
import net.rocketparty.entity.TaskCreation

interface TaskRepository {

    fun findById(id: Int): Task?
    fun findAll(): List<Task>
    fun create(task: TaskCreation): Id?

}