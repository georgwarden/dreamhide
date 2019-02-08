package net.rocketparty.dto

import net.rocketparty.entity.User

fun User.toDto(): UserDto {
    val model = this
    return UserDto(
        model.id,
        model.name,
        TeamDto(
            model.team.id,
            model.team.name,
            model.team.avatar,
            model.team.score
        )
    )
}