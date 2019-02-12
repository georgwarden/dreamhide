package net.rocketparty.interactor

import net.rocketparty.entity.*
import net.rocketparty.repository.CategoryRepository
import net.rocketparty.repository.SolutionRepository
import net.rocketparty.repository.TaskRepository
import net.rocketparty.utils.Either
import net.rocketparty.utils.wrap

class PlatformInteractor(
    private val taskRepository: TaskRepository,
    private val categoryRepository: CategoryRepository,
    private val solutionRepository: SolutionRepository
) {

    fun getTask(id: Id): Either<DomainError, Task> {
        return taskRepository.findById(id)
            .wrap { DomainError.NotFound }
    }

    fun isSolved(task: Id, by: User): Boolean {
        return solutionRepository.existsWith(task, by.team.id)
    }

    fun getTasks(): List<Task> {
        return taskRepository.findAll()
    }

    fun getCategories(): List<Category> {
        return categoryRepository.findAll()
    }

    fun attempt(task: Id, flag: String): Either<DomainError, Boolean> {
        return taskRepository.findById(task)
            .wrap { DomainError.NotFound }
            .mapRight { it.flag == flag }
    }

    fun solve(byTeam: Id, task: Id) {
        solutionRepository.save(task, byTeam)
    }

}