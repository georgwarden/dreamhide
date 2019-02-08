package net.rocketparty.dto

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