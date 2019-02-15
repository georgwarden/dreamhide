package net.rocketparty.dto

import net.rocketparty.entity.Category
import net.rocketparty.entity.Task
import net.rocketparty.entity.Team
import net.rocketparty.entity.User

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

fun Task.toInfo(solved: Boolean = false): BasicTaskInfoDto {
    return BasicTaskInfoDto(
        this.id,
        this.name,
        this.reward,
        this.category.toDto(),
        solved
    )
}

fun Task.toDescription(): TaskDescriptionDto {
    return TaskDescriptionDto(
        this.description,
        this.attachments.map { it.content }
    )
}

operator fun BasicTaskInfoDto.plus(desc: TaskDescriptionDto): FullTaskInfoDto {
    return FullTaskInfoDto(
        this,
        desc
    )
}

operator fun TaskDescriptionDto.plus(info: BasicTaskInfoDto): FullTaskInfoDto {
    return info + this
}