package net.rocketparty.dto

import com.google.gson.annotations.SerializedName

data class UserTeamDto(
    @SerializedName("user")
    val user: UserDto,
    @SerializedName("team")
    val team: TeamDto
)