package net.rocketparty.dto

import net.rocketparty.dto.model.*
import net.rocketparty.dto.model.TaskDeltaDto
import net.rocketparty.entity.*

fun User.toDto(): UserDto {
    return UserDto(
        this.id,
        this.name
    )
}

operator fun UserDto.plus(team: TeamDto): UserTeamDto {
    return UserTeamDto(
        this,
        team
    )
}

operator fun TeamDto.plus(user: UserDto): UserTeamDto {
    return user + this
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

fun Task.toInfo(): BasicTaskInfoDto {
    return BasicTaskInfoDto(
        this.id,
        this.name,
        this.reward,
        this.category.toDto()
    )
}

fun Task.toDescription(): TaskDescriptionDto {
    return TaskDescriptionDto(
        this.description,
        this.attachments.map { it.content }
    )
}

fun TaskCreationDto.toEntity(): TaskCreation {
    return TaskCreation(
        this.title,
        this.description,
        this.reward,
        this.categoryId,
        this.flag,
        this.attachments
    )
}

fun Task.toEssentials(solved: Boolean): TaskEssentialsDto {
    return TaskEssentialsDto(
        this.toInfo(),
        solved
    )
}

fun TaskDeltaDto.toEntity(): TaskDelta {
    return TaskDelta(
        this.id,
        this.title,
        this.description,
        this.categoryId,
        this.reward,
        this.attachments
    )
}