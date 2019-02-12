package net.rocketparty.dto

import net.rocketparty.entity.Category
import net.rocketparty.entity.Task
import net.rocketparty.entity.Team
import net.rocketparty.entity.User

fun User.toDto(): UserDto {
    val model = this
    return UserDto(
        model.id,
        model.name,
        model.team.toDto()
    )
}

fun Team.toDto(): TeamDto {
    return TeamDto(
        this.id,
        this.name,
        this.avatar,
        this.score
    )
}

fun Category.toDto(): CategoryDto {
    return CategoryDto(
        this.id,
        this.name
    )
}

fun Task.toDto(solved: Boolean = false): TaskDto {
    return TaskDto(
        this.id,
        this.name,
        this.description,
        this.category.toDto(),
        this.reward,
        this.attachments.map { it.content },
        solved
    )
}