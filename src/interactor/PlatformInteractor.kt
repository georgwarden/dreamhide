package net.rocketparty.interactor

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import net.rocketparty.entity.*
import net.rocketparty.repository.CategoryRepository
import net.rocketparty.repository.SolutionRepository
import net.rocketparty.repository.TaskRepository
import net.rocketparty.utils.*

class PlatformInteractor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val solutionRepository: SolutionRepository
) {

    fun getTask(id: Id): Either<DomainError, Task> {
        return taskRepository.findById(id)
            .wrap { DomainError.NotFound }
    }

    fun isSolved(task: Id, by: Team): Boolean {
        return solutionRepository.existsWith(task, by.id)
    }

    fun getTasks(): List<Task> {
        return taskRepository.findAll()
    }

    fun getSolutionsOf(teamId: Id): List<Id> {
        return solutionRepository.findAllTasksSolvedByTeam(teamId)
    }

    suspend fun getCategories(): List<Category> {
        return withContext(Dispatchers.IO) {
            categoryRepository.findAll()
        }
    }

    fun attempt(byTeam: Id, task: Id, flag: String): Either<DomainError, Boolean> {
        return restore {
            if (solutionRepository.existsWith(task, byTeam))
                Left(DomainError.AlreadyExists).verify()
            else
                taskRepository.findById(task).wrap { DomainError.NotFound }
                    .mapRight { it.flag == flag }.verify()
        }
    }

    fun solve(byTeam: Id, task: Id) {
        solutionRepository.save(task, byTeam)
    }

    // subjects to be moved to some AdminInteractor

    suspend fun createTask(model: TaskCreation): Either<DomainError, Task> {
        return withContext(Dispatchers.IO) {
            taskRepository.create(model)
                .wrap { DomainError.NotCreated }
        }
    }

    suspend fun editTask(delta: TaskDelta): Task {
        return withContext(Dispatchers.IO) {
            taskRepository.edit(delta)
        }
    }

    suspend fun deleteTask(id: Id) {
        withContext(Dispatchers.IO) {
            taskRepository.delete(id)
        }
    }

}