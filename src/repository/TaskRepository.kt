package net.rocketparty.repository

import net.rocketparty.entity.Task

interface TaskRepository {

    fun findById(id: Int): Task?
    fun findAll(): List<Task>

}